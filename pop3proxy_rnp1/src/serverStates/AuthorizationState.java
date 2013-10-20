package serverStates;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import commands.ClientCommand;

import main.Account;
import main.Mailbox;
import main.Pop3ServerContext;

public class AuthorizationState extends Pop3ServerState
{


	public AuthorizationState(Pop3ServerContext context)
	{
		super(context);
		handleState();
	}

	@Override
	public void handleState()
	{
		System.out.println("S: Switched to AuthorizationState");
		String inputLine;
		String word;
		boolean quit = false;
		
		try
		{
			while(((inputLine = context.getReader().readLine()) != null) && (quit == false))
			{
				System.out.println("S: RECV " + inputLine);
				Scanner scan = new Scanner(inputLine);
				switch (word = scan.next()) {
				case ClientCommand.USER:
					authorize(scan);
					break;
				case ClientCommand.QUIT: 
					quit = true;
					break;
				default:
					error("");
				}
				scan.close();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void authorize(Scanner scan) throws IOException
	{
		String userName = "";
		String password = "";
		String inputLine;
		userName = scan.next();
		if (!userName.isEmpty() && Account.checkUser(userName)) 
		{
			okay("Password please, with sugar on top");
			inputLine = context.getReader().readLine();
			System.out.println("S: RECV " + inputLine);
			scan = new Scanner(inputLine);
			if (scan.next().equals(ClientCommand.PASS))
			{
				password = scan.next();
				acquireMaildrop(userName, password);
			} else
			{
				error("illegal command: PASS expected");
			}
		} else 
		{
			error("Unkown user name");
		}
	}
	
	private void acquireMaildrop(String user, String pass) throws IOException
	{
		if (Account.validate(user, pass))
		{
			Mailbox mbox = Mailbox.get(Account.getByCredentials(user, pass));
			System.out.println(mbox);
			try {
				if (mbox.tryLock(2, TimeUnit.SECONDS)){
					okay("mailbox locked and ready");
					System.out.println(mbox);
					context.setState(new TransactionState(context, mbox));
					mbox.unlock();
				} else
				{
					error("unable to lock maildrop");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			error("Invaldid credentials");
		}
	}

}