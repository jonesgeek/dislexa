/**
 * 
 */
package com.jonesgeeks.util;

import java.util.stream.Stream;

import cyclops.stream.ReactiveSeq;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
public interface Streamable<T> {
	
	/**
	 * 
	 * @return
	 */
	public ReactiveSeq<T> stream();

	/**
	 * 
	 * @param stream
	 */
	void disconnect(Stream<UserAudio> stream);
	
}
