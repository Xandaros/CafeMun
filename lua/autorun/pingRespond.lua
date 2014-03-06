irc.registerInputListener(function(message)
	if message:getCommand() == "PING" then
		irc.sendRaw("PONG :" .. message:getTrailing())
		print("Responded to ping")
	end
end)