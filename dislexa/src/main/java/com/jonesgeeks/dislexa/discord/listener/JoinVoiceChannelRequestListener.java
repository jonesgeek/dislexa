/**
 * 
 */
package com.jonesgeeks.dislexa.discord.listener;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.handle.audo.UserAudioReceiveHandler;

import net.dv8tion.jda.core.entities.GuildVoiceState;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 */
@Component
public class JoinVoiceChannelRequestListener extends CommandListener {
	private @Autowired UserAudioReceiveHandler audioHandler;
	private final Pattern commandWord = Pattern.compile("join");

	@Override
	protected String getRole() {
		return BOTCOMMANDER_ROLE;
	}

	@Override
	protected Pattern getMatchesPattern() {
		return commandWord;
	}

	@Override
	protected void doCommand(MessageReceivedEvent event) {
		Member member = event.getMember();
		TextChannel channel = event.getTextChannel();
		
		GuildVoiceState voiceState = member.getVoiceState();

		if (!voiceState.inVoiceChannel())
			sendTempMessage(channel, ":poo: You aren't in a voice channel! :poo:", 10_000);
		else {
			VoiceChannel voice = voiceState.getChannel();
			//			if (!voice.getO.getModifiedPermissions(client.getSelfUser()).contains(Permissions.VOICE_CONNECT))
			//				channel.sendMessage("I can't join that voice channel!");
			if (voice.getUserLimit() != 0 && voice.getMembers().size() >= voice.getUserLimit())
				sendTempMessage(channel, ":poo: That room is full! :poo:", 10_000);
			else {
				AudioManager manager = voice.getGuild().getAudioManager();
				manager.setReceivingHandler(audioHandler);
				manager.openAudioConnection(voice);
				sendTempMessage(channel, "Connected to **" + voice.getName() + "**.", 5_000);
			}
		}
	}
}
