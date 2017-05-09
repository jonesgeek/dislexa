/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

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

	@Override
	public void accept(UserAudio audio) {
		byte[] pcm = audio.getAudioData(1.0);
		short[] snowboyData = AudioDownsamplerConsumer.convertToShortArray(pcm);
		int result = wakewordDetector.RunDetection(snowboyData, snowboyData.length);
		if (result > 0) {
			System.out.print("wakeword " + result + " detected!\n");
		}
	}
}
