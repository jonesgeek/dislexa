/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import com.jonesgeeks.dislexa.discord.events.UserSpeakingEvent;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * TODO: Implement this
 */
@Component
public class UserSpeakingFilter implements Predicate<UserAudio>, EventListener {
    private User speakingUser = null;

    /* (non-Javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
     */
	@Override
	public boolean test(UserAudio audio) {
		return audio.getUser().equals(speakingUser);
	}

	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.hooks.EventListener#onEvent(net.dv8tion.jda.core.events.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if ( event instanceof UserSpeakingEvent ) {
			UserSpeakingEvent userSpeakingEvent = (UserSpeakingEvent) event;
			speakingUser = userSpeakingEvent.isSpeaking() ? userSpeakingEvent.getUser() : null;
		}
	}
}
