package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;

public class server {

	public final static int PORT = 11000;
	private final static Logger infoLogger = Logger.getLogger("info");
	private final static Logger errorLogger = Logger.getLogger("errors");

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		ExecutorService pool = Executors.newFixedThreadPool(10);

		try {
			ServerSocket ssocket = new ServerSocket(PORT);

			try
			{
				infoLogger.log(Level.INFO, "Starting Server");
				while(true) {
					Socket s = ssocket.accept();
					infoLogger.log(Level.INFO, "Connecting to " + s.getRemoteSocketAddress());
					Callable<Void> task = new ClientHandlerTask(new Pop3ServerContext(s));
					pool.submit(task);
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			errorLogger.log(Level.SEVERE, "Failed to start server" + e.getMessage(), e);
		}
	}
}