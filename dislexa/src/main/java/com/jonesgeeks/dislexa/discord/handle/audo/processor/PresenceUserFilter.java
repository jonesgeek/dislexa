/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import com.jonesgeeks.dislexa.avs.state.Presence;
import net.dv8tion.jda.core.audio.UserAudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
public class PresenceUserFilter implements Predicate<UserAudio> {
	private @Autowired Presence presence;

    /* (non-Javadoc)
     * @see java.util.function.Predicate#test(java.lang.Object)
     */
	@Override
	public boolean test(UserAudio audio) {
		return audio.getUser().equals(presence.getUser());
	}
}
