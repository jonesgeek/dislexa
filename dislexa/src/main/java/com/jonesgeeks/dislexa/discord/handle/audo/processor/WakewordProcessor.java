/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
	private boolean cancelled = false;
	
	@PostConstruct
	public void init() {
		stream = userAudioHandler.stream();
		t = new Thread(new Runnable() {
			@Override
			public void run() {
				stream.filter(audio -> {
					byte[] pcm = audio.getAudioData(1.0);
					short[] snowboyData = null;
					try {
						snowboyData = convertToShortArray(wakewordDetector.downsample(
								UserAudioReceiveHandler.OUTPUT_FORMAT, pcm));
					} catch (Exception e) {
						e.printStackTrace();
						return true; // Cancel out of the stream by returning the first matched 'true'
					}
					int result = wakewordDetector.RunDetection(snowboyData, snowboyData.length);
					if (result > 0) {
						System.out.print("wakeword " + result + " detected!\n");
					}
					// Hack to allow us to end the infinite stream.
					return cancelled;
				}).findFirst();
			}
		});
		t.start();
	}
	
	@PreDestroy
	public void close() {
		userAudioHandler.disconnect(stream);
		cancelled = true;
	}
	
	protected short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		return shorts;
	}
}
