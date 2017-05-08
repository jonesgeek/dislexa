package com.jonesgeeks.dislexa.wakeword;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.io.IOUtils;

public interface WakewordDetector {
	
	public static final AudioFormat WAKEWORD_AUDIO_FORMAT = new AudioFormat(16000, 16, 1, true, false);
	
	default byte[] downsample(AudioFormat sourceFormat, byte[] source) 
			throws IOException {
		byte[] converted = new byte[source.length];
		AudioInputStream sourceStream = null;
		AudioInputStream convertedStream = null;
		try {
			sourceStream = new AudioInputStream(new ByteArrayInputStream(source), sourceFormat, 
					source.length);
			convertedStream = AudioSystem.getAudioInputStream(WAKEWORD_AUDIO_FORMAT, sourceStream);
			convertedStream.read(converted);
		} finally {
			IOUtils.closeQuietly(sourceStream);
			IOUtils.closeQuietly(convertedStream);
		}
		return converted;
	}

	void delete();

	boolean Reset();

	int RunDetection(String data, boolean is_end);

	int RunDetection(String data);

	int RunDetection(float[] data, int array_length, boolean is_end);

	int RunDetection(float[] data, int array_length);

	int RunDetection(short[] data, int array_length, boolean is_end);

	int RunDetection(short[] data, int array_length);

	int RunDetection(int[] data, int array_length, boolean is_end);

	int RunDetection(int[] data, int array_length);

	String GetSensitivity();

	int NumHotwords();

	void ApplyFrontend(boolean apply_frontend);

	int SampleRate();

	int NumChannels();

	int BitsPerSample();

}