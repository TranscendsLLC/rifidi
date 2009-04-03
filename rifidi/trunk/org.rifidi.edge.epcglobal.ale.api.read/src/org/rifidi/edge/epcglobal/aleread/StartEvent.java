/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StartEvent {
	/** Name of the associated ec spec. */
	private String name;

	/**
	 * @param name
	 *            name of the associated ec spec
	 */
	public StartEvent(String name) {
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
