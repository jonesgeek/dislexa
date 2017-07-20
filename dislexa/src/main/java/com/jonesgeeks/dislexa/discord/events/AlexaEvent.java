package com.jonesgeeks.dislexa.discord.events;

import com.jonesgeeks.avs.state.State;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public class AlexaEvent extends UserEvent {
    private final State state;

    public AlexaEvent(JDA api, User user, State state) {
        super(api, user);
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
