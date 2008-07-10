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
package org.rifidi.edge.persistence.xml;

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
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.persistence.utilities.JAXBUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This is SPARTA!
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
	private Document doc = null;

	/**
	 * 
	 */
	private Element root;

	/**
	 * 
	 */
	public PersistedReaderInfo() {
		doc = createDocument();
		root = doc.createElement(XMLTags.ROOT_TAG);
		doc.appendChild(root);
	}

	/**
	 * Adds the given reader information to the XML object.
	 * 
	 * @param readerInfo
	 */
	public void addReaderInfo(ReaderInfo readerInfo) {
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass()
				.getName());
		if (readerInfoTypeList == null) {
			logger.debug("Got into the reader is null area");
			readerInfoTypeList = doc.createElement(XMLTags.ELEMENT_TAG);
			readerInfoTypeList.setAttribute(XMLTags.TYPE_TAG, readerInfo
					.getClass().getName());
			root.appendChild(readerInfoTypeList);
			logger.debug("Leaving the reader is null area");
		}
		logger.debug("Creating the reader info node");
		JAXBUtility.getInstance().save(readerInfo, readerInfoTypeList);
		logger.debug("Just before the print to file");
		printToFile();
	}

	/**
	 * Removes the reader from the persistence layer.
	 * 
	 * @param readerInfo
	 * @throws RifidiReaderInfoNotFoundException
	 */
	public void removeReader(ReaderInfo readerInfo)
			throws RifidiReaderInfoNotFoundException {
		logger.debug("calling the remove reader");
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass()
				.getName());
		if (readerInfoTypeList == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		logger.debug("just before the find");
		Element readerInfoNode = find(readerInfo, root);
		logger.debug("just after the find");
		if (readerInfoNode == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		readerInfoTypeList.removeChild(readerInfoNode);
		printToFile();
	}

	/**
	 * 
	 * @param xml
	 */
	public void parsePersistanceXMLFile(File xml) {
		doc = this.createDocument(xml);
	}

	/**
	 * 
	 * 
	 * @param readerInfo
	 * @param readerInfoTypeList
	 * @return
	 */
	public Element find(ReaderInfo readerInfo, Element readerInfoTypeList) {

		logger.debug("In the find");
		printToSTIO(readerInfoTypeList);
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			logger.debug("Before the exp");
			// String exp = new String("/*/*/*[" + XMLTags.IP_ADDRESS + "="
			// + readerInfo.getIpAddress() + "]");
			String exp = new String("/*/*");
			logger.debug("XPATH expression in the FIND method is: \n" + exp);
			XPathExpression expr = xpath.compile(exp);
			logger.debug("TypeList: " + readerInfoTypeList.toString());
			logger.debug("Before the evaluate");
			Object oResult = expr.evaluate(readerInfoTypeList,
					XPathConstants.NODESET);
			logger.debug("After the evaluate");
			if (oResult == null) {
				logger.debug("find returning null after the IPADDRESS search");
				return null;
			} else {
				//String exp2 = new String("/*[" + XMLTags.PORT + "="
				//		+ readerInfo.getPort() + "]");
				String exp2 = new String("/*");
				logger.debug("XPATH expression in the INNER FIND method is: \n"
						+ exp2);
				logger.debug("NodeSet: " + oResult.toString()
						+ " \nlength is: " + ((NodeList) oResult).getLength());
				NodeList ipMatchList = (NodeList) oResult;
				for (int i = 0, n = ipMatchList.getLength(); i < n; i++) {
					Node node = ipMatchList.item(i);
					logger.debug("NODE: " + node);
				}
				logger.debug("Just before the compile");
				XPathExpression expr2 = xpath.compile(exp2);
				logger.debug("Just before the evaluate");
				Object xResult = expr2.evaluate(ipMatchList,
						XPathConstants.NODE);
				if (xResult == null) {
					logger.debug("find returning null after the PORT search");
					return null;
				} else {
					return (Element) xResult;
				}
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param readerInfoType
	 * @return
	 */
	public Element findReaderTypeList(String readerInfoType) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node result = null;
		try {
			logger.debug("Just before the compile");
			String exp = new String("//" + XMLTags.ELEMENT_TAG + "[@"
					+ XMLTags.TYPE_TAG + "='" + readerInfoType + "']");
			logger.debug("String expression is: \n" + exp);
			XPathExpression expr = xpath.compile(exp);
			logger.debug("Just after the compile");
			Object oResult = expr.evaluate(this.doc, XPathConstants.NODE);
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

	/**
	 * 
	 */
	private Document createDocument(File xmlFile) {
		Document dom = null;
		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			try {
				dom = db.parse(xmlFile);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException pce) {
			// dump it
			logger.debug("Error while trying to "
					+ "instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return dom;
	}

	/**
	 * Prints the document to a file.
	 */
	private void printToFile() {
		logger.debug("Printing in the file");
		try {
			// print
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File("cuddles.xml")), format);

			// XMLSerializer serializer = new XMLSerializer(System.out, format);

			serializer.serialize(doc);

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Prints the document to a file.
	 */
	private void printToSTIO(Element e) {
		logger.debug("Printing in the file");
		try {
			// print
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);

			// XMLSerializer serializer = new XMLSerializer(new
			// FileOutputStream(
			// new File("cuddles.xml")), format);

			XMLSerializer serializer = new XMLSerializer(System.out, format);

			serializer.serialize(e);

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

		public static final String IP_ADDRESS = "ipAddress";

		public static final String PORT = "port";
	}
}
