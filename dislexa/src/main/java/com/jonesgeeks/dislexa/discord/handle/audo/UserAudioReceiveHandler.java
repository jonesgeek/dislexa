/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo;

import com.jonesgeeks.util.Streamable;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
public interface UserAudioReceiveHandler extends AudioReceiveHandler, Streamable<UserAudio> {

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveUser()
	 */
	@Override
	default boolean canReceiveUser() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveCombined()
	 */
	@Override
	default boolean canReceiveCombined() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleCombinedAudio(net.dv8tion.jda.core.audio.CombinedAudio)
	 */
	@Override
	default void handleCombinedAudio(CombinedAudio combinedAudio) {
	}

}
