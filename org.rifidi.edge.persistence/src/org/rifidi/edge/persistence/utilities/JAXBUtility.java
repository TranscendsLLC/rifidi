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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderInfo;
import org.w3c.dom.Node;

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
	// private ReaderPluginRegistryService rprs;
	/**
	 * The instance of the JAXBUtility.
	 */
	private static JAXBUtility instance = new JAXBUtility();

	/**
	 * Private constructor.
	 */
	private JAXBUtility() {
		// ServiceRegistry.getInstance().service(this);
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
	// @Inject
	// public void setRprs(ReaderPluginRegistryService rprs) {
	// this.rprs = rprs;
	// }
	/**
	 * Loads an object out of xml.
	 * 
	 * @param xml
	 *            The XML file in string form.
	 * @return Returns an object
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public Object load(Node xml) throws JAXBException {
		JAXBContext context;
		Class[] newClassArr = new Class[] { AlienReaderInfo.class,
				LLRPReaderInfo.class };
		
		context = JAXBContext.newInstance(newClassArr);

		Unmarshaller unmarshaller = context.createUnmarshaller();

		return unmarshaller.unmarshal(xml);
	}

	/**
	 * Save an object.
	 * 
	 * @param o
	 *            The object to save.
	 */
	@SuppressWarnings("unchecked")
	public Node save(Object o, Node parent) {
		JAXBContext context;
		logger.debug("Just before the try in save()");

		try {
			Class[] newClassArr = new Class[] { AlienReaderInfo.class,
					LLRPReaderInfo.class };// rprs.getAbstractReaderInfoClasses();

			context = JAXBContext.newInstance(newClassArr);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			m.marshal(o, parent);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return parent;
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
		// This synchronized block is dependent on this class being a singleton.
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
