package com.jonesgeeks.dislexa.avs.rest.auth;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@RestController
public class AuthController {
	
	private @Value("${amazon.auth.endpoint:https://www.amazon.com/ap/oa}") String amazonAuthUrl;
	private @Value("${amazon.auth.clientId}") String clientId;
	private @Value("${amazon.auth.clientSecret}") String clientSecret;
	private @Value("${amazon.auth.scope:alexa:all}") String scope;

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
    public HttpResponse handleRedirect(@RequestParam("code") String code) throws ClientProtocolException, UnsupportedEncodingException, IOException, URISyntaxException {
    	URIBuilder builder = new URIBuilder("https://api.amazon.com/auth/o2/token")
    			.addParameter("grant_type", "authorization_code")
    			.addParameter("code", code)
    			.addParameter("redirect_uri", "http://localhost:3000/authresponse")
    			.addParameter("client_id", clientId)
    			.addParameter("client_secret", clientSecret);
    	
    	System.out.println("Sending to " + builder.build().toString());
    	
    	HttpResponse response = Request.Post(builder.build().toString())
    	                   .execute()
    	                   .returnResponse();
    	
    	return response;
    }
}