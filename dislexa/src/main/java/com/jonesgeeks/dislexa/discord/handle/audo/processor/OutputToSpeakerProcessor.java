/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.handle.audo.UserAudioReceiveHandler;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
//@Component
public class OutputToSpeakerProcessor {
	private @Autowired UserAudioReceiveHandler userAudioHandler;
	private @Value("${discord.bot.audio.outputToSpeaker: false}") boolean outputToSpeaker;

	private SourceDataLine line;
	
	private Stream<UserAudio> stream;
	
	private Thread t;
	
	@PostConstruct
	public void init() throws LineUnavailableException {
		if(outputToSpeaker) {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, UserAudioReceiveHandler.OUTPUT_FORMAT);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
			line.start();
			
			stream = userAudioHandler.stream();
			t = new Thread(new Runnable() {
				@Override
				public void run() {
					stream.forEach(audio -> {
						byte[] pcm = audio.getAudioData(1.0);
						line.write(pcm, 0, pcm.length);
					});
				}
			});
			t.start();
			
		}
	}

	@PreDestroy
	public void close() {
		if(outputToSpeaker) {
			if(line != null) {
				line.drain();
				line.close();
				line = null;
			}
			userAudioHandler.disconnect(stream);
			
			t.interrupt();
		}
	}
}
