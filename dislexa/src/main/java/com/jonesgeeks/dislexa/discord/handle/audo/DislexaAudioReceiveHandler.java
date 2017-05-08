/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import cyclops.async.Queue;
import cyclops.async.Topic;
import cyclops.stream.ReactiveSeq;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class DislexaAudioReceiveHandler implements UserAudioReceiveHandler {
	private Queue<UserAudio> audioQueue = new Queue<>(new ArrayBlockingQueue<UserAudio>(1));
	private Topic<UserAudio> userAudioTopic = new Topic<>(audioQueue);

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
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.util.Streamable#stream()
	 */
	@Override
	public ReactiveSeq<UserAudio> stream() {
		return userAudioTopic.stream();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.util.Streamable#disconnect(java.util.stream.Stream)
	 */
	@Override
	public void disconnect(Stream<UserAudio> stream) {
		userAudioTopic.disconnect(stream);
	}

}
