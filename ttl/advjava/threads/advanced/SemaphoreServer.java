package ttl.advjava.threads.advanced;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SemaphoreServer {

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(10000);
		
		while(true) {
			System.out.println("Server ready, going to accept");
			Socket s = ss.accept();
			Scanner scanner = new Scanner(s.getInputStream());
			String line;
			while(true) {
				try {
					line = scanner.nextLine();
					System.out.println("Server got " + line);
				}
				catch(NoSuchElementException e) {
					scanner.close();
					s.close();
					break;
				}
			}
		}
	}
}
