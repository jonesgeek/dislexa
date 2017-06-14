/**
 * 
 */
package com.jonesgeeks.dislexa.avs.rest.auth;

/**
 *
 */
public interface TokenManager {

	/**
	 * @return the access token
	 */
	String authorize(String code);

	/**
	 * @return the access token
	 */
	String getAccessToken();
}
