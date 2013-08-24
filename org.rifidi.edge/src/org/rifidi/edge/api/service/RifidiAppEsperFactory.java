/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service;

import java.util.List;

/**
 * A RifidiAppEsperFactory builds esper statements for AppServices to use.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppEsperFactory {

	/**
	 * Use this method to create the necessary statements. Take care that all
	 * windows have unique names, since windows are shared globally across all
	 * rifidi applications.
	 * 
	 * @return A list of esper statements as strings
	 */
	List<String> createStatements();

	/**
	 * This method should return a select statement that can be hooked up to a
	 * StatementAwareUpdateListener
	 * 
	 * @return
	 */
	String createQuery();

}
