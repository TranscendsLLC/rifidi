/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;


/**
 * A description of a JMS Resource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSResourceDescription extends ResourceDescription implements
		JMSProperties {

	/**
	 * 
	 * @return The name of the destination
	 */
	String getDestinationName() {
		return getProperty(DESTINATION);
	}

	/**
	 * Returns either 'queue' or 'topic'
	 * 
	 * @return The destination type
	 */
	String getDesinationType() {
		return getProperty(DESTINATION_TYPE);
	}

}
