/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class OutputToSpeakerConsumer implements Consumer<UserAudio>{
	private @Value("${discord.bot.audio.outputToSpeaker: false}") boolean outputToSpeaker;

	private SourceDataLine line;
	
	@Override
	public void accept(UserAudio audio) {
		byte[] pcm = audio.getAudioData(1.0);
		line.write(pcm, 0, pcm.length);
	}
	
	@PostConstruct
	public void init() throws LineUnavailableException {
		if(outputToSpeaker) {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, AudioDownsamplerConsumer.DOWNSAMPLED_AUDIO_FORMAT);
//			DataLine.Info info = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT);
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
			line.start();
			
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
		}
	}
}
