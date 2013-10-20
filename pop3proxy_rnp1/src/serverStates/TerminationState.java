package serverStates;

import java.io.IOException;
import java.util.logging.Level;

import main.Pop3ServerContext;
import main.ProxyServer;

public class TerminationState extends Pop3ServerState{


	public TerminationState(Pop3ServerContext context)
	{
		super(context);
		handleState();
	}
	
	@Override
	public void handleState(){
		System.out.println("S: Switched to TerminationState");
	}

}