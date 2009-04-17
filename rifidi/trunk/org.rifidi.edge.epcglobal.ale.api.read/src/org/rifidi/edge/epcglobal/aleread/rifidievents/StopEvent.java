/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.rifidievents;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StopEvent {
	/** Name of the associated ec spec. */
	private String name;
	/** Trigger that created this event. */
	private String trigger = null;

	/**
	 * @param name
	 */
	public StopEvent(String name) {
		this.name = name;
	}

	/**
	 * @param name
	 * @param trigger
	 */
	public StopEvent(String name, String trigger) {
		super();
		this.name = name;
		this.trigger = trigger;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the trigger
	 */
	public String getTrigger() {
		return trigger;
	}

	/**
	 * @param trigger the trigger to set
	 */
	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
}
