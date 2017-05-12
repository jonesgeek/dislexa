package com.jonesgeeks.dislexa.discord.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public class AlexaEvent extends UserEvent {
    public enum State {
        WAKEWORD_DETECTED,
        LISTENING,
        PROCESSING,
        IDLE
    }

    private final State state;

    public AlexaEvent(JDA api, User user, State state) {
        super(api, user);
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
