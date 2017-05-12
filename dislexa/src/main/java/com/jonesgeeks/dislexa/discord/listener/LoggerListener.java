package com.jonesgeeks.dislexa.discord.listener;

import com.jonesgeeks.dislexa.discord.events.AlexaEvent;
import com.jonesgeeks.dislexa.discord.events.UserSpeakingEvent;
import com.jonesgeeks.dislexa.discord.events.WakewordDetectedEvent;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggerListener implements EventListener {
    private Logger logger = LoggerFactory.getLogger(LoggerListener.class);

    @Override
    public void onEvent(Event event) {
        if (event instanceof AlexaEvent) {
            AlexaEvent alexaEvent = (AlexaEvent)event;
            logger.info("Alexa changed it's state to " + alexaEvent.getState() + " for user " + alexaEvent.getUser().getName());
        } else if (event instanceof UserSpeakingEvent) {
            UserSpeakingEvent userSpeakingEvent = (UserSpeakingEvent)event;
            logger.info("user "  + userSpeakingEvent.getUser().getName() + (!userSpeakingEvent.isSpeaking() ? " stopped" : "") + " speaking");
        } else if (event instanceof WakewordDetectedEvent) {
            WakewordDetectedEvent wakewordDetectedEvent = (WakewordDetectedEvent)event;
            logger.info("Wakeword detected for user " + wakewordDetectedEvent.getUser());
        }
    }
}
