/* 
 * PersistedReaderInfo.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.betterpersist.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.betterpersist.utilities.JAXBUtility;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This is
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class PersistedReaderInfo {

	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory
			.getLog(PersistedReaderInfo.class);

	/**
	 * 
	 */
	private Document persistMap = null;

	/**
	 * 
	 */
	private Element root;

	/**
	 * 
	 */
	public PersistedReaderInfo() {
		persistMap = createDocument();
		root = persistMap.createElement(XMLTags.ROOT_TAG);
		persistMap.appendChild(root);
	}

	/**
	 * 
	 * @param readerInfo
	 */
	public void addReaderInfo(ReaderInfo readerInfo) {
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass()
				.getName());
		if (readerInfoTypeList == null) {
			logger.debug("Got into the reader is null area");
			readerInfoTypeList = persistMap.createElement(XMLTags.ELEMENT_TAG);
			readerInfoTypeList.setAttribute(XMLTags.TYPE_TAG, readerInfo
					.getClass().getName());
			root.appendChild(readerInfoTypeList);
			logger.debug("Leaving the reader is null area");
		}
		logger.debug("Creating the reader info node");
		// Element readerInfoNode =
		JAXBUtility.getInstance().save(readerInfo, readerInfoTypeList);
		// readerInfoTypeList.appendChild(readerInfoNode);
		logger.debug("Just before the print to file");
		printToFile();
	}

	public void removeReader(ReaderInfo readerInfo)
			throws RifidiReaderInfoNotFoundException {
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass()
				.getName());
		if (readerInfoTypeList == null) {
			throw new RifidiReaderInfoNotFoundException();
		}

		Element readerInfoNode = find(readerInfo, root);
		if (readerInfoNode == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		readerInfoTypeList.removeChild(readerInfoNode);
		printToFile();

	}

	public void parsePersistanceXMLFile(File xml) {
		// TODO parse XML file into DOM structure
	}

	private Element find(ReaderInfo readerInfo, Element readerInfoTypeList) {
		// TODO: Matt use XPath to find
		return null;
	}

	private Element findReaderTypeList(String readerInfoType) {
		// TODO: Matt use XPATH to return the List of Reader types
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node result = null;
		try {
			logger.debug("Just before the compile");
			XPathExpression expr = xpath.compile(new String("/"
					+ XMLTags.ROOT_TAG + "/" + XMLTags.ELEMENT_TAG + "["
					+ XMLTags.VALUE + "='" + readerInfoType + "']"));
			logger.debug("Just after the compile");
			Object oResult = expr
					.evaluate(this.persistMap, XPathConstants.NODE);
			logger.debug("Just after the evaluate");
			if (oResult == null) {
				logger.debug("returning null");
				return null;
			} else {
				logger.debug("Not null, Casting the result");
				result = (Node) oResult;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		logger.debug("Just before the return in the reader type list.  ");
		// TODO: is this OK? I read it was ok online?
		return (Element) result;
	}

	/**
	 * 
	 */
	private Document createDocument() {
		Document dom = null;
		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			dom = db.newDocument();

		} catch (ParserConfigurationException pce) {
			// dump it
			logger.debug("Error while trying to "
					+ "instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return dom;
	}

	private void printToFile() {
		logger.debug("Printing in the file");
		try {
			// print
			OutputFormat format = new OutputFormat(persistMap);
			format.setIndenting(true);

			 XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File("cuddles.xml")), format);

			//XMLSerializer serializer = new XMLSerializer(System.out, format);

			serializer.serialize(persistMap);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	public class XMLTags {
		public static final String VALUE = "val";

		public static final String ROOT_TAG = "PersistedReaderInfo";

		public static final String ELEMENT_TAG = "ReaderInfoList";

		public static final String TYPE_TAG = "ReaderType";
	}
}
