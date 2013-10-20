package serverStates;

import java.io.IOException;
import java.util.logging.Level;

import main.Pop3ServerContext;
import main.ProxyServer;

// Start State
public class ServerConnectedState extends Pop3ServerState
{

	public ServerConnectedState(Pop3ServerContext context)
	{
		super(context);
		// handleState();
	}

	@Override
	public void handleState()
	{
		System.out.println("S: Switched to ServerConnectedState");

		if(context.getSocket().isConnected())
		{
			try
			{
				okay("POP 3 server ready");
			} catch (IOException e)
			{
				ProxyServer.infoLogger.log(Level.WARNING, e.getMessage(), e);
			}
			context.setState(new AuthorizationState(context));
			ProxyServer.infoLogger.log(Level.INFO, "Closing connection to " + context.getSocket().getRemoteSocketAddress());
			try 
			{
				okay(context.getSocket().getInetAddress() + " is saying goodbye");
				context.getReader().close();
				context.getWriter().close();
				context.getSocket().close();
			} catch (IOException e) 
			{
				ProxyServer.infoLogger.log(Level.WARNING, e.getMessage(), e);
			}
		}
	}
}