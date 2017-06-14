package com.jonesgeeks.dislexa.avs.rest.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Suppliers;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.prefs.Preferences;

/**
 * TODO: This class works. Can use some polish / error handling.
 */
@Component
public class RefreshingTokenManager implements TokenManager {
	private @Value("${amazon.auth.endpoint:https://api.amazon.com/auth/o2/token}") String amazonTokenAuthUrl;
	private @Value("${amazon.auth.clientId}") String clientId;
	private @Value("${amazon.auth.clientSecret}") String clientSecret;

	private @Value("${cache.avs.token.refreshAfterWrite: 2700}") long avsRefreshAfterWrite;
	private @Value("${cache.avs.token.expireAfterWrite: 3600}") long avsExpireAfterWrite;
	private @Value("${cache.avs.maxSize: 10}") long avsCacheMaxSize;

	private static final String REFRESH_TOKEN_KEY = "refreshToken";

	private Preferences avsTokenPreferences = Preferences.userRoot();

	private final Supplier<String> accessTokenCache = Suppliers.memoizeWithExpiration (
			() -> {
				String refreshToken = avsTokenPreferences.get(REFRESH_TOKEN_KEY, null);
				try {
					TokenResponse response = postTokenRequest(
							new BasicNameValuePair("grant_type", "refresh_token"),
							new BasicNameValuePair("refresh_token", refreshToken),
							new BasicNameValuePair("client_id", clientId),
							new BasicNameValuePair("client_secret", clientSecret)
					);
					return response.getAccessToken();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			},
			3600,
			TimeUnit.SECONDS
	);

	@Override
	public String authorize(String code) {

		TokenResponse response = null;
		try {
			response = postTokenRequest(
                    new BasicNameValuePair("grant_type", "authorization_code"),
                    new BasicNameValuePair("code", code),
                    new BasicNameValuePair("redirect_uri", "http://localhost:3000/authresponse"),
                    new BasicNameValuePair("client_id", clientId),
                    new BasicNameValuePair("client_secret", clientSecret)
            );
		} catch (IOException e) {
			throw new RuntimeException(e); // TODO: I am tired!!!
		}

		avsTokenPreferences.put(REFRESH_TOKEN_KEY, response.getRefreshToken());

		return response.getAccessToken();
	}

	@Override
	public String getAccessToken() {
		String refreshToken = avsTokenPreferences.get(REFRESH_TOKEN_KEY, null);
		if (refreshToken == null) {
			// TODO: Throw exception?
			return null;
		}

		return this.accessTokenCache.get();
	}

	private TokenResponse postTokenRequest(NameValuePair... formParams) throws IOException {
		return Request.Post(amazonTokenAuthUrl)
				.addHeader("Content-Type", "application/x-www-form-urlencoded")
				.addHeader("Cache-Control", "no-cache")
				.bodyForm(formParams)
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

					ObjectMapper objectMapper = new ObjectMapper();
					objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
					return objectMapper.readValue(IOUtils.toString(httpResponse.getEntity().getContent()), TokenResponse.class);
				});
	}

	private static class TokenResponse {
		private String accessToken;
		private String refreshToken;
		private String tokenType;
		private int expiresIn;

		@JsonCreator
		public TokenResponse(
				@JsonProperty("access_token") String accessToken,
				@JsonProperty("refresh_token") String refreshToken,
				@JsonProperty("token_type") String tokenType,
				@JsonProperty("expires_in") int expiresIn) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.tokenType = tokenType;
			this.expiresIn = expiresIn;
		}

		String getAccessToken() {
			return accessToken;
		}

		String getRefreshToken() {
			return refreshToken;
		}

		public String getTokenType() {
			return tokenType;
		}

		public int getExpiresIn() {
			return expiresIn;
		}
	}
}
