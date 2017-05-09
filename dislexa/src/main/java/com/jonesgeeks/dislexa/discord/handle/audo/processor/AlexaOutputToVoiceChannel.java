/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 * Consumes and outputs audio to channel, prolly not UserAudio
 */
@Component
public class AlexaOutputToVoiceChannel implements Consumer<UserAudio> {

	@Override
	public void accept(UserAudio t) {
		// TODO output whatever audio Alexa outputs to voice channel
	}

}
