package com.jonesgeeks.dislexa.discord.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public class UserSpeakingEvent extends UserEvent {

    private boolean speaking;

    public UserSpeakingEvent(JDA api, User user, boolean speaking) {
        super(api, user);

        this.speaking = speaking;
    }

    public boolean isSpeaking() {
        return speaking;
    }
}
