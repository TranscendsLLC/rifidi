/**
 * 
 */
package org.rifidi.edge.adapter.thinkify50;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class Thinkify50TagHandler {
	private Thinkify50SensorSession session;

	/**
	 * 
	 * @param session
	 */
	public Thinkify50TagHandler(Thinkify50SensorSession session) {
		this.session = session;
	}
	
	/**
	 * 
	 * @param tag
	 */
	public void tagArrived(String tag) {
		System.out.println("Tag: " + tag);
	}
	
	
}
