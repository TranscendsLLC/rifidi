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
package org.rifidi.edge.adapter.awid.awid2010.communication;

import org.rifidi.edge.notification.TagReadEvent;

/**
 * This is an interfaces for classes that extend AbstractAwidMessage which
 * provide TagData to read. It provides a common interface to extract tag data
 * from an AbstractAwidMessage.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface TagResponseMessage {

	/**
	 * Parse the data held in this awid message into a tagReadEvent object.
	 * 
	 * @return A TagReadEvent that represents tag data held in this message.
	 */
	public TagReadEvent getTagReadEvent();

}
