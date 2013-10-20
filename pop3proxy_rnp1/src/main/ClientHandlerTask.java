package main;

import java.util.concurrent.Callable;

public class ClientHandlerTask implements Callable {
	private Pop3ServerContext context;
	
	ClientHandlerTask(Pop3ServerContext c) 
	{
		this.context = c;
	}
	
	@Override
	public Object call() throws Exception 
	{
		context.getState().handleState();
		return null;
	}
}