package main;

import java.util.HashMap;
import java.util.Map;

public class Account {
	private static final  Map<String, Account> accounts = new HashMap<String, Account>();
	
	public static boolean validate(String user, String pass)
	{
		return accounts.containsKey(user) && accounts.get(user).checkPassword(pass);
	}
	
	public static void add(String user, String password)
	{
		
		accounts.put(user, new Account(user, password));
	}

	public static Account getByCredentials(String u, String p)
	{
		if (validate(u, p))
		{
			return accounts.get(u);
		} else
		{
			return null;
		}
	}

	
	public static boolean checkUser(String user)
	{
		return accounts.containsKey(user);
	}

	private String user;
	private String password;
		
	private Account(String u, String p)
	{
		user = u;
		password = p;
	}
	
	private boolean checkPassword(String p)
	{
		return p.equals(password);
	}
}
