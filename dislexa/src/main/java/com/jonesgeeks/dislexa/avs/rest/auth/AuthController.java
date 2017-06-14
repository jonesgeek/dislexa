package com.jonesgeeks.dislexa.avs.rest.auth;

import org.apache.http.client.utils.URIBuilder;
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
	private @Value("${amazon.auth.scope:alexa:all}") String scope;

    private @Autowired TokenManager tokenManager;

    @RequestMapping("/")
    public void handleLogin(HttpServletResponse response) throws URISyntaxException, IOException {
        if (tokenManager.getAccessToken() != null) {
            logger.info("Already have the access token.");
            response.sendRedirect("http://localhost:3000/success");
        } else {
            logger.info("Redirecting to Amazon login.");
            URIBuilder builder = new URIBuilder(amazonAuthUrl)
                    .addParameter("client_id", clientId)
                    .addParameter("scope", scope)
                    .addParameter("response_type", "code")
                    .addParameter("redirect_uri", "http://localhost:3000/authresponse");
            response.sendRedirect(builder.build().toString());
        }
    }
    
    @RequestMapping("/authresponse")
    public ResponseEntity<String> handleRedirect(@RequestParam("code") String code) throws IOException, URISyntaxException {
        String token = tokenManager.authorize(code);

    	return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping("/success")
    public ResponseEntity<String> handleSuccess() {
        String token = tokenManager.getAccessToken();

        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}