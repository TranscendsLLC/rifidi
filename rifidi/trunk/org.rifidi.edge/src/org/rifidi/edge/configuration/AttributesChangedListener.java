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
