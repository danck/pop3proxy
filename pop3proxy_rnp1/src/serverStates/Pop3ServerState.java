package serverStates;

import main.Pop3ServerContext;

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
		return "+OK "+message+"\n";
	}
	
	public String getErr(String message)
	{
		return "-ERR "+message+"\n";
	}
}
