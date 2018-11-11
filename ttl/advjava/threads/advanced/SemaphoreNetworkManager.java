package ttl.advjava.threads.advanced;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Semaphore;

/**
 * SemaphoreOrderBoard represents a basic order board in a restaraunt.
 * 
 * 
 * @author developintelligence llc
 * @version 1.0
 */
public class SemaphoreNetworkManager {

	private Semaphore connectionSem;
	private int maxOpenConnections;
	private ConnectionFactory connFactory;

	/**
	 * Create a new SemaphoreOrderBoard, initializing the shared list.
	 */
	public SemaphoreNetworkManager(int maxOpenConnections) {
		this.maxOpenConnections = maxOpenConnections;
		connectionSem = new Semaphore(maxOpenConnections);
		connFactory = new ConnectionFactory("Connection Factory");
	}

	/**
	 * add an order to the order board
	 * 
	 * @param toBeProcessed
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void sendTo(String host, int port, String message) throws InterruptedException, UnknownHostException, IOException {
		Socket socket = null;
		PrintWriter pw = null;
		System.out.println("Going to aquire for " + message);
		connectionSem.acquire(); // decrease permits by one
		System.out.println("Aquired Sem for " + message);
		try {
			socket = connFactory.getConnection(host, port);
			pw = new PrintWriter(socket.getOutputStream());
			pw.println(message);
			pw.flush();
			
		} finally {
			if(socket != null) {
				socket.close();
			}
			if(pw != null) {
				pw.close();
			}
			connectionSem.release();
		}
	}
	
	public static void main(String [] args) {
		SemaphoreNetworkManager manager = new SemaphoreNetworkManager(3);
		
		Runnable r = () -> { 
			try {
				manager.sendTo("localhost", 10000, Thread.currentThread().getName());
			}
			catch(Exception e) {e.printStackTrace();}
		};
		
		for(int i = 0; i < 10; i++) {
			Thread th = new Thread(r, i + "");
			th.start();
		}

	}
}

class ConnectionFactory
{
	private String name;
	
	public ConnectionFactory(String name) {
		this.name = name;
	}

	public Socket getConnection(String host, int port) throws UnknownHostException, IOException {
		return new Socket(host, port);
	}
}
