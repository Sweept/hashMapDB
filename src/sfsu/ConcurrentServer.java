package sfsu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A multithreaded server that listens for a fixed number of clients simultaneously. It does not do much, but it is much
 * more fun that a single threaded server.
 *
 * Implements runnable so that it can be executed in a thread or as a future. This allows for multiple servers on
 * different ports.
 */
public class ConcurrentServer implements Runnable {

    // The port that this server will bind to.
    private final int port;
    //i created a db variable to hold the key-value pairs
    private static final Database db = new Database();

    /**
     * Creates a server in the specified port. The server will not start to listen until run is called.
     */
    public ConcurrentServer(int port) {
        this.port = port;
    }

    /**
     * Runs the server on a thread pool.
     */
    @Override
    public void run() {
        multiThreadListen();
    }

    /**
     * Parses and processes client requests using network sockets.
     */
    static class ClientParser implements Runnable {

        private final Socket socket;

        ClientParser(Socket socket) {
            this.socket = socket;
        }

        /**
         * Parses a client request form a socket and pretends to sleep to make things interesting. Sends a dummy
         * response and closes the socket.
         */
        @Override
        public void run() {
            try {
                // Parse the client request directly from the socket. Thank you protobuf.
            	DatabaseProtos.Response res = DatabaseProtos.Response.newBuilder()
            			.build();
                DatabaseProtos.Request request = DatabaseProtos.Request.parseDelimitedFrom(socket.getInputStream());
                System.out.println(String.format("Server: Received request: %s\n", request));
                
                //if parsing the request to find which operation client is wanting to do, i.e. get,put, or delete
                DatabaseProtos.Request.OperationType op = request.getOperation();
            	String key =request.getKey();
                if( op == DatabaseProtos.Request.OperationType.GET )
                {
                	//
                	String value = db.get(request.getKey());
                	res = DatabaseProtos.Response.newBuilder()
                            .setValue(value)
                            .setKey(key)
                            .build();
                }else if ( op == DatabaseProtos.Request.OperationType.PUT )
                {
                	String value = request.getValue();
                	db.put(key, value);
                	res = DatabaseProtos.Response.newBuilder()
                            .setValue(value)
                            .setKey(key)
                            .build();
                }else if ( op == DatabaseProtos.Request.OperationType.DELETE )
                {
                	db.delete(key);
                	res = DatabaseProtos.Response.newBuilder()
                            .setKey(key)
                            .build();
                }
                
                
                // Pretend some heavy lifting is going on.
                Thread.sleep(10);

                // Create a dummy response and send it to the client.
                //deleted dummy response since I created one above
                res.writeDelimitedTo(socket.getOutputStream());
                System.out.println(String.format("Server: Sent response: %s\n", res));

                // This interaction is done. A better server would allow the client to request other things in the same
                // connection. Not here.
                socket.close();
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Creates a fixed size thread pool and listens for clients on a given port. All clients will be accepted and the
     * processing will be submitted to the thread pool.
     */
    private void multiThreadListen() {
        ExecutorService serverThreads = Executors.newFixedThreadPool(10);

        try {
            // Create a server socket for the specified port.
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println(String.format("Server: Server on port %d ready\n", port));

            // Listen for clients until interrupted.
            while (true) {
                System.out.println("Server: Accepting the next client\n");
                Socket clientSocket = serverSocket.accept();

                // Create a new future using the ClientParser. The future will start to run as soon as there is a thread
                // available in the pool.
                serverThreads.submit(new ClientParser(clientSocket));
                // Resume to accept incoming clients immediatly.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts a demo server.
     *
     * Example:
     *  java -cp lib/*:out/production/Database sfsu.ConcurrentServer 1080
     *
     * @param args the first element must be a port number
     * @throws Exception whenever anything bad happens, good enough for a quick test.
     */
    public static void main(String[] args) throws Exception {
        // Verify the command line arguments.
        if (args.length != 1) {
            System.out.println("Usage: SingleThreadedServer <port>");
            return;
        }
        // Parse the port number
        int port = Integer.parseInt(args[0]);

        // Create a thread for a server on a single port. Note that the port itself can receive multiple clients and
        // thus that server is concurrent. What we do not, because we do not need to, is to run multiple servers, each
        // on different ports. Most services do not have to do this (but can when needed, such as a webserver.)
        ExecutorService serverThreads = Executors.newSingleThreadExecutor();
        ConcurrentServer server = new ConcurrentServer(port);
        Future serverFuture = serverThreads.submit(server);
        serverFuture.get();
    }
}
