/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.UnaryOperator;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 *
 */
@Component
public class AudioDownsamplerConsumer implements UnaryOperator<UserAudio> {
	public static final AudioFormat DOWNSAMPLED_AUDIO_FORMAT = 
			new AudioFormat(16000, 16, 1, true, false);

	@Override
	public UserAudio apply(UserAudio audio) {
		UserAudio downsampled = null;
		try {
			byte[] downsampledAudio = downsample(AudioReceiveHandler.OUTPUT_FORMAT, audio.getAudioData(1.0));
			downsampled = new UserAudio(audio.getUser(), convertToShortArray(downsampledAudio));
		} catch (IOException e) {
			e.printStackTrace();
		} catch(RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
		return downsampled;
	}
	
	protected byte[] downsample(AudioFormat sourceFormat, byte[] source) 
			throws IOException {
		byte[] converted = null;
		AudioInputStream sourceStream = null;
		AudioInputStream convertedStream = null;
		ByteArrayOutputStream out = null;
		try {
			sourceStream = new AudioInputStream(new ByteArrayInputStream(source), sourceFormat, 
					source.length);
			out = new ByteArrayOutputStream();
			AudioSystem.write(sourceStream, Type.WAVE, out);
			converted = out.toByteArray();
	        System.out.println("initial size: " + source.length + " converted size: " + converted.length);
		} finally {
			IOUtils.closeQuietly(sourceStream);
			IOUtils.closeQuietly(convertedStream);
			IOUtils.closeQuietly(out);
		}
		return converted;
	}
	
	public static short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		return shorts;
	}

}
