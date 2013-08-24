/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
