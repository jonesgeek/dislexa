/**
 * 
 */
package com.jonesgeeks.dislexa.avs.rest.auth;

/**
 * A simple token manager that allows setting and getting of an access token.
 */
public class SimpleTokenManager implements TokenManager {
	private String accessToken;

	/* (non-Javadoc)
	 * @see com.jonesgeeks.dislexa.avs.rest.auth.TokenManager#getAccessToken()
	 */
	@Override
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * @param token the access token to set.
	 */
	public void setAccessToken(String token) {
		accessToken = token;
	}

}
