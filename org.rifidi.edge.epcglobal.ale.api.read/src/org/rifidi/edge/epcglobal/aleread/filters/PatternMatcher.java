/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

/**
 * A matcher implementing this interface checks if a given string is a match.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface PatternMatcher {
	/**
	 * Match the string.
	 * 
	 * @param matchee
	 * @return
	 */
	public boolean match(String matchee);
}
