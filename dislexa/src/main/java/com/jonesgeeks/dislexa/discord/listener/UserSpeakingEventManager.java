package com.jonesgeeks.dislexa.discord.listener;

import com.jonesgeeks.dislexa.discord.events.UserSpeakingEvent;
import com.jonesgeeks.dislexa.discord.events.WakewordDetectedEvent;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.IEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserSpeakingEventManager implements EventListener, ConnectionListener {

    private @Autowired JDA api;
    private @Autowired IEventManager eventManager;

    private User speakingUser = null;


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
    public void onPing(long l) {
        // Ignored
    }

    @Override
    public void onStatusChange(ConnectionStatus connectionStatus) {
        // Ignored
    }
}
