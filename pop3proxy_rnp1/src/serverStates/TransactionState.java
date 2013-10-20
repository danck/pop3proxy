package serverStates;

import java.io.BufferedWriter;
import java.io.IOException;
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
						quit = true;
						break;					
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
		BufferedWriter bw = context.getWriter();
		for (String s: mbox.fileList()){
			try {
				bw.write(s);
				bw.flush();
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
	
	private void retr(Scanner scan)
	{
		
	}
	
	private void dele(Scanner scan)
	{
		
	}
	
	private void noop()
	{
		
	}
	
	private void rset()
	{
		
	}
}