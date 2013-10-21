package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Mailbox extends ReentrantLock
{
	
	private static Map<Account, Mailbox> mailboxes = new HashMap<Account, Mailbox>();
	private final static String root = "mailboxes" + "/";
	private String uuid;
	private Map<Integer,String> fileMap;
	private Set<Integer> toDelete = new HashSet<Integer>();
	
	private String dir;
	
	public static Mailbox get(Account a)
	{
		return mailboxes.get(a);
	}
	
	public static void add(Account a)
	{
		if (!mailboxes.containsKey(a))
			mailboxes.put(a, new Mailbox());
	}
	
	private Mailbox()
	{
		uuid 	= UUID.randomUUID().toString();
		dir 	= root + uuid + "/";
	}
	
	/**
	 * Stores a message string
	 * 
	 * @param message
	 */
	public void addMessage(String message)
	{
		(new File(dir)).mkdirs();
		File mfile = new File(dir, UUID.randomUUID() + ".txt");
		try {
			mfile.createNewFile();
			FileWriter fw = new FileWriter(mfile);
			fw.append(message);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Integer quantity()
	{
		return new File(dir).list().length;
	}
	
	public long numberOctets()
	{
		long numberOctets = 0;
		for (String s: new File(dir).list()){
			System.out.println(s);
			numberOctets += new File(dir,s).length();
		}
		return numberOctets;
	}
	
	public List<String> fileList()
	{
		long numberOctets;
		int count = 1;
		List<String> fileList = new ArrayList<String>();
		File f = new File(dir);
		String[] fl = f.list();
		this.fileMap = new HashMap<Integer, String>();
		
		for (String s: fl){
			StringBuilder sb = new StringBuilder("");
			this.fileMap.put(Integer.valueOf(count), s);
			sb.append(String.valueOf(count++) + " ");
			numberOctets = new File(dir,s).length();
			sb.append(String.valueOf(numberOctets));
			fileList.add(sb.toString());
		}
		return fileList;
	}
	
	public String retrieve(Integer fileNumber)
	{
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			FileReader fr = new FileReader(dir + fileMap.get(fileNumber));
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				sb.append(line+ "\r\n");
			}
				
			fr.close();
		} catch (IOException e) {
			ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
		}
		return sb.toString();
	}
	
	public boolean delete(Integer messageNumber)
	{
		return toDelete.add(messageNumber);
	}
	
	public void reset() {
		this.toDelete.clear();
	}
	
	public void update() {
		System.out.println("mbox update");
		for (String s: new File(dir).list()){
			System.out.println("deleting file "+ dir + s);
			try {
				Files.delete(Paths.get(dir + s));
			} catch (IOException e) {
				ProxyServer.errorLogger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}
}