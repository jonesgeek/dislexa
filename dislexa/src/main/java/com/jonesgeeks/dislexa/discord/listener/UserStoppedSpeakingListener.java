package com.jonesgeeks.dislexa.discord.listener;

import com.jonesgeeks.dislexa.discord.handle.audo.processor.AlexaListenFilter;
import net.dv8tion.jda.core.audio.hooks.ConnectionListener;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserStoppedSpeakingListener implements ConnectionListener {

    private @Autowired AlexaListenFilter alexaListenFilter;

    @Override
    public void onPing(long l) {

    }

    @Override
    public void onStatusChange(ConnectionStatus connectionStatus) {

    }

    @Override
    public void onUserSpeaking(User user, boolean speaking) {
        System.out.println("user "  + user.getName() + (!speaking ? " stopped" : "") + " speaking");
        if (!speaking && user.equals(alexaListenFilter.getSpeakingUser())) {
            alexaListenFilter.setSpeakingUser(null);
        }
    }
}
