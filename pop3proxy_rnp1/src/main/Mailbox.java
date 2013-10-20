package main;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Mailbox extends ReentrantLock
{
	
	private static Map<Account, Mailbox> mailboxes = new HashMap<Account, Mailbox>();
	
	public static Mailbox get(Account a)
	{
		return mailboxes.get(a);
	}
	
	public static void add(Account a)
	{
		mailboxes.put(a, new Mailbox());
	}
	
	private Mailbox()
	{
		
	}
	
	
}
