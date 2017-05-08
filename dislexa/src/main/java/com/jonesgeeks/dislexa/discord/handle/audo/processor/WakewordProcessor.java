/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.LineUnavailableException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.discord.handle.audo.UserAudioReceiveHandler;
import com.jonesgeeks.dislexa.wakeword.WakewordDetector;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class WakewordProcessor {
	private @Autowired UserAudioReceiveHandler userAudioHandler;
	private @Autowired WakewordDetector wakewordDetector;

	private Stream<UserAudio> stream;

	private Thread t;
	
	@PostConstruct
	public void init() {
		stream = userAudioHandler.stream();
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				stream.forEach(audio -> {
					byte[] pcm = audio.getAudioData(1.0);
					short[] snowboyData = convertToShortArray(pcm);
					int result = wakewordDetector.RunDetection(snowboyData, snowboyData.length);
					if (result > 0) {
						System.out.print("wakeword " + result + " detected!\n");
					}
				});
			}
		});
		t.start();
		System.out.println("Wakeword Processor initialized.");
	}
	
	@PreDestroy
	public void close() {
		userAudioHandler.disconnect(stream);
		t.interrupt();
	}
	
	protected short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
		for (short s : shorts) {
			bytes.putShort(s);
		}
		return shorts;
	}
}
