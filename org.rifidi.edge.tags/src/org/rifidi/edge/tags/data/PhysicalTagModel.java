/*
 *  PhysicalTagModel.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.tags.data;

import java.io.Serializable;

/**
 * A PhysicalTagModel is a representation of the data that is actually stored on
 * the tags. For example, it stores the bits actually collected when the ID of
 * the tag was read.
 * 
 * All PhysicalTagModels should extend this class.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class PhysicalTagModel implements Serializable {

	/**
	 * The SerialVersionID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to identify the type of the tag. All concrete
	 * implementations of this class should return a unique string. It is
	 * recommended that the concrete class simply return its canonical class
	 * name
	 * 
	 * @return A unique string for the concrete implementation
	 */
	public abstract String getTagType();
}
