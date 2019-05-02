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
    private static Vector<Integer> keys = new Vector<Integer>(100, 2);

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
	            for (int i = 0; i < 100; i++) {
		            // Create a socket and attempt to connect.
		            int key = (int) (Math.random() * 100);
		            if (keys.indexOf(key) == -1) {
			            System.out.println("Delete: No - " + i + ": key: " + key + " Not found");
			            TimeUnit.MILLISECONDS.sleep(100);
		            	continue;
		            }
		            Socket clientSocket = new Socket(serverAddress, port);
		            System.out.println("Delete: No - " + i + "Executing : " + name + "for key - " + key);
		            TimeUnit.MILLISECONDS.sleep(10);
		            // Create a dummy request. None of the arguments are important, as int as the request is syntactically valid.
		            keys.remove(keys.indexOf(key));
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.DELETE)
		                    .setKey(Integer.toString(key))
		                    .build();
	
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
		            // Close the sockets and finish.
		            clientSocket.close();
	            }

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
	            for (int i = 0; i < 100; i++) {
		            int key = (int) (Math.random() * 100);
		            if (keys.indexOf(key) == -1) {
			            System.out.println("Get: No - " + i + ": key: " + key + " Not found");
			            TimeUnit.MILLISECONDS.sleep(100);
		            	continue;
		            }
		            Socket clientSocket = new Socket(serverAddress, port);
		            System.out.println("Get: No - " + i + "Executing : " + "key: " + key);
		            TimeUnit.MILLISECONDS.sleep(10);
		            int value = key * 33;
		            // Create a dummy request. None of the arguments are important, as int as the request is syntactically valid.
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.GET)
		                    .setKey(Integer.toString(key))
		                    .build();
	
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Get: No - " + i + "Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
		            int recd = Integer.parseInt(response.getValue());
		            if(recd == value) {
			            System.out.println("Get: No - " + i + "database is fine");
		            } else {
			            System.out.println("Get: No - " + i + "database is corrupt key - " + key + " recd val = " + recd + " expcted value " + value);
		            }
		            clientSocket.close();
	            }

	            // Close the sockets and finish.
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
	            for (int i = 0; i < 100; i++) {
		            int key = i;
		            if (keys.indexOf(key) != -1) {
			            System.out.println("Put: No - " + i + ": key: " + key + " Already there");
			            TimeUnit.MILLISECONDS.sleep(100);
		            	continue;
		            }
		            Socket clientSocket = new Socket(serverAddress, port);
		            System.out.println("Put: No - " + i + "Executing : " + "key: " + key);
		            TimeUnit.MILLISECONDS.sleep(10);
		            int value = key * 33;
		            // Create a dummy request. None of the arguments are important, as int as the request is syntactically valid.
		            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
		                    .setOperation(DatabaseProtos.Request.OperationType.PUT)
		                    .setKey(Integer.toString(key))
		                    .setValue(Integer.toString(value))
		                    .build();
		            // Write the request message to the socket.
		            request.writeDelimitedTo(clientSocket.getOutputStream());
		            System.out.println("Request sent, waiting for response.");
	
		            // Receive and parse a response from the server.
		            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
		            System.out.println(String.format("Response received: %s\n", response));
		        	
		            keys.addElement(key);
	
		            // Close the sockets and finish.
		            clientSocket.close();
	            }

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
        // Parse the IP and port
        serverAddress = args[0];
        port = Integer.parseInt(args[1]);
        if (args.length != 2) {
            System.out.println("Usage: Client <IP> <port>");
            return;
        }
        for (int i = 0; i < 100; i++) {
            int key = i;
            if (keys.indexOf(key) != -1) {
	            System.out.println("Put: No - " + i + ": key: " + key + " Already there");
            	continue;
            }
            Socket clientSocket = new Socket(serverAddress, port);
            System.out.println("Put: No - " + i + "Executing : " + "key: " + key);
            TimeUnit.MILLISECONDS.sleep(10);
            int value = key * 33;
            // Create a dummy request. None of the arguments are important, as int as the request is syntactically valid.
            DatabaseProtos.Request request = DatabaseProtos.Request.newBuilder()
                    .setOperation(DatabaseProtos.Request.OperationType.PUT)
                    .setKey(Integer.toString(key))
                    .setValue(Integer.toString(value))
                    .build();
            // Write the request message to the socket.
            request.writeDelimitedTo(clientSocket.getOutputStream());
            System.out.println("Request sent, waiting for response.");

            // Receive and parse a response from the server.
            DatabaseProtos.Response response = DatabaseProtos.Response.parseDelimitedFrom(clientSocket.getInputStream());
            System.out.println(String.format("Response received: %s\n", response));
        	
            keys.addElement(key);

            // Close the sockets and finish.
            clientSocket.close();
        }

        ExecutorService putThreads = Executors.newFixedThreadPool(1);
        ExecutorService getThreads = Executors.newFixedThreadPool(1);
        ExecutorService deleteThreads = Executors.newFixedThreadPool(1);
        
        
        
        Collection<Future<?>> tasks = new LinkedList<Future<?>>();

        Future future = putThreads.submit(new putTask("put"));
        tasks.add(future);
        Future future1 = getThreads.submit(new getTask("get"));
        tasks.add(future1);
        Future future2 = deleteThreads.submit(new deleteTask("delete"));
        tasks.add(future2);
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
