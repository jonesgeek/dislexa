/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.wakeword.WakewordDetector;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class WakewordConsumer implements Consumer<UserAudio>{
	private @Autowired WakewordDetector wakewordDetector;
	private @Autowired AlexaListenFilter alexaListenFilter;

	/*
	 * (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 */
	@Override
	public void accept(UserAudio audio) {
		byte[] pcm = audio.getAudioData(1.0);
		short[] snowboyData = convertToShortArray(pcm);
		int result = wakewordDetector.RunDetection(snowboyData, snowboyData.length);
		if (result > 0) {
			if (alexaListenFilter.getSpeakingUser() == null) {
				alexaListenFilter.setSpeakingUser(audio.getUser());
			}
			System.out.print("wakeword " + result + " detected!\n");
		}
	}

	public short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		return shorts;
	}
}
