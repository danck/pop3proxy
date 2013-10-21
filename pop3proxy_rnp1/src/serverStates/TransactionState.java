package serverStates;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

import commands.ClientCommand;

import main.Mailbox;
import main.Pop3ServerContext;
import main.ProxyServer;

public class TransactionState extends Pop3ServerState {

	private Mailbox mbox;
	
	public TransactionState(Pop3ServerContext context, Mailbox m) {
		super(context);
		mbox = m;
		handleState();
	}
	
	@Override
	public void handleState() {
		System.out.println("S("+ Thread.currentThread() +": Switched to AuthorizationState");
		String inputLine;
		String word;
		boolean quit = false;
		
		try
		{
			while(((inputLine = context.getReader().readLine()) != null) && (quit == false))
			{
				System.out.println("S: RECV " + inputLine);
				Scanner scan = new Scanner(inputLine);
				switch(word = scan.next())
				{
					case ClientCommand.STAT:
						stat();
						break;
					case ClientCommand.LIST:
						list(scan);
						break;
					case ClientCommand.RETR:
						retr(scan);
						break;
					case ClientCommand.DELE:
						dele(scan);
						break;
					case ClientCommand.NOOP:
						noop();
						break;
					case ClientCommand.RSET:
						rset();
						break;
					case ClientCommand.QUIT:
						context.setState(new UpdateState(context, mbox));
						quit = true;
						scan.close();
						return;
					default:
						error("illegal command: " + word);
				}
				scan.close();
			}
		} catch (IOException e) 
		{
			ProxyServer.errorLogger.log(Level.WARNING, e.getMessage(), e);
		}
		System.out.println("S: Switched to TransactionState");
		System.out.println(mbox);
	}
	
	private void stat()
	{
		try {
			okay(mbox.quantity() + " messages (" + String.valueOf(mbox.numberOctets()) + " octets)");
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void list(Scanner scan)
	{
		stat();
		List<String> l = mbox.fileList();
		BufferedWriter bw = context.getWriter();
		for (String s: l){
			try {
				System.out.println("S: SENT " + s);
				bw.write(s + "\r\n");
				bw.flush();
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		try {
			System.out.println("S: SENT .");
			bw.write(".\r\n");
			bw.flush();
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void retr(Scanner scan)
	{
		try {
			Integer messageNumber = scan.nextInt();
			okay(mbox.retrieve(messageNumber));
			String message = mbox.retrieve(messageNumber);
			System.out.println("S: SENT " + message);
			context.getWriter().write(message + "\r\n");
			System.out.println("S: SENT .");
			context.getWriter().write(".\r\n");
			context.getWriter().flush();
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void dele(Scanner scan)
	{
		Integer messageNumber = scan.nextInt();
		if (mbox.delete(messageNumber)) {
			try {
				okay("message " + messageNumber + " deleted");
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
			try {
				error("message " + messageNumber + " already deleted");
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void noop()
	{
		try {
			okay("");
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private void rset()
	{
		mbox.reset();
		stat();
	}
}