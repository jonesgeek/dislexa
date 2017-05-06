/**
 * 
 */
package com.jonesgeeks.util;

import java.util.stream.Stream;

/**
 *
 */
public interface Streamable<T> {
	
	/**
	 * 
	 * @return
	 */
	public Stream<T> stream();
	
}
