package org.rifidi.edge.core.rmi.client.sessionstub.valueobjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * This method wraps a ReaderInfo XML in order to make accessing data from it
 * easier
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderInfoWrapper {

	private Log logger = LogFactory.getLog(ReaderInfoWrapper.class);

	private Document readerInfo;

	/**
	 * Create a new ReaderInfoWrapper.
	 * 
	 * @param XML
	 *            The readerInfo in XML format
	 */
	public ReaderInfoWrapper(String XML) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(XML.getBytes());
			readerInfo = parser.parse(is);
		} catch (IOException ex) {
			logger.error("Cannot create Reader Info Wrapper", ex);
		} catch (ParserConfigurationException e) {
			logger.error("Cannot create Reader Info Wrapper", e);
		} catch (SAXException e) {
			logger.error("Cannot create Reader Info Wrapper", e);
		}

	}

	/**
	 * Get the value of a particular XML element in the ReaderInfo
	 * 
	 * @param name
	 *            The name of the element
	 * @return The value of the supplied element name
	 */
	public String getValue(String name) {
		if (readerInfo != null) {
			NodeList nl = readerInfo.getDocumentElement().getChildNodes();

			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).getNodeName().equalsIgnoreCase(name)) {
					NodeList values = nl.item(i).getChildNodes();
					for (int j = 0; j < values.getLength(); j++) {
						if (values.item(j).getNodeType() == Node.TEXT_NODE) {
							return ((Text) values.item(j)).getNodeValue();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @return A list of all element names in this readerInfo
	 */
	public ArrayList<String> getElementNames() {
		if (readerInfo != null) {
			ArrayList<String> names = new ArrayList<String>();
			NodeList nl = readerInfo.getDocumentElement().getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				names.add(nl.item(i).getNodeName());
			}
			return names;
		}
		return null;
	}

	/**
	 * 
	 * @return This ReaderInfo as an XML string
	 */
	public String getString() {

		try {
			// Set up the output transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans;

			trans = transfac.newTransformer();

			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// Print the DOM node

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(readerInfo);
			trans.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
		} catch (TransformerException e) {
			logger.error("Cannot create Reader Info Wrapper", e);
		}

		return null;
	}

}
