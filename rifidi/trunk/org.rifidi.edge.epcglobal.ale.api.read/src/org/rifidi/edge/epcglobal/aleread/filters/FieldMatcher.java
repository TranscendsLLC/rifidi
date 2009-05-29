/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.filters;

/**
 * Matcher implementing this interface are used to identify fields in a pattern.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface FieldMatcher {
	/**
	 * Check if the given String is a field.
	 * 
	 * @param field
	 * @return
	 */
	public boolean match(String field);
}
