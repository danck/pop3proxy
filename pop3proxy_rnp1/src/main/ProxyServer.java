package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class ProxyServer {

	public final static int PORT = 11006;
	public final static Logger infoLogger = Logger.getLogger("info");
	public final static Logger errorLogger = Logger.getLogger("errors");

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{	
		initialize();
		
		Thread fetcher = new Thread(new Client());
		fetcher.start();
		ExecutorService pool = Executors.newFixedThreadPool(10);

		try {
			ServerSocket ssocket = new ServerSocket(PORT);

			try {
				infoLogger.log(Level.INFO, "Starting Server on Port " + PORT);
				while(true) {
					Socket s = ssocket.accept();
					infoLogger.log(Level.INFO, "Connecting to " + s.getRemoteSocketAddress());
					Callable<Void> task = new ClientHandlerTask(new Pop3ServerContext(s));
					pool.submit(task);
				}
			} catch (IOException e) {
				errorLogger.log(Level.SEVERE, "Failed to initialize Connection: " + e.getMessage(), e);
			} catch (RuntimeException e) {
				errorLogger.log(Level.SEVERE, "Failed to initialize Connection: " + e.getMessage(), e);
			}
		} catch (IOException e) {
			errorLogger.log(Level.SEVERE, "Failed to initialize server: " + e.getMessage(), e);
		} catch (RuntimeException e) {
			errorLogger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
		}
	}
	
	private static void initialize()
	{
		Account.add("manfred", "Wurst");
		Account a = Account.getByCredentials("manfred", "Wurst");
		Mailbox.add(a);
		Mailbox mbox = Mailbox.get(a);
		mbox.addMessage("From: George Jones <Jones@Host.Net> \r\n Subject  :  Rhabarbaraba \r\n Sender: Jones@Host\r\nReply-To: The Committee: Jones@Host.Net,\r\nSmith@Other.Org,\r\nDoe@Somewhere-Else;" +
				"\r\n\0\r\nHallo Peter");
		mbox.addMessage("Hallo Ugur");
	}
}