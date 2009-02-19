/**
 * 
 */
package org.rifidi.configuration;

/**
 * Interface for rifidi services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface RifidiService {
	/**
	 * Id of the service. This ID has be unique for the whole application.
	 * 
	 * @return
	 */
	String getID();

	/**
	 * Set the ID of the service. This can only be done once. Further calls have
	 * to be ignored.
	 * 
	 * @param id
	 */
	void setID(String id);
}
