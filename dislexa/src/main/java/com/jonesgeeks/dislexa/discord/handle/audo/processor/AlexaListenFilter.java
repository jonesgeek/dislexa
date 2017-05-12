/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.function.Predicate;

import net.dv8tion.jda.core.entities.User;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 * TODO: Implement this
 */
@Component
public class AlexaListenFilter implements Predicate<UserAudio> {

    private User speakingUser = null;

    public User getSpeakingUser() {
        return speakingUser;
    }

    public void setSpeakingUser(User speakingUser) {
        this.speakingUser = speakingUser;
    }

    /* (non-Javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
             */
	@Override
	public boolean test(UserAudio audio) {
		return audio.getUser().equals(speakingUser);
	}

}
