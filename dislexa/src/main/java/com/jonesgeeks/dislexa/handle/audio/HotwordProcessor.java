/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.hotword.HotwordDetector;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 * @author will
 *
 */
@Component
public class HotwordProcessor implements AudioReceiveHandler{

	@Autowired
	private HotwordDetector detector;

	private boolean print = true;
	
	AudioFormat OUTPUT_FORMAT = new AudioFormat(48000.0f, 32, 2, true, false);

	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveUser()
	 */
	@Override
	public boolean canReceiveUser() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		// Ignore any bot trashtalk
		if(userAudio.getUser().isBot()) return;
		
		byte[] audio = userAudio.getAudioData(1.0);
		
		if(print) {
			System.out.println(audio.length);
			print=false;
		}

		short[] snowboyData = new short[1600];
		ByteBuffer.wrap(audio).order(
				ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(snowboyData);

		// Detection.
		int result = detector.RunDetection(snowboyData, snowboyData.length);
		if (result > 0) {
			System.out.print("Hotword " + result + " detected!\n");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveCombined()
	 */
	@Override
	public boolean canReceiveCombined() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleCombinedAudio(net.dv8tion.jda.core.audio.CombinedAudio)
	 */
	@Override
	public void handleCombinedAudio(CombinedAudio combinedAudio) {
	}
}
