package io.inp.cafemun;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

public class CafeMun
{
	public static Logger		logger	= Logger.getLogger("cafemun");
	private static Socket		socket;
	private static InputStream	in;
	private static OutputStream	out;
	
	public static void sendRaw(String message)
	{
		System.out.println("\nsending: " + message);
		System.out.println();
		try
		{
			message += "\r\n";
			out.write(message.getBytes("US-ASCII"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		try
		{
			socket = new Socket("irc.esper.net", 5555);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			sendRaw("NICK CafeMun");
			sendRaw("USER CafeMun 0 * :Cafe Mun");
			
			MainThread mainThread = new MainThread(in, out);
			mainThread.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
