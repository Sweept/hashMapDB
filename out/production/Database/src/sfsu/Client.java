package sfsu;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Verifies that the database server is able to receive a client request. Prints the response from the server.
 *
 * Invocation and arguments:
 *   java -cp lib/*:out/src/sfsu sfsu.Client <IP> <port>
 */
public class Client {
    private static String serverAddress;
    private static int port;
    private static Vector<Long> keys = new Vector<Long>(100, 2);

    /**
     * Connects to the sever process. Sends a dummy request and receives a response.
     * @throws Exception whenever anything bad happens. This avoids the need to wrap most instructions in try/catch
     *      blocks. Good enough for a dummy tester.
     */
    static class deleteTask implements Runnable {
	    private String name;
	 
	    public deleteTask(String name) {
	        this.name = name;
	    }
	 
	    public String getName() {
	        return name;
	    }
	 
	    public void run() {
	        try {
	            // Create a socket and attempt to connect.
	            Socket clientSocket = new Socket(serverAddress, port);
	            for (int i = 0; i < 100; i++) {
		            Long key = (long) (Math.random() * 100);
		            if (keys.indexOf(key) == -1) {
		            	continue;
		            }
		            System.out.println("Executing : " + name);
		            TimeUnit.SECONDS.sleep(10);
		            Long value = key * 33;
		            // Create a dummy request. None of the arguments are important, as long as the request is syntactically valid.
		            keys.remove(keys.indexOf(key));
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.DELETE)
		                    .setKey(key.toString())
		                    .build();
	
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
		            if(response.getValue() == value.toString()) {
			            System.out.println("database is fine");
		            } else {
			            System.out.println("database is corrupt");
		            }
	            }

	            // Close the sockets and finish.
	            clientSocket.close();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}	
	static class getTask implements Runnable {
	    private String name;
	 
	    public getTask(String name) {
	        this.name = name;
	    }
	 
	    public String getName() {
	        return name;
	    }
	 
	    public void run() {
	        try {
	            // Create a socket and attempt to connect.
	            Socket clientSocket = new Socket(serverAddress, port);
	            for (int i = 0; i < 100; i++) {
		            Long key = (long) (Math.random() * 100);
		            if (keys.indexOf(key) == -1) {
		            	continue;
		            }
		            System.out.println("Executing : " + name);
		            TimeUnit.SECONDS.sleep(10);
		            Long value = key * 33;
		            // Create a dummy request. None of the arguments are important, as long as the request is syntactically valid.
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.GET)
		                    .setKey(key.toString())
		                    .build();
	
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
		            if(response.getValue() == value.toString()) {
			            System.out.println("database is fine");
		            } else {
			            System.out.println("database is corrupt");
		            }
	            }

	            // Close the sockets and finish.
	            clientSocket.close();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}	 
	static class putTask implements Runnable {
	    private String name;
	 
	    public putTask(String name) {
	        this.name = name;
	    }
	 
	    public String getName() {
	        return name;
	    }
	 
	    public void run() {
	        try {
	            // Create a socket and attempt to connect.
	            Socket clientSocket = new Socket(serverAddress, port);
	            for (int i = 0; i < 100; i++) {
		            Long key = (long) (Math.random() * 100);
		            if (keys.indexOf(key) != -1) {
		            	continue;
		            }
		            System.out.println("Executing : " + name);
		            TimeUnit.SECONDS.sleep(1);
		            Long value = key * 33;
		            // Create a dummy request. None of the arguments are important, as long as the request is syntactically valid.
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.PUT)
		                    .setKey(key.toString())
		                    .setValue(value.toString())
		                    .build();
	
		            keys.addElement(key);
	
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
	            }

	            // Close the sockets and finish.
	            clientSocket.close();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        } catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: Client <IP> <port>");
            return;
        }

        int thredaNo = 1;
        // Parse the IP and port
        serverAddress = args[0];
        port = Integer.parseInt(args[1]);
        ExecutorService putThreads = Executors.newFixedThreadPool(20);
        ExecutorService getThreads = Executors.newFixedThreadPool(100);
        ExecutorService deleteThreads = Executors.newFixedThreadPool(20);
        
        
        
        Collection<Future<?>> tasks = new LinkedList<Future<?>>();

        Future future = putThreads.submit(new putTask("put"));
        tasks.add(future);
        future = getThreads.submit(new getTask("get"));
        tasks.add(future);
        future = deleteThreads.submit(new deleteTask("delete"));
        tasks.add(future);
        // wait for tasks completion
        for (Future currTask : tasks) {
                try {
                    currTask.get();
                } catch (Throwable thrown) {
                	System.out.println("Error while waiting for thread completion");
                }
            }
        return;
    }

}
