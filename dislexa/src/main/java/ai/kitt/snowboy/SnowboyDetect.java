/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package ai.kitt.snowboy;

import java.io.IOException;

import com.jonesgeeks.dislexa.wakeword.WakewordDetector;
import com.jonesgeeks.util.NativeUtils;

public class SnowboyDetect implements WakewordDetector {
	static {
		try {
			NativeUtils.loadLibraryFromJar("/libsnowboy-detect-java.dylib");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private transient long swigCPtr;
	protected transient boolean swigCMemOwn;

	protected SnowboyDetect(long cPtr, boolean cMemoryOwn) {
		swigCMemOwn = cMemoryOwn;
		swigCPtr = cPtr;
	}

	protected static long getCPtr(SnowboyDetect obj) {
		return (obj == null) ? 0 : obj.swigCPtr;
	}

	protected void finalize() {
		delete();
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#delete()
	 */
	@Override
	public synchronized void delete() {
		if (swigCPtr != 0) {
			if (swigCMemOwn) {
				swigCMemOwn = false;
				snowboyJNI.delete_SnowboyDetect(swigCPtr);
			}
			swigCPtr = 0;
		}
	}

	public SnowboyDetect(String resource_filename, String model_str) {
		this(snowboyJNI.new_SnowboyDetect(resource_filename, model_str), true);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#Reset()
	 */
	@Override
	public boolean Reset() {
		return snowboyJNI.SnowboyDetect_Reset(swigCPtr, this);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(java.lang.String, boolean)
	 */
	@Override
	public int RunDetection(String data, boolean is_end) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_0(swigCPtr, this, data, is_end);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(java.lang.String)
	 */
	@Override
	public int RunDetection(String data) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_1(swigCPtr, this, data);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(float[], int, boolean)
	 */
	@Override
	public int RunDetection(float[] data, int array_length, boolean is_end) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_2(swigCPtr, this, data, array_length, is_end);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(float[], int)
	 */
	@Override
	public int RunDetection(float[] data, int array_length) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_3(swigCPtr, this, data, array_length);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(short[], int, boolean)
	 */
	@Override
	public int RunDetection(short[] data, int array_length, boolean is_end) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_4(swigCPtr, this, data, array_length, is_end);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(short[], int)
	 */
	@Override
	public int RunDetection(short[] data, int array_length) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_5(swigCPtr, this, data, array_length);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(int[], int, boolean)
	 */
	@Override
	public int RunDetection(int[] data, int array_length, boolean is_end) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_6(swigCPtr, this, data, array_length, is_end);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#RunDetection(int[], int)
	 */
	@Override
	public int RunDetection(int[] data, int array_length) {
		return snowboyJNI.SnowboyDetect_RunDetection__SWIG_7(swigCPtr, this, data, array_length);
	}

	public void SetSensitivity(String sensitivity_str) {
		snowboyJNI.SnowboyDetect_SetSensitivity(swigCPtr, this, sensitivity_str);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#GetSensitivity()
	 */
	@Override
	public String GetSensitivity() {
		return snowboyJNI.SnowboyDetect_GetSensitivity(swigCPtr, this);
	}

	public void SetAudioGain(float audio_gain) {
		snowboyJNI.SnowboyDetect_SetAudioGain(swigCPtr, this, audio_gain);
	}

	public void UpdateModel() {
		snowboyJNI.SnowboyDetect_UpdateModel(swigCPtr, this);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#NumHotwords()
	 */
	@Override
	public int NumHotwords() {
		return snowboyJNI.SnowboyDetect_NumHotwords(swigCPtr, this);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#ApplyFrontend(boolean)
	 */
	@Override
	public void ApplyFrontend(boolean apply_frontend) {
		snowboyJNI.SnowboyDetect_ApplyFrontend(swigCPtr, this, apply_frontend);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#SampleRate()
	 */
	@Override
	public int SampleRate() {
		return snowboyJNI.SnowboyDetect_SampleRate(swigCPtr, this);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#NumChannels()
	 */
	@Override
	public int NumChannels() {
		return snowboyJNI.SnowboyDetect_NumChannels(swigCPtr, this);
	}

	/* (non-Javadoc)
	 * @see ai.kitt.snowboy.HotwordDetector#BitsPerSample()
	 */
	@Override
	public int BitsPerSample() {
		return snowboyJNI.SnowboyDetect_BitsPerSample(swigCPtr, this);
	}

}
