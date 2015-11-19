/*******************************************************************************
 * Copyright (c) 2015 Transcends, LLC.
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
package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class AppResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2588852972212099383L;

	@XmlElementWrapper(required = true, name="apps")
	@XmlElement(name = "app")
	private List<AppNameDTO> apps;

	public List<AppNameDTO> getApps() {
		return apps;
	}

	public void setApps(List<AppNameDTO> apps) {
		this.apps = apps;
	}
	
	
}
