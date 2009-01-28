/*
 *  TagMarshaller.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.api.readerplugin.messages.impl;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * This class is a singleton that is used to turn TagMessage objects into XML
 * strings
 * 
 * @author kyle
 * 
 */
public class TagMarshaller {

	/**
	 * The JAXB Marshaller
	 */
	private Marshaller marshaller;
	
	/**
	 * The instance of this Marshaller
	 */
	private static TagMarshaller instance = new TagMarshaller();;

	/**
	 * A private constructor 
	 */
	private TagMarshaller() {
		try {
			JAXBContext context = JAXBContext.newInstance(TagMessage.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return The instance of the TagMarshaller
	 */
	public static TagMarshaller getInstance() {
		return instance;
	}

	/**
	 * Convert a TagMessage into an XML String
	 * @param tm The Tag Message to convert
	 * @return an XML string
	 */
	public String getXML(TagMessage tm) {
		StringWriter writer = new StringWriter();
		try {
			marshaller.marshal(tm, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}

		return writer.toString();
	}

}
