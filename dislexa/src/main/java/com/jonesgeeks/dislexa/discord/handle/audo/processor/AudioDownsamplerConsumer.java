/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.function.UnaryOperator;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
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
	public static final AudioFormat DOWNSAMPLED_AUDIO_FORMAT = new AudioFormat(
					Encoding.PCM_SIGNED,
					16000,
					16,
					1,
					2,
					16000,
					false);

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
		AudioInputStream targetStream = null;
		ByteArrayOutputStream out = null;
		try {
			sourceStream = new AudioInputStream(new ByteArrayInputStream(source), sourceFormat, 
					source.length);
			
			targetStream = AudioSystem.getAudioInputStream(DOWNSAMPLED_AUDIO_FORMAT, sourceStream);
			out = new ByteArrayOutputStream();

			AudioSystem.write(targetStream, Type.AU, out);
			converted = out.toByteArray();
		} finally {
			IOUtils.closeQuietly(sourceStream);
			IOUtils.closeQuietly(targetStream);
			IOUtils.closeQuietly(out);
		}
		// 24 is AU header length
		return Arrays.copyOfRange(converted, 24, converted.length);
	}
	
	public static short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		return shorts;
	}
	
	public static byte[] convertToLittleEndian(byte[] rawData) {
		byte[] bytes = new byte[rawData.length];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put(rawData);
		return bytes;
	}

}
