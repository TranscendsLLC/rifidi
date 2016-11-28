package org.rifidi.edge.utils;

public interface RemoveConfig {
	
	/**
	 * this method remove on the path of the webapp the ECSpec file which is undefine
	 * @param specName
	 */
	void removeECSpec(String specName);
	
	/**
	 *  this method remove on the path of the webapp the ECSpec subscriber wich is unsubscribe. if the file is empty
	 * @param specName
	 */
	void removeECSpecSubscriber(String specName, String notificationURI);
	
	/**
	 * this method remove on the path of the webapp each LRSpec which is undefine
	 * @param specName
	 */
	void removeLRSpec(String specName);
		
	/**
	 * this method remove on the path of the webapp each ROSpec which is undefine
	 * @param specName
	 */
	void removeROSpec(String lrSpecName);		
}