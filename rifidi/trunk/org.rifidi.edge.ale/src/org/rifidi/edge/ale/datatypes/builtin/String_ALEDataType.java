/*
 *  String_ALEDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.datatypes.builtin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.ale.datatypes.ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.ALEFormat;
import org.rifidi.edge.ale.datatypes.builtin.formats.StringFormats;

/**
 * This class represents the ISO 15962 String datatype as specified in section
 * 6.2.4 of the ALE 1.1 specification
 * 
 * Since ISO 15962 is not-royalty free, it is unclear how this encoding works
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class String_ALEDataType implements ALEDataType {

	private Log logger = LogFactory.getLog(String_ALEDataType.class);
	private String _data;

	/**
	 * Construct a new String datatype from an ISO 15962-encoded unicode string.
	 * 
	 * @param data
	 *            a unicode String
	 */
	public String_ALEDataType(String data) {
		_data = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ale.datatypes.ALEDataType#getData(java.lang.String)
	 */
	@Override
	public String getData(ALEFormat format) {
		StringFormats stringFormat = Enum.valueOf(StringFormats.class, format
				.getFormatName());
		switch (stringFormat) {
		case STRING:
			return _data;
		}
		logger.error("ALEFormat: " + format
				+ " is not a valid format for String_ALEDatatType");
		throw new IllegalArgumentException("ALEFormat: " + format
				+ " is not a valid format for String_ALEDatatType");
	}

}
