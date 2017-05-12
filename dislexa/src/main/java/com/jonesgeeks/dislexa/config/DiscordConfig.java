/**
 * 
 */
package com.jonesgeeks.dislexa.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.IEventManager;
import net.dv8tion.jda.core.hooks.InterfacedEventManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.jonesgeeks.dislexa.wakeword.WakewordDetector;

import ai.kitt.snowboy.SnowboyDetect;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

/**
 *
 */
@Configuration
public class DiscordConfig {
	
	@Value("${discord.bot.token}")
	private String token;

	@Value("${discord.bot.wakeword.sensitivity: 0.6}")
	private String wakewordSensitivity;
	
	@Value("${discord.bot.wakeword.audioGain: 1}")
	private float wakewordAudioGain;
	
	@Value("classpath:common.res")
    private Resource commonResource;
	
	@Value("classpath:Kirby.pmdl")
    private Resource modelResource;


	private @Autowired IEventManager eventManager;
	private @Autowired List<EventListener> eventListeners;


    @Bean
	public IEventManager eventManager() {
		return new InterfacedEventManager();
	}

	@Bean(destroyMethod="shutdown")
	public JDA jda(IEventManager eventManager) throws LoginException, IllegalArgumentException, RateLimitedException {
		JDA jda = new JDABuilder(AccountType.BOT)
				.setToken(token)
				.setEventManager(eventManager)
				.buildAsync();
		return jda;
	}
	
	@Bean
	public WakewordDetector wakewordDetector() throws IOException {
		SnowboyDetect detector = new SnowboyDetect(copyResourceToTemp(commonResource).getAbsolutePath(), 
				copyResourceToTemp(modelResource).getAbsolutePath());
		detector.SetSensitivity(wakewordSensitivity);
	    detector.SetAudioGain(wakewordAudioGain);
	    System.out.println("Wakeword Detector for model " + modelResource.getFilename() + " initialized");
	    return detector;
	}

	@PostConstruct
	public void init() {
		for (EventListener eventListener : eventListeners) {
			eventManager.register(eventListener);
		}
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
}
