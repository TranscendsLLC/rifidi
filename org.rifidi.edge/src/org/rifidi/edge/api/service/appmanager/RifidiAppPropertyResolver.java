/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service.appmanager;

import java.util.Properties;

/**
 * This interface is for classes that resolve properties for a given app.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppPropertyResolver {

	/**
	 * Resolve the property for the given app
	 * 
	 * @param appGroup
	 * @param appName
	 * @return
	 */
	Properties reolveProperties(String appGroup, String appName);

}
