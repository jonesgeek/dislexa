/**
 * 
 */
package com.jonesgeeks.dislexa.discord.listener;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.handle.audio.HotwordProcessor;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * @author will
 *
 */
@Component
public class JoinVoiceChannelRequestListener implements EventListener{
	@Value("${discord.bot.prefix:!}")
	private String prefix;
	
	private @Autowired JDA client;
	private @Autowired HotwordProcessor hotwordProcessor;
	
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
	
	protected void onMessage(MessageReceivedEvent event) {
		Message message = event.getMessage();
		User user = message.getAuthor();
		if (user.isBot()) {
			return;
		}

		MessageChannel channel = message.getChannel();
		
		if (event.isFromType(ChannelType.TEXT)) {
			Guild guild = message.getGuild();
			Member member = event.getMember();
			
			 
			
			boolean hasRole = member.isOwner() || member.getRoles().stream().filter(r -> 
					r.getName().equals("@botcommander")).findFirst().isPresent();

			if(!hasRole) {
				channel.sendMessage("You do not have the appropriate role to command the bot");
				return;
			}
			
			String[] split = message.getContent().split(" ");

			if (split.length >= 1 && split[0].startsWith(prefix)) {
				String command = split[0].replaceFirst(prefix, "");
				
				if (command.equalsIgnoreCase("join")) {
					join(channel, member);
				} 
			}
			
		}
	}
	
	private void join(MessageChannel channel, Member member) {
		GuildVoiceState voiceState = member.getVoiceState();
		
		if (!voiceState.inVoiceChannel())
			channel.sendMessage("You aren't in a voice channel!");
		else {
			VoiceChannel voice = voiceState.getChannel();
//			if (!voice.getO.getModifiedPermissions(client.getSelfUser()).contains(Permissions.VOICE_CONNECT))
//				channel.sendMessage("I can't join that voice channel!");
			if (voice.getUserLimit() != 0 && voice.getMembers().size() >= voice.getUserLimit())
				channel.sendMessage("That room is full!");
			else {
				AudioManager manager = voice.getGuild().getAudioManager();
				manager.openAudioConnection(voice);
				manager.setReceivingHandler(hotwordProcessor);
				channel.sendMessage("Connected to **" + voice.getName() + "**.");
			}
		}
	}
}
