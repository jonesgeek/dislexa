/**
 * 
 */
package com.jonesgeeks.dislexa.handle.audio.converter;

import java.io.IOException;

/**
 *
 */
public interface AudioConverter {

	public byte[] pcmToWave(byte[] pcm, int srate, int channels, int format) throws IOException;
}
