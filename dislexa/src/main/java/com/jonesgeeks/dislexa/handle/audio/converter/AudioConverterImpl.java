/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio.converter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class AudioConverterImpl implements AudioConverter {

	/* (non-Javadoc)
	 * @see com.jonesgeeks.dislexa.handle.audio.converter.AudioConverter#pcmToWave(byte[])
	 */
	@Override
	public byte[] pcmToWave(byte[] rawData, int srate, int channel, int format) throws IOException {
		int bitrate = srate * channel * format;
		DataOutputStream output = null;
		byte[] wave;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			output = new DataOutputStream(baos);
			// WAVE header
			// see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
			writeString(output, "RIFF"); // chunk id
			writeInt(output, 36 + rawData.length); // chunk size
			writeString(output, "WAVE"); // format
			writeString(output, "fmt "); // subchunk 1 id
			writeInt(output, 16); // subchunk 1 size
			writeShort(output, (short) 1); // audio format (1 = PCM)
			writeShort(output, (short) channel); // number of channels
			writeInt(output, srate); // sample rate
			writeInt(output, bitrate * 2); // byte rate
			writeShort(output, (short) 2); // block align
			writeShort(output, (short) 16); // bits per sample
			writeString(output, "data"); // subchunk 2 id
			writeInt(output, rawData.length); // subchunk 2 size
			output.flush();
			wave = baos.toByteArray();
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return wave;
	}
	
	public short[] convertToShortArray(byte[] rawData) {
		short[] shorts = new short[rawData.length / 2];
		ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
		ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
		for (short s : shorts) {
			bytes.putShort(s);
		}
		return shorts;
	}
	
	public byte[] convertToByteArray(short[] rawData) {
		byte[] bytes = new byte[rawData.length * 2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(rawData);
		return bytes;
	}

	private void writeInt(final DataOutputStream output, final int value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
		output.write(value >> 16);
		output.write(value >> 24);
	}

	private void writeShort(final DataOutputStream output, final short value) throws IOException {
		output.write(value >> 0);
		output.write(value >> 8);
	}

	private void writeString(final DataOutputStream output, final String value) throws IOException {
		for (int i = 0; i < value.length(); i++) {
			output.write(value.charAt(i));
		}
	}

}
