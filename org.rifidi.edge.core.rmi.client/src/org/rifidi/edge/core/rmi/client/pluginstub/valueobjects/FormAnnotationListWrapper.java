/*
 *  FormAnnotationListWrapper.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.client.pluginstub.valueobjects;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.rifidi.dynamicswtforms.xml.constants.FormData;

/**
 * This object holds a list of dynamic SWT Form annotations. It simply makes it
 * a little easier to parse and access lists of DyncamicSWTForm annotations
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FormAnnotationListWrapper {

	private Log logger = LogFactory.getLog(FormAnnotationListWrapper.class
			.getName());

	/**
	 * keys- form names values - FormAnnotations
	 */
	private HashMap<String, Element> forms;

	/**
	 * Constructor takes in an XML string, which contains a list of form
	 * annotations. For example: <ROOT> <DYNAMIC_SWT_FORM NAME="form1"\>
	 * <DYNAMIC_SWT_FORM NAME="form2"\> </ROOT>
	 */
	@SuppressWarnings("unchecked")
	public FormAnnotationListWrapper(String XML) {
		forms = new HashMap<String, Element>();

		SAXBuilder builder = new SAXBuilder();
		Reader reader = new StringReader(XML);

		try {
			Document doc = builder.build(reader);
			List<Element> elements = doc.getRootElement().getChildren();
			for (Element element : elements) {
				String commandName = element.getAttributeValue(FormData.NAME
						.name());
				forms.put(commandName, element);
			}

		} catch (JDOMException e) {
			logger.error("Cannot parse XML: ", e);
		} catch (IOException e) {
			logger.error("Cannot parse XML: ", e);
		}
	}

	/**
	 * Get the Dynamic SWT Form with the given form name
	 * 
	 * @param formName
	 * @return a JDOM Element that has a root <DYNAMIC_SWT_FORM
	 *         NAME="formName"\>
	 */
	public Element getFormAnnotation(String formName) {
		return forms.get(formName);
	}

	/**
	 * 
	 * @return A set of form names in this wrapper
	 */
	public Set<String> getFormNames() {
		return forms.keySet();
	}

}
