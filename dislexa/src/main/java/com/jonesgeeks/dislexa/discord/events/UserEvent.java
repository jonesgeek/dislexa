package com.jonesgeeks.dislexa.discord.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;

public class UserEvent extends Event {

    private final User user;

    public UserEvent(JDA api, User user) {
        super(api, 200); // I have no idea what the response number is, or what it is used for.
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }
}
