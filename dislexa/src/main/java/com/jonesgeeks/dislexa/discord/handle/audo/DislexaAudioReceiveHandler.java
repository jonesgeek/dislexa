/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo;

import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.handle.audo.processor.AlexaListenFilter;
import com.jonesgeeks.dislexa.discord.handle.audo.processor.AlexaOutputToVoiceChannel;
import com.jonesgeeks.dislexa.discord.handle.audo.processor.AudioDownsamplerConsumer;
import com.jonesgeeks.dislexa.discord.handle.audo.processor.OutputToSpeakerConsumer;
import com.jonesgeeks.dislexa.discord.handle.audo.processor.WakewordConsumer;

import cyclops.async.Queue;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class DislexaAudioReceiveHandler implements UserAudioReceiveHandler {
	private @Autowired AudioDownsamplerConsumer downsampler;
	private @Autowired OutputToSpeakerConsumer outputToSpeaker;
	private @Autowired WakewordConsumer wakeword;
	private @Autowired AlexaListenFilter alexaListen;
	private @Autowired AlexaOutputToVoiceChannel alexaRespond;

	private Queue<UserAudio> audioQueue = new Queue<>(new ArrayBlockingQueue<UserAudio>(1));
	private Thread t;

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		if(!userAudio.getUser().isBot() && !audioQueue.add(userAudio)) {
			audioQueue.get(); //Pull off the current audio and add a new one.
			audioQueue.add(userAudio);
		}
	}

	@PostConstruct
	public void init() {
		t = new Thread(new Runnable() {

			@Override
			public void run() {
				audioQueue.stream()
//				.map(downsampler)
				.filter(audio -> audio != null)
				.peek(outputToSpeaker)
				.peek(wakeword)
				.filter(alexaListen)
				.forEach(alexaRespond);
			}
		});
		t.start();

	}

	@PreDestroy
	public void close() {
		audioQueue.closeAndClear();
	}

}
