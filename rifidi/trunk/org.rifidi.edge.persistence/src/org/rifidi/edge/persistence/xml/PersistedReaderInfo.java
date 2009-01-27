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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.persistence.utilities.JAXBUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Comments considered harmful.
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
	 * The document object
	 */
	private Document doc = null;

	/**
	 * The root element.
	 */
	private Element root;

	/**
	 * The PersistedReaderInfo constructor.
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
	public boolean addReaderInfo(ReaderInfo readerInfo) {
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass()
				.getName());
		if (readerInfoTypeList == null) {
			readerInfoTypeList = doc.createElement(XMLTags.ELEMENT_LIST_TAG);
			readerInfoTypeList.setAttribute(XMLTags.TYPE_TAG, readerInfo
					.getClass().getName());
			root.appendChild(readerInfoTypeList);
		}
		if (this.find(readerInfo) == null) {
			JAXBUtility.getInstance().save(readerInfo, readerInfoTypeList);
			printToFile();
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Gets a list of the readerInfos stored in the XML file under the given
	 * class.
	 * 
	 * @param readerInfoClass
	 * @return
	 */
	/*
	 * Synchronized block to prevent two threads from calling this method at the
	 * same time. If they do, XPath can fall apart.
	 */
	public synchronized List<ReaderInfo> getReaderInfos(String className) {
		List<ReaderInfo> retVal = new ArrayList<ReaderInfo>();
		Element ele = this.findReaderTypeList(className);
		if (ele != null) {
			NodeList nodes = ele.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node nod = nodes.item(i);
				if (nod instanceof Element) {
					try {
						// this.printToSTIO(nod);
						ReaderInfo ri = (ReaderInfo) JAXBUtility.getInstance()
								.load(nod);
						retVal.add(ri);
					} catch (JAXBException e) {
						// TODO: probably should throw something here
						e.printStackTrace();
					}
				}
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
	public Element removeReader(ReaderInfo readerInfo)
			throws RifidiReaderInfoNotFoundException {
		Element readerInfoTypeList = this.findReaderTypeList(readerInfo
				.getClass().getName());
		if (readerInfoTypeList == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		Element readerInfoNode = find(readerInfo);
		if (readerInfoNode == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		Element e = (Element)readerInfoTypeList.removeChild(readerInfoNode);
		printToFile();
		return e;
	}

	/**
	 * Returns true if a reader with an IP and port already exists
	 * 
	 * @param ip
	 * @param port
	 * @return
	 */
	private Element find(ReaderInfo readerInfo) {
		return find(readerInfo.getIpAddress(), readerInfo.getPort(), readerInfo
				.getClass().getName());
	}

	/**
	 * This will attempt to find a readerInfo in the XML data.
	 * 
	 * @param readerInfo
	 *            The reader we want to find. The information we will use from
	 *            this class is the class name, the IP, and the port.
	 * @param readerInfoTypeList
	 *            This is a list of ReaderInfo datas in XML form. The class
	 *            already matches
	 * @return
	 */
	private Element find(String ip, int port, String readerInfoClass) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Element readerInfoTypeList = findReaderTypeList(readerInfoClass);
		try {
			String exp = new String("/*/*/*");
			XPathExpression expr = xpath.compile(exp);
			Object oResult = expr.evaluate(readerInfoTypeList,
					XPathConstants.NODESET);
			if (oResult == null) {
				return null;
			} else {
				NodeList nodeArray = (NodeList) oResult;

				Node xResult = this.findUniqueReader(ip, port, nodeArray);

				if (xResult == null) {
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
	 * Finds a unique reader based on the IP and port given.
	 * 
	 * @param ip
	 *            The IP of the reader you want to find.
	 * @param port
	 *            The port of the reader you want to find.
	 * @param readerList
	 *            The list of readers that will be searched to find the reader.
	 * @return The element representing the reader if it was found, null
	 *         otherwise.
	 */
	private Element findUniqueReader(String ip, int port, NodeList readerList) {
		for (int i = 0, n = readerList.getLength(); i < n; i++) {
			Node node = readerList.item(i);
			boolean ip_match = false;
			boolean port_match = false;
			NodeList childNodeList = node.getChildNodes();
			for (int x = 0, m = childNodeList.getLength(); x < m; x++) {
				Node child = childNodeList.item(x);
				if (child.getNodeName().equals(XMLTags.IP_ADDRESS)) {
					if (child.getFirstChild().getNodeValue().equals(ip)) {
						ip_match = true;
					}
				}
				if (child.getNodeName().equals(XMLTags.PORT)) {
					if (child.getFirstChild().getNodeValue().equals(
							String.valueOf(port))) {
						port_match = true;
					}
				}
				if (ip_match && port_match) {
					return (Element) node;
				}
			}
		}
		return null;
	}

	/**
	 * Find the readerTypeList.
	 * 
	 * @param readerInfoType
	 * @return
	 */
	private Element findReaderTypeList(String readerInfoType) {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		Node result = null;
		try {
			String exp = new String("//" + XMLTags.ELEMENT_LIST_TAG + "[@"
					+ XMLTags.TYPE_TAG + "='" + readerInfoType + "']");
			XPathExpression expr = xpath.compile(exp);
			Object oResult = expr.evaluate(this.doc, XPathConstants.NODE);
			if (oResult == null) {
				return null;
			} else {
				result = (Node) oResult;
			}
		} catch (XPathExpressionException e) {
			logger.error("Error in persistance", e);
		}
		return (Element) result;
	}

	/**
	 * Creates a new Dom document.
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
	 * Creates a document based on the xml file given.
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
				dom = db.parse(xml);
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
		
		logger.debug("Persistance File: " + xmlFile.getAbsolutePath());

		if (xmlFile != null) {
			this.doc = this.createDocument(xmlFile);
			this.root = (Element) doc.getFirstChild();
		} else {
			logger.debug("FILE IS NULL");
		}
	}

	/**
	 * Sets the file that this class will use to build the DOM document.
	 * 
	 * @param path
	 *            Sets the path for the folders that the file will use. The
	 *            folders have to be created separately.
	 * @param filename
	 *            The complete path for the file. This is the file that the
	 *            class will create.
	 * @throws IOException
	 */
	public void setFile(String path, String filename) throws IOException {
		JAXBUtility.getInstance().setFile(path, filename);
		this.loadFromFile();
	}

	/**
	 * Prints the document to a file.
	 */
	private void printToFile() {
		// print
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;

		try {
			trans = transfac.newTransformer();

			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			StreamResult result = new StreamResult(JAXBUtility.getInstance()
					.getXMLFile());

			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * The tags for the XML document that we will search for via Dom and XPath.
	 * 
	 * @author Matthew Dean - matt@pramari.com
	 */
	private class XMLTags {
		/**
		 * 
		 */
		public static final String VALUE = "val";

		/**
		 * The Root tag for the xml.
		 */
		public static final String ROOT_TAG = "PersistedReaderInfo";

		/**
		 * The tag which contains a list of several ReaderInfo objects, all of
		 * the same ReaderType.
		 */
		public static final String ELEMENT_LIST_TAG = "ReaderInfoList";

		/**
		 * The ReaderType, which is represented by a package-qualified name of
		 * the ReaderInfo class.
		 */
		public static final String TYPE_TAG = "ReaderType";

		/**
		 * The ip tag we will look for
		 */
		public static final String IP_ADDRESS = "ipAddress";

		/**
		 * The port tag we will look for
		 */
		public static final String PORT = "port";
	}
}
