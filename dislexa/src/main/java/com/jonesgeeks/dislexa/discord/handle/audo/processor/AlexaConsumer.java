/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.sound.sampled.LineUnavailableException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazon.alexa.avs.AudioCapture;
import com.amazon.alexa.avs.AudioStateOutputStream;
import com.amazon.alexa.avs.RecordingRMSListener;
import com.amazon.alexa.avs.RecordingStateListener;
import com.jonesgeeks.avs.state.AlexaPresence;
import com.jonesgeeks.dislexa.discord.events.UserSpeakingEvent;

import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

/**
 * Consumes and outputs audio to channel
 */
@Component
public class AlexaConsumer implements Consumer<UserAudio>, AudioCapture, EventListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private BlockingQueue<byte []> audioQueue;
	
    private final int BUFFER_SIZE_IN_BYTES = 10000;

    private AudioBufferThread thread;
    
    private boolean isCapturing = false;

	@Override
	public void accept(UserAudio audio) {
		this.audioQueue.add(audio.getAudioData(1.0));
	}
	
	@PostConstruct
	public void init() throws IOException {
		audioQueue = new LinkedBlockingDeque<>();
	}

	@Override
	public InputStream getAudioInputStream(RecordingStateListener stateListener, RecordingRMSListener rmsListener)
			throws LineUnavailableException, IOException {
		try {
            PipedInputStream inputStream = new PipedInputStream(BUFFER_SIZE_IN_BYTES);
            thread = new AudioBufferThread(inputStream, stateListener, rmsListener);
            thread.start();
            startCapture();
            return inputStream;
        } catch (IOException e) {
            stopCapture();
            throw e;
        }
	}
	
	private void startCapture() {
		isCapturing = true;
	}

	@Override
	public void stopCapture() {
		isCapturing = false;
		thread.setStopCapture(false);
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof UserSpeakingEvent) {
			UserSpeakingEvent use = (UserSpeakingEvent) event;
			if(isCapturing && !use.isSpeaking()) {
				stopCapture();
			}
		}
	}
	
	private class AudioBufferThread extends Thread {

        private final AudioStateOutputStream audioStateOutputStream;
        private boolean stopCapture;

        public AudioBufferThread(PipedInputStream inputStream,
                RecordingStateListener recordingStateListener, RecordingRMSListener rmsListener)
                        throws IOException {
            audioStateOutputStream =
                    new AudioStateOutputStream(inputStream, recordingStateListener, rmsListener);
            stopCapture = false;
        }

        @Override
        public void run() {
        	try {
	        	while(!stopCapture) {
		        	audioQueue.stream().forEachOrdered(b -> {
		        		try {
							audioStateOutputStream.write(b);
						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}
		        	});
	        	}
	        	audioQueue.clear();
        	} finally {
        		IOUtils.closeQuietly(audioStateOutputStream);
        	}
        }

		/**
		 * @param stopCapture the stopCapture to set
		 */
		public void setStopCapture(boolean stopCapture) {
			this.stopCapture = stopCapture;
		}
    }
}
