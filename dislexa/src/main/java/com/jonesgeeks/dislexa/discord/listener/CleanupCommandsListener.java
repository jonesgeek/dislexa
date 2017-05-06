/**
 * 
 */
package com.jonesgeeks.dislexa.discord.listener;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 */
@Component
public class CleanupCommandsListener extends CommandListener {
	private final Pattern matches = Pattern.compile("clea(n|r)");

	@Override
	protected Pattern getMatchesPattern() {
		return matches;
	}

	@Override
	protected void doCommand(MessageReceivedEvent event) {
		TextChannel channel = event.getTextChannel();
		MessageHistory history = channel.getHistory();
		history.retrievePast(100).complete().stream().filter(m -> m.getContent().startsWith(getPrefix()) 
				|| m.getAuthor().isBot()).forEach(m -> m.delete().submit());
		sendTempMessage(channel, "Cleaned up commands and bot responses", 5_000);
	}
}
