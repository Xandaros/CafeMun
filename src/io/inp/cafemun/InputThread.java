package io.inp.cafemun;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class InputThread extends Thread
{
	private InputStream	in;
	private MainThread mainThread;
	
	private String			nick;
	private String			user;
	private String			host;
	private String			command;
	private List<String>	params		= new ArrayList<String>();
	private String			trailing;
	
	private byte[] debugBuffer = new byte[4096];
	private int debugPtr = 0;

	public InputThread(InputStream in)
	{
		this.in = in;
	}
	
	private void handleInput() throws UnsupportedEncodingException
	{
		mainThread.addInput(new Message(nick, user, host, command, params, trailing));
		
		nick = null;
		user = null;
		host = null;
		command = null;
		params.clear();
		trailing = null;
	}
	
	@Override
	public void run()
	{
		byte[] inputBuffer = new byte[4096];
		int bufferPtr = 0;
		ReadMode readMode = ReadMode.NORMAL;
		try
		{
			int read;
			while ((read = in.read()) != -1)
			{
				if (read == '\r') continue;
				switch (readMode)
				{
					case NORMAL:
						if (read == ':')
						{
							readMode = ReadMode.NICK;
							break;
						}
						readMode = ReadMode.COMMAND;
						
					case COMMAND:
						if (read == ' ' || read == '\n')
						{
							if (bufferPtr == 0) break;
							
							command = new String(inputBuffer, 0, bufferPtr, "US-ASCII").trim();
							bufferPtr = 0;
							if (read == '\n')
								readMode = ReadMode.NORMAL;
							else 
								readMode = ReadMode.PARAMS;
							break;
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
					
					case PARAMS:
						if (read == ':' && bufferPtr == 0)
						{
							readMode = ReadMode.TRAILING;
							break;
						}
						if (read == ' ' || read == '\n')
						{
							if (bufferPtr == 0) break;
							
							params.add(new String(inputBuffer, 0, bufferPtr, "US-ASCII").trim());
							bufferPtr = 0;
							
							if (read == '\n') readMode = ReadMode.NORMAL;
							break;
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
					
					case TRAILING:
						if (read == '\n')
						{
							trailing = new String(inputBuffer, 0, bufferPtr, "US-ASCII");
							bufferPtr = 0;
							
							readMode = ReadMode.NORMAL;
							break;
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
						
					case NICK:
						if (read == ' ' || read == '!' || read == '@')
						{
							nick = new String(inputBuffer, 0, bufferPtr, "US-ASCII");
							bufferPtr = 0;
							
							switch (read)
							{
								case ' ':
									readMode = ReadMode.COMMAND;
									break;
									
								case '!':
									readMode = ReadMode.USER;
									break;
									
								case '@':
									readMode = ReadMode.HOST;
									break;
							}
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
						
					case USER:
						if (read == ' ' || read == '@')
						{
							user = new String(inputBuffer, 0, bufferPtr, "US-ASCII");
							bufferPtr = 0;
							
							if (read == ' ')
								readMode = ReadMode.COMMAND;
							else
								readMode = ReadMode.HOST;
							break;
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
						
					case HOST:
						if (read == ' ')
						{
							host = new String(inputBuffer, 0, bufferPtr, "US-ASCII");
							bufferPtr = 0;
							
							readMode = ReadMode.COMMAND;
							break;
						}
						inputBuffer[bufferPtr++] = (byte)read;
						break;
				}
				if (read == '\n')
				{
					handleInput();
					debugBuffer = new byte[4096];
					debugPtr = 0;
				}
				else
				{
					debugBuffer[debugPtr++] = (byte)read;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private enum ReadMode
	{
		NORMAL,
		NICK,
		USER,
		HOST,
		COMMAND,
		PARAMS,
		TRAILING
	}
}
