/* 
 * PersistedReaderInfo2.java
 *  Created:	Jul 12, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.persistence.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.persistence.utilities.JAXBUtility;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class PersistedReaderInfo2 {
	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory
			.getLog(PersistedReaderInfo2.class);

	/**
	 * 
	 */
	private Document doc = null;

	/**
	 * 
	 */
	private Element root;

	/**
	 * 
	 */
	private XMLOutputter serializer;

	/**
	 * Constructor.
	 */
	public PersistedReaderInfo2() {
		logger.debug("Creating a new XML document");
		this.createDocument(doc);
	}

	/**
	 * Create the document
	 * 
	 * @param newDoc
	 * @param file
	 */
	private void createDocument(Document newDoc) {
		root = new Element(XMLTags.ROOT_TAG);
		newDoc = new Document(root);
	}

	/**
	 * 
	 * 
	 * @param xmlFile
	 */
	public void parseFile(File xmlFile) {
		this.createDocument(doc, xmlFile);
	}

	/**
	 * 
	 * @param newDoc
	 * @param file
	 */
	private void createDocument(Document newDoc, File file) {
		SAXBuilder saxb = new SAXBuilder();
		try {
			newDoc = saxb.build(file);
			root = (Element) newDoc.getRootElement().getChildren().get(0);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the given reader information to the XML object.
	 * 
	 * @param readerInfo
	 */
	public void addReaderInfo(ReaderInfo readerInfo) {
		List<?> root_children = root.getChildren();
		Element result = this.findReaderInfoTypeList(readerInfo, root_children);
		if (result == null) {
			Element newListElement = new Element(XMLTags.ELEMENT_LIST_TAG);
			newListElement.setAttribute(new Attribute(XMLTags.TYPE_TAG,
					readerInfo.getClass().getName()));
			root.addContent(newListElement);
			this.printToSTDIO(doc);
			result = newListElement;
		}
		//DOMOutputter domout = new DOMOutputter();
		// try {
		// org.w3c.dom.Element domResult = domout.output(result);
		// JAXBUtility.getInstance().save(readerInfo, result);
		// } catch (JDOMException e) {
		// e.printStackTrace();
		//		 }
	}

	/**
	 * Returns null if the readertypelist doesn't
	 * 
	 * @return
	 */
	private Element findReaderInfoTypeList(ReaderInfo info, List<?> elements) {
		for (Object o : elements) {
			Element ele = (Element) o;
			if (ele.getAttribute(XMLTags.TYPE_TAG).getValue().equals(
					info.getClass().getName())) {
				return ele;
			}
		}
		return null;
	}

	/**
	 * Loads everything into a dom object for testing.
	 */
	public void loadFromFile() {
		File xmlFile = null;
		try {
			xmlFile = JAXBUtility.getXMLFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (xmlFile != null) {
			logger.debug("FILE NOT NULL, CREATING NEW DOCUMENT");
			this.createDocument(doc, xmlFile);
		} else {
			logger.debug("FILE IS NULL");
		}
	}

	/**
	 * Print the document to STDIO.
	 * 
	 * @param doc
	 */
	private void printToSTDIO(Document doc) {
		try {
			this.serializer.output(doc, System.out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A collection of XML tags that will be used in the XML file.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class XMLTags {
		public static final String VALUE = "val";

		public static final String ROOT_TAG = "PersistedReaderInfo";

		public static final String ELEMENT_LIST_TAG = "ReaderInfoList";

		public static final String TYPE_TAG = "ReaderType";

		public static final String IP_ADDRESS = "ipAddress";

		public static final String PORT = "port";
	}
}
