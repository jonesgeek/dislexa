/**
 * 
 */
package com.jonesgeeks.dislexa.discord.listener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 *
 */
public abstract class CommandListener implements EventListener{
	private @Value("${discord.bot.prefix:!}") String prefix;
	private @Autowired JDA client;
	
	public static final String BOTCOMMANDER_ROLE = "@botcommander";
	
	@PostConstruct
	private void init() {
		if(client != null) {
			client.addEventListener(this);
			System.out.println("Registered listener " + this.getClass().getName());
		} else {
			System.out.println("Could not register listener " + this.getClass().getName() + ", discord client is null.");
		}
	}
	
	@Override
	public void onEvent(Event event) {
		if(event instanceof MessageReceivedEvent) {
			onMessage((MessageReceivedEvent)event);
		}
		
	}
	
	private void onMessage(MessageReceivedEvent event) {
		Message message = event.getMessage();
		User user = message.getAuthor();
		if (user.isBot()) {
			return;
		}
		
		if (event.isFromType(ChannelType.TEXT)) {
			String[] split = message.getContent().split(" ");

			if (split.length >= 1 && split[0].startsWith(prefix)) {
				if(!checkPermissions(event)) {
					return;
				}
				
				String command = split[0].replaceFirst(prefix, "");
				Matcher m = getMatchesPattern().matcher(command);
				if (m.matches()) {
					message.delete().submit();
					doCommand(event);
				} 
			}
			
		}
	}
	
	protected boolean checkPermissions(MessageReceivedEvent event) {
		Message message = event.getMessage();
		TextChannel channel = message.getTextChannel();
		Member member = event.getMember();
		
		if(null != getRole()) {
			boolean hasRole = member.isOwner() || member.getRoles().stream().filter(r -> 
					r.getName().equals(getRole())).findFirst().isPresent();

			if(!hasRole) {
				sendTempMessage(channel, "**" + member.getAsMention() + "**, you do not have the "
						+ "appropriate role to perform this bot command.", 5_000);
			}
			return hasRole;
		}
		return true;
	}
	
	protected void sendTempMessage(TextChannel channel, String msg, long ttl) {
		Future<Message> m = channel.sendMessage(msg).submit();
		new Timer().schedule( new TimerTask() {
			@Override
			public void run() {
				if(m.isDone()) {
					try {
						m.get().delete().submit();
					} catch (InterruptedException | ExecutionException ignore) {}
				}
			}
		}, ttl );
	}
	
	protected String getRole() {
		return null;
	}
	
	protected abstract Pattern getMatchesPattern();
	
	protected abstract void doCommand(MessageReceivedEvent event);
}
