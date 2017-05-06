/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.wakeword.WakewordDetector;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

/**
 * @author will
 *
 */
@Component
public class WakewordProcessor implements AudioReceiveHandler {

	private @Autowired WakewordDetector detector;
	
	private @Value("${discord.bot.audio.canReceive: true}") boolean canReceive = true;


	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#canReceiveUser()
	 */
	@Override
	public boolean canReceiveUser() {
		return canReceive;
	}

	/*
	 * (non-Javadoc)
	 * @see net.dv8tion.jda.core.audio.AudioReceiveHandler#handleUserAudio(net.dv8tion.jda.core.audio.UserAudio)
	 */
	@Override
	public void handleUserAudio(UserAudio userAudio) {
		// Ignore any bot trashtalk
		if(userAudio.getUser().isBot()) return;
		
		handleAudio(userAudio.getAudioData(1.0));
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
		handleAudio(combinedAudio.getAudioData(1.0));
	}
	
	protected void handleAudio(byte[] audio) {
		short[] snowboyData;
		try {
			snowboyData = PCMtoWave(audio);

			// Detection.
			int result = detector.RunDetection(snowboyData, snowboyData.length);
			if (result > 0) {
				System.out.print("Hotword " + result + " detected!\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public short[] PCMtoWave(byte[] rawData) throws IOException {
		int srate = (int) OUTPUT_FORMAT.getSampleRate();
		int channel = OUTPUT_FORMAT.getChannels();
		int format = OUTPUT_FORMAT.getSampleSizeInBits();
		int bitrate = srate * channel * format;
		DataOutputStream output = null;
		short[] shorts;
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
			
			byte[] wave = baos.toByteArray();
			
			// Audio data (conversion big endian -> little endian)
			shorts = new short[wave.length / 2];
			ByteBuffer.wrap(wave).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
			ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
			for (short s : shorts) {
				bytes.putShort(s);
			}
		} finally {
			if (output != null) {
				output.close();
			}
		}
		return shorts;
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
