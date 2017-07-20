/**
 * 
 */
package com.jonesgeeks.avs.state;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.events.AlexaEvent;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.hooks.IEventManager;

/**
 *
 */
@Component
public class AlexaPresence implements Presence {
	private @Autowired IEventManager eventManager;
	private @Autowired JDA api;
	
	private AtomicReference<State> state = new AtomicReference<>(State.IDLE);
	private AtomicReference<User> user = new AtomicReference<>(null);


	/* (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#getState()
	 */
	@Override
	public State getState() {
		return state.get();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#getUser()
	 */
	@Override
	public User getUser() {
		return user.get();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setIdle()
	 */
	@Override
	public void setIdle() {
		user.set(null);
		setState(State.IDLE);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setListening(net.dv8tion.jda.core.entities.User)
	 */
	@Override
	public void setListening(User user) {
		this.user.compareAndSet(null, user);
		setState(State.LISTENING);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setActiveListening()
	 */
	@Override
	public void setActiveListening() {
		setState(State.ACTIVE_LISTENING);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setThiking()
	 */
	@Override
	public void setThiking() {
		setState(State.THINKING);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setSpeaking()
	 */
	@Override
	public void setSpeaking() {
		setState(State.SPEAKING);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setMicrophoneOff()
	 */
	@Override
	public void setMicrophoneOff() {
		setState(State.MICROPHONE_OFF);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.jonesgeeks.avs.state.Presence#setSystemError()
	 */
	@Override
	public void setSystemError() {
		setState(State.SYSTEM_ERROR);
	}
	
	/**
	 * 
	 */
	protected void setState(State state) {
		this.state.set(state);
		AlexaEvent event = new AlexaEvent(api, user.get(), state);
		eventManager.handle(event);
	}

}
