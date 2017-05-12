/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.events.wakeword.WakewordDetectedEvent;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 * TODO: Implement this
 */
@Component
public class AlexaListenFilter implements Predicate<UserAudio>, EventListener {
	private @Autowired JDA api;
	
	@PostConstruct
	public void init() {
		api.addEventListener(this);
	}

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

	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.hooks.EventListener#onEvent(net.dv8tion.jda.core.events.Event)
	 */
	@Override
	public void onEvent(Event event) {
		if (event instanceof WakewordDetectedEvent) {
			if( speakingUser == null ) {
				speakingUser = ((WakewordDetectedEvent)event).getUser();
			}
			System.out.println("Wakeword detected");
		}
		
	}

}
