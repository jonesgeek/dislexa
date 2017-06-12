/**
 * 
 */
package com.jonesgeeks.dislexa.config;

import java.net.URL;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazon.alexa.avs.ResultListener;
import com.amazon.alexa.avs.http.AVSClient;
import com.amazon.alexa.avs.http.MultipartParser.MultipartParserConsumer;
import com.amazon.alexa.avs.http.ParsingFailedHandler;
import com.amazon.alexa.avs.message.response.Directive;
import com.jonesgeeks.avs.directive.DialogRequestIdAuthority;
import com.jonesgeeks.avs.directive.DirectiveEnqueuer;
import com.jonesgeeks.dislexa.avs.rest.auth.SimpleTokenManager;
import com.jonesgeeks.dislexa.avs.rest.auth.TokenManager;

/**
 *
 */
@Configuration
@ComponentScan({"com.jonesgeeks.dislexa.avs"})
public class AvsConfig {
	private @Autowired ResultListener resultListener;
	private @Autowired ParsingFailedHandler parsingFailedHandler;
	
	@Bean
	public SimpleTokenManager tokenManager() {
		return new SimpleTokenManager();
	}
	
	@Bean
	public AVSClient avsClient(@Value("${amazon.alexa.host:https://avs-alexa-na.amazon.com}") URL avsHost) 
			throws Exception {
		return new AVSClient(avsHost, directiveEnqueuer(), new SslContextFactory(),
                parsingFailedHandler, resultListener);
	}
	
	@Bean 
	public MultipartParserConsumer directiveEnqueuer() {
		return new DirectiveEnqueuer(dialogRequestIdAuthority(), dependentQueue(), independentQueue());
	}
	
	@Bean
	public Queue<Directive> dependentQueue() {
		return new LinkedBlockingDeque<>();
	}
	
	@Bean
	public Queue<Directive> independentQueue() {
		return new LinkedBlockingDeque<>();
	}
	
	@Bean
	public DialogRequestIdAuthority dialogRequestIdAuthority() {
		return DialogRequestIdAuthority.getInstance();
	}
	
	@Bean
	public ParsingFailedHandler parsingFailedHandler() {
		return new ParsingFailedHandler() {

			@Override
			public void onParsingFailed(String unparseable) {
				// TODO send SystemExceptionEncounteredEvent back to AVS
				System.out.printf("Parsing Failed for %s", unparseable);
			}
			
		};
	}
	
	@Bean
	public ResultListener resultListener() {
		return new ResultListener() {
            private boolean setLocaleCalled;
            CountDownLatch loadBeforeSync = new CountDownLatch(2);

            @Override
            public void onSuccess() {
                loadBeforeSync.countDown();
                sendSyncAndLocale();
            }

            @Override
            public void onFailure() {
                loadBeforeSync.countDown();
                sendSyncAndLocale();
            }

            private void sendSyncAndLocale() {
                // Send synchronize state and set location once the file operations have finished.
                if (loadBeforeSync.getCount() <= 0) {
//                    log.info("Start sending SynchronizeStateEvent");
//                    sendSynchronizeStateEvent();
                    if (!setLocaleCalled) {
                        setLocale(Locale.US);
                        setLocaleCalled = true;
                    }
                }
            }
            
            /**
             * Set this device account's locale to the given locale by
             * sending a SettingsUpdated event to AlexaService.
             * @param locale
             */
            public void setLocale(Locale locale) {
//                List<Setting> settings = new ArrayList<>();
//                settings.add(new LocaleSetting(locale.toLanguageTag()));
//                sendRequest(RequestFactory.createSettingsUpdatedEvent(settings));
            }
        };
	}
	
}
