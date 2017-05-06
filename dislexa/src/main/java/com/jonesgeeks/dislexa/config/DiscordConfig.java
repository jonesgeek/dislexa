/**
 * 
 */
package com.jonesgeeks.dislexa.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.jonesgeeks.dislexa.hotword.HotwordDetector;

import ai.kitt.snowboy.SnowboyDetect;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * @author will
 *
 */
@Configuration
@ComponentScan("com.jonesgeeks.dislexa.discord")
public class DiscordConfig {
	
	@Value("${discord.bot.token}")
	private String token;

	@Value("${discord.bot.hotword.sensitivity: 0.6}")
	private String hotwordSensitivity;
	
	@Value("${discord.bot.hotword.audioGain: 1}")
	private float hotwordAudioGain;
	
	@Value("classpath:common.res")
    private Resource commonResource;
	
	@Value("classpath:alexa.umdl")
    private Resource modelResource;
	

	@Bean(destroyMethod="shutdown")
	public JDA getDiscordClient() throws LoginException, IllegalArgumentException, RateLimitedException {
		JDA jda = new JDABuilder(AccountType.BOT).setToken(token).buildAsync();
		return jda;
	}
	
	@Bean
	public HotwordDetector getHotwordDetector() throws IOException {
		
		SnowboyDetect detector = new SnowboyDetect(copyResourceToTemp(commonResource).getAbsolutePath(), 
				copyResourceToTemp(modelResource).getAbsolutePath());
		detector.SetSensitivity(hotwordSensitivity);
	    detector.SetAudioGain(hotwordAudioGain);
	    return detector;
	}
	
	private File copyResourceToTemp(Resource r) throws IOException {
		OutputStream out = null;
		InputStream in = null;
		try {
			File temp = File.createTempFile("snowball_", ".tmp");
			temp.deleteOnExit();
			out = new FileOutputStream(temp);
			in = r.getInputStream();
			IOUtils.copy(in, out);
			return temp;
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}
	
	/**
	 * Event listener to let us know that the bot is ready
	 */
	@Component
	class BotReadyListener extends ListenerAdapter{
		private @Autowired JDA jda;
		
		@PostConstruct
		private void init() {
			jda.addEventListener(this);
		}
		/*
		 * (non-Javadoc)
		 * @see net.dv8tion.jda.core.hooks.EventListener#onEvent(net.dv8tion.jda.core.events.Event)
		 */
		@Override
		public void onReady(ReadyEvent event) {
			System.out.println("Dislexa is now ready!");
			jda.removeEventListener(this);
		}
	}
}
