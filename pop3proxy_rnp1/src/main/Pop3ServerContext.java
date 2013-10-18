package main;

import java.io.*;
import java.net.Socket;

import serverStates.Pop3ServerState;
import serverStates.ServerConnectedState;

public class Pop3ServerContext
{
	private Pop3ServerState state;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Socket socket;
	
	public Pop3ServerContext(Socket socket) throws IOException
	{
		this.socket = socket;

		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer = new BufferedWriter(new OutputStreamWriter (socket.getOutputStream()));
		state = new ServerConnectedState(this);
	}
	
	public Pop3ServerState getState()
	{
		return state;
	}
	
	public void setState(Pop3ServerState state)
	{
		this.state = state;
	}
	
	public BufferedReader getReader()
	{
		return reader;
	}

	public void setReader(BufferedReader reader)
	{
		this.reader = reader;
	}

	public BufferedWriter getWriter()
	{
		return writer;
	}

	public void setWriter(BufferedWriter writer)
	{
		this.writer = writer;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
}
