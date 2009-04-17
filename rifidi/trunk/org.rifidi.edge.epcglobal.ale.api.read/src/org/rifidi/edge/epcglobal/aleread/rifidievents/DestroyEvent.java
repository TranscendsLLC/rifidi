/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.rifidievents;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class DestroyEvent {
	/** Name of the associated ec spec. */
	private String name;

	/**
	 * @param name
	 *            name of the associated ec spec
	 */
	public DestroyEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}
