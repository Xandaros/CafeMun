irc.registerInputListener(function(message)
	if message:getCommand() == "JOIN" and message:getNick() ~= "CafeMun" then
		irc.sendRaw("PRIVMSG " .. message:getParams():get(0) .. " :Hello, " .. message:getNick() .. "!")
	end
end)