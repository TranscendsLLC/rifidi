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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
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
 * TODO: Potential problem with this: IP and Port might lose uniqueness
 * depending on which readers were loaded at the time.
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
			readerInfoTypeList = doc.createElement(XMLTags.ELEMENT_LIST_TAG);
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
	 * Gets a list of the readerInfos stored in the XML file under the given
	 * class.
	 * 
	 * @param readerInfoClass
	 * @return
	 */
	public List<ReaderInfo> getReaderInfos(String className) {
		List<ReaderInfo> retVal = new ArrayList<ReaderInfo>();
		Element ele = this.findReaderTypeList(className);
		NodeList nodes = ele.getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			Node nod = nodes.item(i);
			try {
				ReaderInfo ri = (ReaderInfo) JAXBUtility.getInstance()
						.load(nod);
				retVal.add(ri);
			} catch (JAXBException e) {
				// TODO: probably should throw something here
				e.printStackTrace();
			}
		}
		return retVal;
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
		Element readerInfoTypeList = this.findReaderTypeList(readerInfo
				.getClass().getName());
		if (readerInfoTypeList == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		logger.debug("just before the find");
		Element readerInfoNode = find(readerInfo, root);
		logger.debug("just after the find");
		if (readerInfoNode == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		logger.debug("removing the child node");
		this.printToSTIO(readerInfoTypeList);
		readerInfoTypeList.removeChild(readerInfoNode);
		logger.debug("printing to file");
		printToFile();
	}

	/**
	 * 
	 * @param readerInfo
	 * @param readerInfoTypeList
	 * @return
	 */
	private Element find(ReaderInfo readerInfo, Element readerInfoTypeList) {
		logger.debug("In the find");
		printToSTIO(readerInfoTypeList);
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		try {
			logger.debug("Before the exp");
			String exp = new String("/*/*/*");
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
				logger.debug("NodeSet: " + oResult.toString()
						+ " \nlength is: " + ((NodeList) oResult).getLength());
				NodeList nodeArray = (NodeList) oResult;

				Node xResult = this.findUniqueReader(readerInfo.getIpAddress(),
						readerInfo.getPort(), nodeArray);

				if (xResult == null) {
					logger.debug("find returning null after the PORT search");
					return null;
				} else {
					logger.debug("RETURNING A RESULT: " + xResult);
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
	 * 
	 * @param ip
	 * @param port
	 * @param readerList
	 * @return
	 */
	private Element findUniqueReader(String ip, int port, NodeList readerList) {
		for (int i = 0, n = readerList.getLength(); i < n; i++) {
			Node node = readerList.item(i);
			logger.debug("NODE: " + node);
			boolean ip_match = false;
			boolean port_match = false;
			NodeList childNodeList = node.getChildNodes();
			for (int x = 0, m = childNodeList.getLength(); x < m; x++) {
				Node child = childNodeList.item(x);
				logger.debug("CHILD NODE: " + child);
				if (child.getNodeName().equals(XMLTags.IP_ADDRESS)) {
					logger.debug("IP CHILD NODE FOUND");
					if (child.getFirstChild().getNodeValue().equals(ip)) {
						logger.debug("IP IS A MATCH");
						ip_match = true;
					}
				}
				if (child.getNodeName().equals(XMLTags.PORT)) {
					logger.debug("PORT CHILD NODE FOUND");
					if (child.getFirstChild().getNodeValue().equals(
							String.valueOf(port))) {
						logger.debug("PORT IS A MATCH");
						port_match = true;
					}
				}
				if (ip_match && port_match) {
					logger.debug("RETURNING: " + child);
					return (Element) node;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param readerInfoType
	 * @return
	 */
	private Element findReaderTypeList(String readerInfoType) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node result = null;
		try {
			logger.debug("Just before the compile");
			String exp = new String("//" + XMLTags.ELEMENT_LIST_TAG + "[@"
					+ XMLTags.TYPE_TAG + "='" + readerInfoType + "']");
			logger.debug("String expression is: \n" + exp);
			XPathExpression expr = xpath.compile(exp);
			logger.debug("Just after the compile");
			this.printToSTIO(this.doc);
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
		return (Element) result;
	}

	/**
	 * 
	 */
	private Document createDocument() {
		Document dom = null;
		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
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
	private Document createDocument(File xml) {
		Document dom = null;
		// get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			// get an instance of builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// create an instance of DOM
			try {
				logger.debug("Before the parse");
				dom = db.parse(xml);
				logger.debug("After Parse, parse successful");
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
	 * Loads everything into a dom object for testing.
	 */
	public void loadFromFile() {
		File xmlFile = null;
		
		xmlFile = JAXBUtility.getInstance().getXMLFile();

		if (xmlFile != null) {
			logger.debug("FILE NOT NULL, CREATING NEW DOCUMENT");
			this.doc = this.createDocument(xmlFile);
			this.root = (Element) doc.getFirstChild();
		} else {
			logger.debug("FILE IS NULL");
		}
	}

	/**
	 * Prints the document to a file.
	 */
	private void printToFile() {
		logger.debug("Printing the file");
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
	 * Prints the document to a file.
	 */
	private void printToSTIO(Document e) {
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
	private class XMLTags {
		public static final String VALUE = "val";

		public static final String ROOT_TAG = "PersistedReaderInfo";

		public static final String ELEMENT_LIST_TAG = "ReaderInfoList";

		public static final String TYPE_TAG = "ReaderType";

		public static final String IP_ADDRESS = "ipAddress";

		public static final String PORT = "port";
	}
}
