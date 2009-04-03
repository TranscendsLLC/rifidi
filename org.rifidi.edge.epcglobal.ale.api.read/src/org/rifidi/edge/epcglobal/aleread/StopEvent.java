/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StopEvent {
	/** Name of the associated ec spec. */
	private String name;

	/**
	 * @param name
	 */
	public StopEvent(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
