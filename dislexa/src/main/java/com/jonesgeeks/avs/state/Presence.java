package com.jonesgeeks.avs.state;

import net.dv8tion.jda.core.entities.User;

public interface Presence {

	void setSystemError();

	void setMicrophoneOff();

	void setSpeaking();

	void setThiking();

	void setActiveListening();

	void setListening(User user);

	void setIdle();

	User getUser();

	State getState();
}
