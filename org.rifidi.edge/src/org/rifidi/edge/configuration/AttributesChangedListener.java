/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.configuration;
import javax.management.AttributeList;

/**
 * This is an ugly side effect of the fact that we need to notify the client of
 * when the attributes of a configuration is changed. Get rid of this when we
 * switch to using AspecJ
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface AttributesChangedListener {
	/**
	 * Called when attributes have been modified on a Configuration
	 * 
	 * @param configurationID
	 *            The ID of the configuration whose ID was modified
	 * @param attributes
	 *            The attributes that were changed
	 */
	void attributesChanged(String configurationID, AttributeList attributes);
}
