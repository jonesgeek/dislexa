/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import cyclops.async.Topic;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class DislexaAudioReceiveHandler implements UserAudioReceiveHandler {
	private BlockingQueue<UserAudio> audioQueue = new ArrayBlockingQueue<>(1);
	private Topic<UserAudio> userAudioTopic = new Topic<>();

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		if(!userAudio.getUser().isBot() && !audioQueue.offer(userAudio)) {
			audioQueue.remove();
			audioQueue.add(userAudio);
		}
	}
	
	@PostConstruct
	public void init() {
		userAudioTopic.fromStream(audioQueue.stream());
	}
	
	/*
	 * 
	 */
	public Stream<UserAudio> stream() {
		return userAudioTopic.stream();
	}

}
