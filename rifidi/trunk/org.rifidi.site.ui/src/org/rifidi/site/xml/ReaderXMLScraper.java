/* 
 * ReaderXMLScraper.java
 *  Created:	Jul 25, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.rifidi.site.ReaderObject;
import org.rifidi.site.constants.ConfigurationConstants;
import org.rifidi.site.constants.PluginConstants;

/**
 * Scrape the XML file.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ReaderXMLScraper {

	/**
	 * 
	 */
	private static ReaderXMLScraper instance = new ReaderXMLScraper();

	/**
	 * 
	 */
	private ReaderXMLScraper() {
	}

	/**
	 * 
	 * 
	 * @return the instance
	 */
	public static ReaderXMLScraper getInstance() {
		return instance;
	}

	/**
	 * Gets the list of reader objects
	 * 
	 * @return
	 */
	public List<ReaderObject> getReaderObjectList() {
		List<ReaderObject> readerObjectList = new ArrayList<ReaderObject>();
		File reader_folder = new File(ConfigurationConstants.READER_FOLDER_PATH);
		File[] file_list = reader_folder.listFiles();
		for (File jar_file : file_list) {
			InputStream reader_xml = this.findXMLInJar(jar_file);
			// File[] jar_file_list = f.listFiles();
			// File reader_xml = null;
			// System.out.println("File f is: " + f.getName());
			// for (File x : jar_file_list) {
			// // TODO: Might be wrong, and might have to break the filename
			// // out from the complete path.
			// if (x.getName().equals(PluginConstants.READER_XML_FILENAME)) {
			// reader_xml = x;
			// }
			// }
			if (reader_xml != null) {
				ReaderObject ro = this.parseReaderXML(jar_file.getName(),
						reader_xml);
				if (ro != null) {
					readerObjectList.add(ro);
				}
			}
		}
		return readerObjectList;
	}

	/**
	 * Finds an xml file in the given file. If the file is not a jar, or if
	 * there is no ReaderPlugin.xml in the jar file, null is returned.
	 * 
	 * @param jarFile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private InputStream findXMLInJar(File jarFile) {
		InputStream retVal = null;
		try {
			JarFile jar = new JarFile(jarFile);
			Enumeration resources = jar.entries();
			while (resources.hasMoreElements()) {
				JarEntry je = (JarEntry) resources.nextElement();
				if (je.getName().matches(".*\\.xml")) {
					System.out.println("XML file: " + je.getName());
					retVal = jar.getInputStream(je);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * Parses the xml file into a ReaderObject
	 * 
	 * @param reader_xml
	 * @return
	 */
	private ReaderObject parseReaderXML(String jar_include_name,
			InputStream reader_xml) {
		// if (!reader_xml.exists()) {
		// return null;
		// }
		String include = jar_include_name;
		String name = null;
		String info = null;
		ReaderObject ro = new ReaderObject();
		try {
			Document doc = new SAXBuilder().build(reader_xml);
			Element root = doc.getRootElement();
			List<Element> eleList = root.getChildren();
			for (Element e : eleList) {
				if (e.getName()
						.equals(PluginConstants.READERPLUGIN_DESCRIPTION)) {
					info = e.getText();
				} else if (e.getName()
						.equals(PluginConstants.READERPLUGIN_NAME)) {
					name = e.getText();
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (name != null && info != null) {
			ro.setDescription(info);
			ro.setInclude(include);
			ro.setName(name);
			return ro;
		} else {
			return null;
		}
	}
}
