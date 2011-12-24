/**
 * 
 */
package org.rifidi.edge.tools.diagnostics;

import java.util.List;

/**
 * @author kyle
 *
 */
public interface TagGenerator {
	
	/**
	 * Method to start a runner with the supplied tagBatchID and exposureID
	 * 
	 * @param tagReadDataID
	 * @param exposureID
	 * @return
	 */
	Integer startRunner(String tagSetID, String exposureID);
	
	Integer startRunner(List<AbstractReadData<?>> tags, Exposure exposure);
	
	void stopRunner(Integer runnerID);
	
	void addTagSet(String tagSetID, List<AbstractReadData<?>> tags);
	
	void addExposure(String exposureID, Exposure exposure);

}
