/**
 * 
 */
package org.rifidi.edge.betterpersist.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.rifidi.edge.betterpersist.utilities.JAXBUtility;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class PersistedReaderInfo {

	private Document persistMap = null;
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
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass().getName());
			if (readerInfoTypeList == null) {
				readerInfoTypeList = persistMap.createElement(XMLTags.ELEMENT_TAG);
				readerInfoTypeList.setAttribute(XMLTags.TYPE_TAG, readerInfo.getClass().getName());
				root.appendChild(readerInfoTypeList);
			}
			Element readerInfoNode = JAXBUtility.getInstance()
					.createReaderInfoNode(readerInfo, persistMap);
			readerInfoTypeList.appendChild(readerInfoNode);
			printToFile();
	}
	
	public void removeReader(ReaderInfo readerInfo) throws RifidiReaderInfoNotFoundException{
		Element readerInfoTypeList = findReaderTypeList(readerInfo.getClass().getName());
		if (readerInfoTypeList == null) {
			throw new RifidiReaderInfoNotFoundException();
		}
		
		Element readerInfoNode = find(readerInfo, root);
		if(readerInfoNode==null){
			throw new RifidiReaderInfoNotFoundException();
		}
		readerInfoTypeList.removeChild(readerInfoNode);
		printToFile();
		
	}
	
	public void parsePersistanceXMLFile(File xml){
		//TODO parse XML file into DOM structure
	}

	private Element find(ReaderInfo readerInfo, Element readerInfoTypeList){
		//TODO: Matt use XPath to find
		return null;
	}
	
	private Element findReaderTypeList(String ReaderInfoType)  {
		// TODO: Matt use XPATH to return the List of Reader types
		return null;
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
			System.out.println("Error while trying to "
					+ "instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return dom;
	}

	void printToFile() {
		try {
			// print
			OutputFormat format = new OutputFormat(persistMap);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(
					new File("cuddles.xml")), format);

			serializer.serialize(persistMap);

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

		public static final String ELEMENT_TAG = "ReaderInfoList";

		public static final String TYPE_TAG = "ReaderType";

		// public static final String READER_TAG = "ReaderInstance";
	}
}
