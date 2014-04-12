/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
