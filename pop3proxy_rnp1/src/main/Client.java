package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Client implements Runnable {

	private enum RemoteAccount {
		account1 ("Alberto", "foo", "localhost", 110),
		account2 ("manfred", "Wurst", "localhost", ProxyServer.PORT);

		private final String username;
		private final String password;
		private final String server;
		private final Integer port;
		
		RemoteAccount(String u, String pw, String s, Integer p){
			username = u;
			password = pw;
			server = s;
			port = p;
		}
	}
	
	private BufferedReader reader;
	private BufferedWriter writer;
	private Socket socket;
	private RemoteAccount ra;
	private Mailbox mbox;

	private void log(String message){
		System.out.println("C: " + message);
	}
	
	@Override
	public void run(){
		while(true) {
			try {
				for (RemoteAccount ra: RemoteAccount.values()){
					this.ra = ra;
					socket = new Socket(ra.server, ra.port);
					reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer = new BufferedWriter(new OutputStreamWriter (socket.getOutputStream()));
					mbox = Mailbox.get(Account.getByCredentials("manfred", "Wurst"));
					try {
						if (!mbox.tryLock(2, TimeUnit.SECONDS)){
							getReply("QUIT");
							break;
						} 	
					} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					fetch();
				}
			
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
			try {
				Thread.currentThread();
				Thread.sleep(3000000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void fetch() throws IOException {
		if (reader.readLine().contains("OK"))
		{
			getReply("USER", ra.username);
			getReply("PASS", ra.password);
			String stat = getReply("STAT");
			Scanner statScan = new Scanner(stat);
			statScan.next();
			Integer numberMessages = statScan.nextInt();
			for (int i=0; i < numberMessages; i++ ) {
				String message = getReply("RETR", String.valueOf(i+1));
				log("C: MESSAGE "+ message);
				mbox.addMessage(message);
				getReply("DELE", String.valueOf(i+1));
			}
//			String listedMessages = getReply("LIST");
			getReply("QUIT");
		} else
			log("Server not ready");
	}
	
	private String getReply(String command) throws IOException {
		return getReply(command, "");
	}
	
	private String getReply(String command, String arguments) throws IOException {
		StringBuilder sb = new StringBuilder("");
		String line = "";
		if (arguments.isEmpty()) {
			log("SENT " + command);
			writer.write(command + "\r\n");
		} else {
			log("SENT " + command + " " + arguments);
			writer.write(command + " " + arguments + "\r\n");
		}
		writer.flush();
		line = reader.readLine();
		sb.append(line);
		while (socket.getInputStream().available() != 0){
			line = reader.readLine();
			log("RECV " + line);
			sb.append(line);
		}
		return sb.toString();
	}
}