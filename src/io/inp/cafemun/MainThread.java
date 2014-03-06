package io.inp.cafemun;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.JsePlatform;

public class MainThread extends Thread
{
	public Queue<Message>	inputQueue = new ConcurrentLinkedQueue<Message>();
	private InputStream		in;
	private OutputStream	out;
	private Globals			globals	= JsePlatform.standardGlobals();
	
	public MainThread(InputStream in, OutputStream out)
	{
		this.in = in;
		this.out = out;
	}
	
	private void runDirectory(File directory)
	{
		System.out.println("Dir: " + directory.getAbsolutePath());
		if (!directory.isDirectory()) return;
		
		for (File file : directory.listFiles())
		{
			if (file.isDirectory())
			{
				runDirectory(file);
			}
			else
			{
				try
				{
					System.out.println("Loading file: " + file.getAbsolutePath());
					globals.loadfile(file.getAbsolutePath()).call();
				}
				catch (LuaError error)
				{
					error.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void run()
	{
		InputThread inputThread = new InputThread(this, in);
		inputThread.start();
		
		runDirectory(new File("lua/"));
		
		for (;;)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
