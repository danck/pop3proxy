package serverStates;

import java.io.IOException;
import java.util.logging.Level;

import main.Pop3ServerContext;
import main.ProxyServer;

public abstract class Pop3ServerState
{
	protected Pop3ServerContext context;
	
	public Pop3ServerState(Pop3ServerContext context)
	{
		this.context = context;
	}
	public abstract void handleState();
	
	public String getOK(String message)
	{
		//ProxyServer.infoLogger.log(Level.INFO, "S: +OK " + message);
		System.out.println("S: SENT +OK " + message);
		return "+OK "+message+"\r\n";
	}
	
	public String getErr(String message)
	{
		//ProxyServer.infoLogger.log(Level.INFO, "S: -ERR " + message);
		System.out.println("S: SENT -ERR " + message);
		return "-ERR "+message+"\r\n";
	}
	
	public void error(String message) throws IOException
	{
		context.getWriter().write(getErr(message));
		context.getWriter().flush();
	}
	
	public void okay(String message) throws IOException
	{
		context.getWriter().write(getOK(message));
		context.getWriter().flush();
	}
}