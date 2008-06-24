/* 
 * JAXBUtility.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.utilities;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class loads and saves
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class JAXBUtility {

	/**
	 * 
	 */
	private ReaderPluginRegistryService rprs;

	/**
	 * 
	 */
	private static JAXBUtility instance = new JAXBUtility();

	/**
	 * 
	 */
	private JAXBUtility() {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * @return the instance
	 */
	public static JAXBUtility getInstance() {
		return instance;
	}

	/**
	 * @param rprs
	 *            the rprs to set
	 */
	@Inject
	public void setRprs(ReaderPluginRegistryService rprs) {
		this.rprs = rprs;
	}

	/**
	 * Load an xml file.  
	 * 
	 * @param filename
	 * @param o
	 * @return
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Object load(String xml, Class<?> clazz) throws JAXBException {
		JAXBContext context;
		Class[] newClassArr = rprs.getAbstractReaderInfoClasses();
		context = JAXBContext.newInstance(newClassArr);

		Unmarshaller unmarshaller = context.createUnmarshaller();

		return unmarshaller.unmarshal(new StringReader(xml));
	}

	/**
	 * Save an object.  
	 * 
	 * @param o
	 */
	public String save(Object o) {
		StringWriter writer = new StringWriter();
		JAXBContext context;

		try {
			context = JAXBContext.newInstance(o.getClass());
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(o, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		// if(debugToConsole)
		System.out.print(writer);
		return writer.toString();
	}
}
