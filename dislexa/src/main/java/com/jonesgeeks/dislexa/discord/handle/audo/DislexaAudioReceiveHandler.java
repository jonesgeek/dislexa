/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo;

import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

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
	/**
     * Downsampled audio used by JDA. 16KHz 16bit mono signed LittleEndian PCM.
     */
    public static final AudioFormat DOWNSAMPLED_AUDIO_FORMAT = new AudioFormat(16000.0f, 16, 1, true, true);
    
	private @Autowired AudioDownsamplerConsumer downsampler;
	private @Autowired OutputToSpeakerConsumer outputToSpeaker;
	private @Autowired WakewordConsumer wakeword;
	private @Autowired AlexaListenFilter alexaListen;
	private @Autowired AlexaOutputToVoiceChannel alexaRespond;

	private Queue<UserAudio> audioQueue = new Queue<>(new ArrayBlockingQueue<UserAudio>(50));

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		if(!userAudio.getUser().isBot() && !audioQueue.add(userAudio)) {
			System.out.println("audioQueue too slow, trying message one more time: " +
					audioQueue.add(userAudio));
		}
	}

	@PostConstruct
	public void init() {
		
		new Thread(() -> {
			audioQueue.stream()
//				.map(downsampler)
//				.filter(audio -> audio == null)
				.peek(outputToSpeaker)
//				.peek(wakeword)
//				.filter(alexaListen)
				.forEach(alexaRespond);
		}).start();

	}

	@PreDestroy
	public void close() {
		audioQueue.closeAndClear();
	}

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return DOWNSAMPLED_AUDIO_FORMAT;
	}

}
