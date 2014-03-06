package io.inp.cafemun;

import java.util.ArrayList;
import java.util.List;

public class Message
{
	private String			nick;
	private String			user;
	private String			host;
	private String			command;
	private List<String>	params	= new ArrayList<String>();
	private String			trailing;
	
	public Message(String nick, String user, String host, String command, List<String> params, String trailing)
	{
		super();
		this.nick = nick;
		this.user = user;
		this.host = host;
		this.command = command;
		
		this.params = new ArrayList<String>(params);
		
		this.trailing = trailing;
	}
	
	public String getNick()
	{
		return nick;
	}
	
	public void setNick(String nick)
	{
		this.nick = nick;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host)
	{
		this.host = host;
	}
	
	public String getCommand()
	{
		return command;
	}
	
	public void setCommand(String command)
	{
		this.command = command;
	}
	
	public List<String> getParams()
	{
		return params;
	}
	
	public void setParams(List<String> params)
	{
		this.params = params;
	}
	
	public String getTrailing()
	{
		return trailing;
	}
	
	public void setTrailing(String trailing)
	{
		this.trailing = trailing;
	}
}
