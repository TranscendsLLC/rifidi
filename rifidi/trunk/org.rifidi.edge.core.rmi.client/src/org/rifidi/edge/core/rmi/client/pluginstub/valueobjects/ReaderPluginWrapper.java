package org.rifidi.edge.core.rmi.client.pluginstub.valueobjects;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
 * A value object that wraps a Reader Plugin XML
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderPluginWrapper {

	private Log logger = LogFactory.getLog(ReaderPluginWrapper.class);

	private Document readerPluginXML;

	/**
	 * Key- PropertyName Value - Set of groups
	 */
	private HashMap<String, Set<String>> properties;

	private String desciption = null;
	private String readerName = null;
	private String readerInfoClass = null;
	private Set<String> groups = null;
	private Set<String> commandNames = null;

	public ReaderPluginWrapper(String XML) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(XML.getBytes());
			readerPluginXML = parser.parse(is);
		} catch (ParserConfigurationException e) {
			logger.error("Cannot create Reader Plugin Wrapper", e);
		} catch (SAXException e) {
			logger.error("Cannot create Reader Plugin Wrapper", e);
		} catch (IOException e) {
			logger.error("Cannot create Reader Plugin Wrapper", e);
		}

	}

	/**
	 * 
	 * @return The description of the reader from the reader plugin xml
	 */
	public String getDescription() {
		if (desciption == null) {
			desciption = getValue("description");
			if (desciption == null) {
				logger.debug("No description found in ReaderPluginXML");
			}
		}
		return desciption;
	}

	/**
	 * 
	 * return The reader name from the reader plugin xml
	 */
	public String getReaderName() {
		if (readerName == null) {
			readerName = getValue("name");
			if (readerName == null) {
				logger.debug("No name found in ReaderPluginXML");
			}
		}
		return readerName;
	}

	/**
	 * 
	 * @return The fully qualified class name of the readerInfo class from the
	 *         reader plugin xml
	 */
	public String getReaderInfoClassName() {
		if (readerInfoClass == null) {
			readerInfoClass = getValue("info");
			if (readerInfoClass == null) {
				logger.debug("No readerinfoclassname found in ReaderPluginXML");
			}
		}
		return readerInfoClass;
	}

	/**
	 * This is a helper method that seraches one level deep to find a node with
	 * the given node name. It returns the text node (if it exists) that is a
	 * subnode of the found node
	 * 
	 * @param nodeName
	 *            the node name to search for in the ReaderPluginXML
	 * @return The text value of the node
	 */
	private String getValue(String nodeName) {
		NodeList nodes = readerPluginXML.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
				NodeList description = nodes.item(i).getChildNodes();
				for (int j = 0; j < description.getLength(); j++) {
					if (description.item(j).getNodeType() == Node.TEXT_NODE) {
						return ((Text) description.item(j)).getNodeValue();
					}
				}
			}

		}
		return null;
	}

	/**
	 * This method returns a list of property names which are defined in the
	 * reader plugin xml
	 * 
	 * @param group
	 *            The group which the readers are a part of
	 * @return A list of property names
	 */
	public Set<String> getPropertyNames(String group) {
		Set<String> propNames = new HashSet<String>();
		if (this.properties == null) {
			processProperties();
		}
		for (String propName : this.properties.keySet()) {
			if (this.properties.get(propName).contains(group)) {
				propNames.add(propName);
			}
		}
		return propNames;
	}

	/**
	 * This method returns the groups that a particular property belongs to.
	 * 
	 * @param propertyName
	 *            The property name to search for
	 * @return A set of groups that the property belongs to
	 */
	public Set<String> getGroups(String propertyName) {
		if (this.properties == null) {
			processProperties();
		}
		return this.properties.get(propertyName);
	}

	/**
	 * @return All available property groups
	 */
	public Set<String> getGroups() {
		if (this.properties == null) {
			processProperties();
		}

		if (groups == null) {
			groups = new HashSet<String>();
			for (String key : this.properties.keySet()) {
				groups.addAll(properties.get(key));
			}
		}
		return groups;
	}

	/**
	 * A helper method that processes the properties in the XML into more
	 * accessible data strucutres
	 */
	private void processProperties() {
		this.properties = new HashMap<String, Set<String>>();
		NodeList nodes = readerPluginXML.getDocumentElement().getChildNodes();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName().equalsIgnoreCase("property")) {
				Set<String> groups = new HashSet<String>();
				String propName = null;
				NodeList subElementsOfProperty = nodes.item(i).getChildNodes();
				for (int j = 0; j < subElementsOfProperty.getLength(); j++) {
					Node propertySubElement = subElementsOfProperty.item(j);
					if (propertySubElement.getNodeName().equalsIgnoreCase(
							"name")) {
						if (propertySubElement.getFirstChild() instanceof Text) {
							propName = ((Text) propertySubElement
									.getFirstChild()).getNodeValue();
						}
					} else if (propertySubElement.getNodeName().equals("group")) {
						if (propertySubElement.getFirstChild() instanceof Text) {
							groups.add(((Text) propertySubElement
									.getFirstChild()).getNodeValue());
						}
					}

				}
				this.properties.put(propName, groups);
			}

		}

	}

	/**
	 * 
	 * @return The commands defined in this ReaderPlugin XML
	 */
	public Set<String> getCommandNames() {
		if (commandNames == null) {
			commandNames = new HashSet<String>();
			NodeList nodes = readerPluginXML.getDocumentElement()
					.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i).getNodeName().equalsIgnoreCase("command")) {
					NodeList subElementsOfCommand = nodes.item(i)
							.getChildNodes();
					for (int j = 0; j < subElementsOfCommand.getLength(); j++) {
						Node commandSubElement = subElementsOfCommand.item(j);
						if (commandSubElement.getNodeName().equalsIgnoreCase(
								"name")) {
							if (commandSubElement.getFirstChild() instanceof Text) {
								String commandName = ((Text) commandSubElement
										.getFirstChild()).getNodeValue();
								this.commandNames.add(commandName);
							}
						}
					}
				}
			}
		}
		return this.commandNames;

	}

	/**
	 * 
	 * @return Returns this ReaderPlugin as an XML string
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
			DOMSource source = new DOMSource(readerPluginXML);
			trans.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
