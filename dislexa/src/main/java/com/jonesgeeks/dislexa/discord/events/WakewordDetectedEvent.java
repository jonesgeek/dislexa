/**
 * 
 */
package com.jonesgeeks.dislexa.discord.events;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

/**
 *
 */
public class WakewordDetectedEvent extends UserEvent {

	public WakewordDetectedEvent(JDA api, User user) {
		super(api, user);
	}
}
