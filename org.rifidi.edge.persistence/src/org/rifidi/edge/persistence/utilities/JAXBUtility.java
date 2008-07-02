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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class loads and saves.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class JAXBUtility {

	/**
	 * The log4j logger.
	 */
	private static Log logger = LogFactory.getLog(JAXBUtility.class);

	/**
	 * The ReaderPluginRegistryService, which we use to get the list of classes.
	 */
	private ReaderPluginRegistryService rprs;

	/**
	 * The instance of the JAXBUtility.
	 */
	private static JAXBUtility instance = new JAXBUtility();

	/**
	 * Private constructor.
	 */
	private JAXBUtility() {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * Gets the Singleton instance of the JAXBUtility.
	 * 
	 * @return the instance
	 */
	public static JAXBUtility getInstance() {
		return instance;
	}

	/**
	 * Set the ReaderPluginRegistryService via injection.
	 * 
	 * @param rprs
	 *            the rprs to set
	 */
	@Inject
	public void setRprs(ReaderPluginRegistryService rprs) {
		this.rprs = rprs;
	}

	/**
	 * Loads an object out of xml.
	 * 
	 * @param xml
	 *            The XML file in string form.
	 * @return Returns an object
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Object load(String xml) throws JAXBException {
		JAXBContext context;
		Class[] newClassArr = rprs.getAbstractReaderInfoClasses();
		List<Class> classList = new ArrayList<Class>();
		for (Class a : newClassArr) {
			classList.add(a);
		}
		classList.add(AbstractReaderInfoSuite.class);

		Class[] omg = new Class[classList.size()];
		int index = 0;
		for (Class c : classList) {
			omg[index] = c;
			index++;
		}

		context = JAXBContext.newInstance(omg);

		Unmarshaller unmarshaller = context.createUnmarshaller();

		return unmarshaller.unmarshal(new StringReader(xml));
	}

	/**
	 * Save an object.
	 * 
	 * @param o
	 *            The object to save.
	 */
	@SuppressWarnings("unchecked")
	public String save(Object o) {
		StringWriter writer = new StringWriter();
		JAXBContext context;

		try {
			Class[] newClassArr = rprs.getAbstractReaderInfoClasses();
			List<Class> classList = new ArrayList<Class>();
			for (Class a : newClassArr) {
				classList.add(a);
			}
			classList.add(AbstractReaderInfoSuite.class);

			Class[] omg = new Class[classList.size()];
			int index = 0;
			for (Class c : classList) {
				omg[index] = c;
				index++;
			}

			context = JAXBContext.newInstance(omg);
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

	/**
	 * Saves a string-based xml to a file.
	 * 
	 * @param xml
	 *            The XML to save.
	 * @param filename
	 *            The file to save to.
	 */
	public void saveToFile(String xml, String filename) {
		//This synchronized block is dependent on this class being a singleton.  
		synchronized (this) {
			File f = new File(filename);
			if (f.exists()) {
				f.delete();
			}

			logger.debug("path is: " + f.getAbsolutePath());
			FileWriter fw = null;
			try {
				logger.debug("XML: " + xml);
				f.createNewFile();
				fw = new FileWriter(f);
				fw.write(xml);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fw != null) {
					try {
						fw.flush();
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Saves a string-based xml to a file.
	 * 
	 * @param xml
	 * @param filename
	 */
	public String restoreFromFile(String filename) throws FileNotFoundException {
		String retVal = null;
		try {
			retVal = new String();
			logger.debug("filename: " + filename);
			File file = new File(filename);
			if (!file.exists()) {
				throw new FileNotFoundException("File does not exist: "
						+ filename);
			}
			FileReader fr = new FileReader(file);

			StringBuffer sb = new StringBuffer();
			int temp = fr.read();
			while (temp != -1) {
				sb.append((char) temp);
				temp = fr.read();
			}
			retVal = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}
}
