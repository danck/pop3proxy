package serverStates;

import java.io.IOException;

import main.Pop3ServerContext;

public class ServerConnectedState extends Pop3ServerState
{

	public ServerConnectedState(Pop3ServerContext context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		// handleState();
	}

	@Override
	public void handleState()
	{
		if(context.getSocket().isConnected())
		{
			try
			{
				System.out.println("+ Welcome to simple Pop3Proxy");
				context.getWriter().write(getOK("Welcome to simple Pop3Proxy"));
				context.getWriter().flush();
				
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			context.setState(new AuthorizationState(context));
		}
		// TODO Auto-generated method stub
	}

}
