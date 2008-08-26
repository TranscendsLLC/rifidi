package org.rifidi.edge.core.readerplugin.annotations.service.impl;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiWidgetAnnotationException;
import org.rifidi.edge.core.readerplugin.annotations.Widget;
import org.rifidi.edge.core.readerplugin.annotations.Widgets;
import org.rifidi.edge.core.readerplugin.annotations.service.WidgetAnnotationProcessorService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WidgetAnnotationProcessorServiceImpl implements
		WidgetAnnotationProcessorService {
	
	private Log logger = LogFactory.getLog(WidgetAnnotationProcessorServiceImpl.class);

	@Override
	public Document processAnnotation(Class<?> clazz)
			throws RifidiWidgetAnnotationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.newDocument();

			Widgets widgetsAnnotation = clazz.getAnnotation(Widgets.class);

			Element root = doc.createElement("composite");
			root.setAttribute("name", clazz.getSimpleName());
			doc.appendChild(root);
			for (Widget w : widgetsAnnotation.widgets()) {
				root.appendChild(processWidget(w, doc));
			}

			return doc;
		} catch (ParserConfigurationException e) {
			throw new RifidiWidgetAnnotationException(e);
		}

	}

	private Element processWidget(Widget widget, Document doc) {

		Element element = doc.createElement(widget.type().toString());

		Element name = doc.createElement(Widget.ELEMENT_NAME);
		name.appendChild(doc.createTextNode(widget.elementName()));
		element.appendChild(name);

		Element displayname = doc.createElement(Widget.DISPLAY_NAME);
		displayname.appendChild(doc.createTextNode(widget.displayName()));
		element.appendChild(displayname);

		Element defaultValue = doc.createElement(Widget.DEFAULT_VALUE);
		defaultValue.appendChild(doc.createTextNode(widget.defaultValue()));
		element.appendChild(defaultValue);

		Element editable = doc.createElement(Widget.EDITABLE);
		editable.appendChild(doc.createTextNode(Boolean.toString(widget
				.editable())));
		element.appendChild(editable);



		switch (widget.type()) {
		case BOOLEAN:
			processBooleanWidget(widget, element);
			break;
		case STRING:
			processStringWidget(widget, element);
			break;
		case INTEGER:
			processIntegerWidget(widget, element);
			break;
		case LONG:
			processLongWidget(widget, element);
			break;
		case FLOAT:
			processFloatWidget(widget, element);
			break;
		case DOUBLE:
			processDoubleWidget(widget, element);
			break;
		case ENUM:
			processEnumWidget(widget, element);
			break;
		}

		return element;
	}

	private void processEnumWidget(Widget widget, Element element) {
		String enumClass= widget.enumClass();
		try {
			ArrayList<String> enumValues = new ArrayList<String>();
			Enum<?>[] enums = (Enum[])Class.forName(enumClass).getEnumConstants();	
			for(Enum<?> e : enums){
				enumValues.add(e.toString());
			}
			processPossibleValues(element, enumValues);
		} catch (ClassNotFoundException e) {
			logger.debug(e.getMessage());
		}
	}

	private void processDoubleWidget(Widget widget, Element element) {
		Element min = element.getOwnerDocument().createElement(Widget.MIN);
		min.appendChild(element.getOwnerDocument().createTextNode(
				Double.toString(widget.min())));
		element.appendChild(min);

		Element max = element.getOwnerDocument().createElement(Widget.MAX);
		max.appendChild(element.getOwnerDocument().createTextNode(
				Double.toString(widget.max())));
		element.appendChild(max);

		Element decimal = element.getOwnerDocument().createElement(
				Widget.DECIMAL_PLACES);
		decimal.appendChild(element.getOwnerDocument().createTextNode(
				Integer.toString(widget.decimalPlaces())));
		element.appendChild(decimal);

	}

	private void processFloatWidget(Widget widget, Element element) {
		Element min = element.getOwnerDocument().createElement(Widget.MIN);
		Float minFloat = new Double(widget.min()).floatValue();
		min.appendChild(element.getOwnerDocument().createTextNode(
				Float.toString(minFloat)));
		element.appendChild(min);

		Element max = element.getOwnerDocument().createElement(Widget.MAX);
		Float maxLong = new Double(widget.max()).floatValue();
		max.appendChild(element.getOwnerDocument().createTextNode(
				Float.toString(maxLong)));
		element.appendChild(max);

		Element decimal = element.getOwnerDocument().createElement(
				Widget.DECIMAL_PLACES);
		decimal.appendChild(element.getOwnerDocument().createTextNode(
				Integer.toString(widget.decimalPlaces())));
		element.appendChild(decimal);

	}

	private void processLongWidget(Widget widget, Element element) {
		Element min = element.getOwnerDocument().createElement(Widget.MIN);
		Long minLong = new Double(widget.min()).longValue();
		min.appendChild(element.getOwnerDocument().createTextNode(
				Long.toString(minLong)));
		element.appendChild(min);

		Element max = element.getOwnerDocument().createElement(Widget.MAX);
		Long maxLong = new Double(widget.max()).longValue();
		max.appendChild(element.getOwnerDocument().createTextNode(
				Long.toString(maxLong)));
		element.appendChild(max);

	}

	private void processIntegerWidget(Widget widget, Element element) {
		Element min = element.getOwnerDocument().createElement(Widget.MIN);
		Integer minInt = new Double(widget.min()).intValue();
		min.appendChild(element.getOwnerDocument().createTextNode(
				Integer.toString(minInt)));
		element.appendChild(min);

		Element max = element.getOwnerDocument().createElement(Widget.MAX);
		Integer maxInt = new Double(widget.max()).intValue();
		max.appendChild(element.getOwnerDocument().createTextNode(
				Integer.toString(maxInt)));
		element.appendChild(max);

	}

	private void processStringWidget(Widget widget, Element element) {
		Element regex = element.getOwnerDocument().createElement(Widget.REGEX);
		regex.appendChild(element.getOwnerDocument().createTextNode(
				widget.regex()));
		element.appendChild(regex);
	}

	private void processBooleanWidget(Widget widget, Element element) {

	}
	
	private void processPossibleValues(Element element, ArrayList<String> possibleValues){
		if (possibleValues.size() > 0) {
			Element values = element.getOwnerDocument().createElement(
					Widget.POSSIBLE_VALUES);
			for (String value : possibleValues) {
				Element valueElement = element.getOwnerDocument()
						.createElement(Widget.POSSIBLE_VALUE);
				valueElement.appendChild(element.getOwnerDocument()
						.createTextNode(value));
				values.appendChild(valueElement);
			}
			element.appendChild(values);
		}
	}

}
