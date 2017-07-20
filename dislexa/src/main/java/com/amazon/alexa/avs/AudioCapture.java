package com.amazon.alexa.avs;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.LineUnavailableException;

public interface AudioCapture {
	public InputStream getAudioInputStream(final RecordingStateListener stateListener,
            final RecordingRMSListener rmsListener) throws LineUnavailableException, IOException;
	
	public void stopCapture();
}
