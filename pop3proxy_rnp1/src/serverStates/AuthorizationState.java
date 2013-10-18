package serverStates;

import java.io.IOException;

import main.Pop3ServerContext;

public class AuthorizationState extends Pop3ServerState
{

	public AuthorizationState(Pop3ServerContext context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		handleState();
	}

	@Override
	public void handleState()
	{
		// TODO Auto-generated method stub
		String inputLine;
		
		try
		{
			while((inputLine = context.getReader().readLine()) != null)
			{
				context.getWriter().write(getOK(inputLine));
				context.getWriter().flush();
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
