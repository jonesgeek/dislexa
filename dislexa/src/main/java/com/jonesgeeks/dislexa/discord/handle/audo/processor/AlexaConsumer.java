/**
 * 
 */
package com.jonesgeeks.dislexa.discord.handle.audo.processor;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazon.alexa.avs.AudioInputFormat;
import com.amazon.alexa.avs.RequestListener;
import com.amazon.alexa.avs.http.AVSClient;
import com.amazon.alexa.avs.message.request.RequestBody;

import net.dv8tion.jda.core.audio.UserAudio;

/**
 * Consumes and outputs audio to channel
 */
//@Component
public class AlexaConsumer implements Consumer<UserAudio> {
	private @Autowired AVSClient client;
	
	private PipedInputStream in;
	private PipedOutputStream out;

	@Override
	public void accept(UserAudio audio) {
		try {
			out.write(audio.getAudioData(1.0));
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@PostConstruct
	public void init() throws IOException {
		in = new PipedInputStream();
		out = new PipedOutputStream(in);
		
		new Thread(() -> {
			RequestBody body = null;
			RequestListener listener = new RequestListener() {

				/* (non-Javadoc)
				 * @see com.amazon.alexa.avs.RequestListener#onRequestSuccess()
				 */
				@Override
				public void onRequestSuccess() {
					// TODO Auto-generated method stub
					super.onRequestSuccess();
				}
				
			};
			
			try {
				client.sendEvent(body, in, listener, AudioInputFormat.LPCM);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
	
	@PreDestroy
	public void close() {
		IOUtils.closeQuietly(in);
		IOUtils.closeQuietly(out);
	}

}
