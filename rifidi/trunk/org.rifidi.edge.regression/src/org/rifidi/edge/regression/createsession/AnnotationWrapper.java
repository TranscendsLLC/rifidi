package org.rifidi.edge.regression.createsession;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.rifidi.dynamicswtforms.xml.constants.FormData;
import org.rifidi.dynamicswtforms.xml.constants.FormElementData;

public class AnnotationWrapper {

	private Document annotationDocument;

	public AnnotationWrapper(String readerInfoAnnotation) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder();
		StringReader stringReader = new StringReader(readerInfoAnnotation);
		annotationDocument = builder.build(stringReader);
	}

	public String buildDefaultReaderInfo() throws IOException {
		Element root = new Element(getRIName());
		for (String eName : getformElementNames()) {
			Element node = new Element(eName);
			String defaultValue = getDefaultValue(eName);
			if(defaultValue==null){
				throw new NullPointerException();
			}else{
				node.setText(defaultValue);
			}
			root.addContent(node);
		}
		Document returnDoc = new Document(root);
		XMLOutputter outPutter = new XMLOutputter();
		StringWriter sw = new StringWriter();
		outPutter.output(returnDoc, sw);
		return sw.toString();
	}

	public String getRIName() {
		return annotationDocument.getRootElement().getAttribute(
				FormData.NAME.name()).getValue();
	}

	public List<String> getformElementNames() {
		ArrayList<String> names = new ArrayList<String>();
		List<Element> elements = annotationDocument.getRootElement().getChildren();
		for(Element e : elements){
			String name = e.getChildText(FormElementData.ELEMENT_NAME.name());
			names.add(name);
		}
		return names;
	}

	public String getDefaultValue(String formElementName) {
		ArrayList<String> names = new ArrayList<String>();
		List<Element> elements = annotationDocument.getRootElement().getChildren();
		for(Element e : elements){
			String name = e.getChildText(FormElementData.ELEMENT_NAME.name());
			if(name.equalsIgnoreCase(formElementName)){
				return e.getChildText(FormElementData.DEFAULT_VALUE.name());
			}
		}
		return null;
	}

}
