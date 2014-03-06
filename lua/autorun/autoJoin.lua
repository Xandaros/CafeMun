irc.registerInputListener(function(message)
	if message:getCommand() == "376" then
		irc.sendRaw("JOIN #cafemun")
	end
end)