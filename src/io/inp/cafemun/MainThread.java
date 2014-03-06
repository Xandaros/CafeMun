package io.inp.cafemun;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

public class MainThread extends Thread
{
	public BlockingQueue<Message>	inputQueue		= new LinkedBlockingQueue<Message>();
	public boolean running = true;
	
	private InputStream				in;
	private Globals					globals			= JsePlatform.standardGlobals();
	private List<LuaFunction>		inputCallbacks	= new LinkedList<LuaFunction>();
	
	public MainThread(InputStream in)
	{
		this.in = in;
	}
	
	private void setupLua()
	{
		LuaTable irc = new LuaTable();
		
		irc.set(LuaValue.valueOf("registerInputListener"), new OneArgFunction()
		{
			@Override
			public LuaValue call(LuaValue arg)
			{
				inputCallbacks.add(arg.checkfunction());
				return LuaValue.NIL;
			}
		});
		
		irc.set(LuaValue.valueOf("sendRaw"), new OneArgFunction()
		{
			@Override
			public LuaValue call(LuaValue arg)
			{
				CafeMun.sendRaw(arg.checkjstring());
				return LuaValue.NIL;
			}
		});
		
		globals.set(LuaValue.valueOf("irc"), irc);
	}
	
	private void runDirectory(File directory)
	{
		System.out.println("Dir: " + directory.getAbsolutePath());
		if (!directory.isDirectory())
			return;
		
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
		
		setupLua();
		
		runDirectory(new File("lua/autorun/"));
		
		for (;;)
		{
			try
			{
				Message mes = inputQueue.take();
				for (LuaFunction callback : inputCallbacks)
				{
					callback.call(CoerceJavaToLua.coerce(mes));
				}
			}
			catch (InterruptedException e)
			{
				if (!running) return;
			}
		}
	}
}
