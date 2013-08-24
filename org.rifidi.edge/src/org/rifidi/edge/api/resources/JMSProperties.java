/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;

/**
 * Properties used to configure a JMS Resource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface JMSProperties {

	/** The name of the destination */
	public static final String DESTINATION = "Destination";
	/** The type of the destination (queue or topic) */
	public static final String DESTINATION_TYPE = "DestinationType";

}
