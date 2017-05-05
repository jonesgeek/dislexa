package com.jonesgeeks.dislexa.hotword;

public interface HotwordDetector {

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