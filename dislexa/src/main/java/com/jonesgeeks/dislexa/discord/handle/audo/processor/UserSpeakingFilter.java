/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import com.jonesgeeks.dislexa.discord.events.UserSpeakingEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.JDAImpl;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import net.dv8tion.jda.core.hooks.IEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.events.WakewordDetectedEvent;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 * TODO: Implement this
 */
@Component
public class UserSpeakingFilter implements Predicate<UserAudio>, EventListener, ConnectionListener {
	private @Autowired JDA api;
	private @Autowired IEventManager eventManager;

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
		if (event instanceof WakewordDetectedEvent) {
			if ( speakingUser == null ) {
                speakingUser = ((WakewordDetectedEvent) event).getUser();
                eventManager.handle(new UserSpeakingEvent(api, speakingUser, true));
            }
		}
		
	}

	@Override
	public void onUserSpeaking(User user, boolean speaking) {
		if ( !speaking && user.equals(speakingUser) ) {
			speakingUser = null;
			eventManager.handle(new UserSpeakingEvent(api, user, false));
		}
	}

	@Override
	public void onStatusChange(ConnectionStatus connectionStatus) {
	}

	@Override
	public void onPing(long l) {
	}
}
