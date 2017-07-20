/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo;

import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.handle.audo.processor.UserSpeakingFilter;
import com.jonesgeeks.dislexa.discord.handle.audo.processor.AlexaConsumer;
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
    
	private @Autowired OutputToSpeakerConsumer outputToSpeaker;
	private @Autowired WakewordConsumer wakeword;
	private @Autowired UserSpeakingFilter userSpeakingFilter;
	private @Autowired AlexaConsumer alexa;

	// Queue that all audio is put on, perform wakeword detection, then filter what goes on alexaQueue
	private Queue<UserAudio> audioQueue = new Queue<>(new ArrayBlockingQueue<>(50));
	// Queue that alexa will be listening to.
	private Queue<UserAudio> alexaQueue = new Queue<>(new ArrayBlockingQueue<>(50));

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		if(!userAudio.getUser().isBot()) {
			addToQueue(userAudio, audioQueue, "audioQueue");
		}
	}

	@PostConstruct
	public void init() {
		// Thread for processing wakeword and determining if alexa should be interested
		new Thread(() -> {
			audioQueue.stream()
				.peek(wakeword)
				.filter(userSpeakingFilter)
				.forEach(audio -> { addToQueue(audio, alexaQueue, "alexaQueue"); });
		}).start();
		
		// Thread for processing alexa-interesting audio so that the back-pressure doesn't affect 
		// the wakeword processing
		new Thread(() -> {
			alexaQueue.stream()
				.peek(outputToSpeaker)
				.forEach(alexa);
		}).start();

	}
	
	protected void addToQueue(UserAudio audio, Queue<UserAudio> queue, String queueName) {
		if(!queue.add(audio)) {
			System.out.println(queueName + " too slow, trying message one more time: " +
					queue.add(audio));
		}
	}

	@PreDestroy
	public void close() {
		audioQueue.closeAndClear();
		alexaQueue.closeAndClear();
	}

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#getFormat()
	 */
	@Override
	public AudioFormat getFormat() {
		return DOWNSAMPLED_AUDIO_FORMAT;
	}

}
