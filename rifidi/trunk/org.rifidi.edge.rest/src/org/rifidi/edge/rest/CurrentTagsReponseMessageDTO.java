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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class CurrentTagsReponseMessageDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5864386747857666489L;
	
	@XmlElementWrapper(required = true, name="currenttags")
	@XmlElement(name = "tag")
	private List<CurrentTagDTO> tags;

	public List<CurrentTagDTO> getTags() {
		return tags;
	}

	public void setTags(List<CurrentTagDTO> tags) {
		this.tags = tags;
	}
}
