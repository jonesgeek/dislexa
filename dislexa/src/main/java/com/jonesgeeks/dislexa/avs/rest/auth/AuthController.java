package com.jonesgeeks.dislexa.avs.rest.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class AuthController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private @Value("${amazon.auth.endpoint:https://www.amazon.com/ap/oa}") String amazonAuthUrl;
	private @Value("${amazon.auth.clientId}") String clientId;
	private @Value("${amazon.auth.clientSecret}") String clientSecret;
	private @Value("${amazon.auth.scope:alexa:all}") String scope;
	
	// TODO: replace this with just a TokenManager when we get a refreshing token manager.
	private @Autowired SimpleTokenManager tokenManager;

    @RequestMapping("/")
    public void handleLogin(HttpServletResponse response) throws URISyntaxException, IOException {
    	URIBuilder builder = new URIBuilder(amazonAuthUrl)
    			.addParameter("client_id", clientId)
    			.addParameter("scope", scope)
    			.addParameter("response_type", "code")
    			.addParameter("redirect_uri", "http://localhost:3000/authresponse");
    	response.sendRedirect(builder.build().toString());
    }
    
    @RequestMapping("/authresponse")
    public ResponseEntity<String> handleRedirect(@RequestParam("code") String code) throws IOException, URISyntaxException {
    	URIBuilder builder = new URIBuilder("https://api.amazon.com/auth/o2/token");
    	
    	logger.info("Sending to " + builder.build().toString());
    	
    	AccessToken token = Request.Post(builder.build().toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Cache-Control", "no-cache")
                .bodyForm(
                        new BasicNameValuePair("grant_type", "authorization_code"),
                        new BasicNameValuePair("code", code),
                        new BasicNameValuePair("redirect_uri", "http://localhost:3000/authresponse"),
                        new BasicNameValuePair("client_id", clientId),
                        new BasicNameValuePair("client_secret", clientSecret)
                )
                .execute()
    	        .handleResponse(httpResponse -> {

                    StatusLine statusLine = httpResponse.getStatusLine();
                    HttpEntity entity = httpResponse.getEntity();
                    if (statusLine.getStatusCode() < 200 & statusLine.getStatusCode() >= 300) {
                        throw new HttpResponseException(
                                statusLine.getStatusCode(),
                                statusLine.getReasonPhrase());
                    }
                    if (entity == null) {
                        throw new ClientProtocolException("Response contains no content");
                    }

                    ContentType contentType = ContentType.getOrDefault(entity);
                    if (!ContentType.APPLICATION_JSON.getMimeType().equals(contentType.getMimeType())) {
                        throw new ClientProtocolException("Unexpected content type:" + contentType);
                    }

                    return new ObjectMapper().readValue(IOUtils.toString(httpResponse.getEntity().getContent()), AccessToken.class);
                });
    	tokenManager.setAccessToken(token.getAccessToken());
    	return new ResponseEntity<>(token.getAccessToken(), HttpStatus.OK);
    }
}