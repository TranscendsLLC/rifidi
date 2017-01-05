package org.rifidi.edge.utils;

import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.alelr.LRSpec;

public interface WriteConfig {
	
	/**
	 * this method write on the path of the webapp each ECSpec loaded in the ALE.
	 * @param specName
	 * @param spec
	 */
	void writeECSpec(String specName, ECSpec spec);
	
	/**
	 *  this method write on the path of the webapp each ECSpec subscriber loaded in the ALE.
	 * @param specName
	 * @param notificationURI
	 */
	void writeECSpecSubscriber(String specName, String notificationURI);
	
	/**
	 * this method write on the path of the webapp each LRSpec loaded in the ALE.
	 * @param specName
	 * @param spec
	 */
	void writeLRSpec(String specName, LRSpec spec);
	
	/**
	 * this method write on the path of the webapp the ADD_ROSPEC loaded in the ALE.
	 * @param specName the logical reader name
	 * @param addRoSpec the ADD_ROSPEC to save
	 */
	void writeAddROSpec(String specName, ADD_ROSPEC addRoSpec);
	
	/**
	 * this method write on the path of the webapp the ADD_ROSPEC loaded in the ALE.
	 * @param specName the logical reader name
	 * @param addAccessSpec the ADD_ACCESSSPEC to save
	 */
	void writeAddAccessSpec(String specName, ADD_ACCESSSPEC addAccessSpec);
}

