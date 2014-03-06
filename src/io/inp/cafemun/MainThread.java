package io.inp.cafemun;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Queue;

public class MainThread extends Thread
{
	private InputStream		in;
	private OutputStream	out;
	private Queue<Message>	inputQueue;
	
	public synchronized void addInput(Message input)
	{
		inputQueue.add(input);
	}
	
	public MainThread(InputStream in, OutputStream out)
	{
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run()
	{
		InputThread inputThread = new InputThread(in);
		inputThread.start();
		
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
