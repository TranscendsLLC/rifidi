/*
 *  TagData.java
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
import java.util.HashMap;

/**
 * This class represents a tag read from a reader. The physicalModel holds the
 * data that was on the tag. The metaData hold information about the tag read,
 * such as timestamps or velocity information for example.
 * 
 * Note that this class is serializable and can be sent over the wire.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagData implements Serializable {

	/**
	 * The serialized ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This object holds the data that is on the tag that was read
	 */
	private PhysicalTagModel _model;

	/**
	 * This object holds information gathered when the tag was read that was not
	 * part of the data on the tag, such as timestamps or velocity information
	 * 
	 * The keys are the names of the kind of data and the values are the data
	 * themselves. For example one entry might be <"lastSeenTime", "112312423">
	 */
	private HashMap<String, String> _metaData = new HashMap<String, String>();

	/**
	 * Construct a new TagData using a model
	 * 
	 * @param model
	 *            The data on the tag that was read
	 * @throws IllegalArgumentException
	 *             if model is null
	 */
	public TagData(PhysicalTagModel model) {
		if (null == model) {
			throw new IllegalArgumentException(
					"PhysicalTagModel cannot be null");
		}
		_model = model;
	}

	/**
	 * Gets the object that holds the data that is on the actual tag
	 * 
	 * @return the physicalTagModel associated with this tag read
	 */
	public PhysicalTagModel getPhysicalTagModel() {
		return _model;
	}

	/**
	 * Adds a key-value pair to the list of meta data associated with this tag
	 * read. The meta data is extra information associated with the tag read.
	 * 
	 * @param metaDataName
	 *            the kind of data, for example "Last Seen Time"
	 * @param metaDataValue
	 *            the data iteself, for example the timestamp
	 */
	public void addMetaData(String metaDataName, String metaDataValue) {
		_metaData.put(metaDataName, metaDataValue);
	}

	/**
	 * Sets the list of meta data associated with this tag read. The meta data
	 * is extra information associated with the tag read.
	 * 
	 * @param metaData
	 *            The data to set. The keys are the meta data names, and the
	 *            values are the data themselves
	 * @throws IllegalArgumentException
	 *             if metaData is null
	 */
	public void setMetaData(HashMap<String, String> metaData) {
		if (null == metaData) {
			throw new IllegalArgumentException("MetaData cannot be null");
		}

		_metaData = metaData;
	}

	/**
	 * Returns a meta data object with the given name
	 * 
	 * @param metatDataName
	 *            The name of the meta data to get, for example "last seen time"
	 * @return a String representation of the meta data
	 */
	public String getMetaData(String metatDataName) {
		return _metaData.get(metatDataName);
	}

}
