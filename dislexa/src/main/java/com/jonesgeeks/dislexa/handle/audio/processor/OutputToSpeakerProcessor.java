/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio.processor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.handle.audio.UserAudioReceiveHandler;
import com.jonesgeeks.dislexa.handle.audio.converter.AudioConverter;

/**
 *
 */
@Component
public class OutputToSpeakerProcessor {
	private @Autowired UserAudioReceiveHandler userAudioHandler;
	private @Autowired AudioConverter audioConverter;
	private @Value("${discord.bot.audio.outputToSpeaker: false}") boolean outputToSpeaker;

	private SourceDataLine line;
	
	@PostConstruct
	public void init() throws LineUnavailableException {
		if(outputToSpeaker) {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, UserAudioReceiveHandler.OUTPUT_FORMAT);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
			line.start();
			
			userAudioHandler.stream().forEach(audio -> {
				byte[] pcm = audio.getAudioData(1.0);
				try {
					byte[] wave = audioConverter.pcmToWave(pcm, 
							(int) UserAudioReceiveHandler.OUTPUT_FORMAT.getSampleRate(), 
							UserAudioReceiveHandler.OUTPUT_FORMAT.getChannels(),
							UserAudioReceiveHandler.OUTPUT_FORMAT.getSampleSizeInBits());
					line.write(wave, 0, wave.length);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	@PreDestroy
	public void close() {
		if(outputToSpeaker && line != null) {
			line.drain();
			line.close();
			line = null;
		}
	}
}
