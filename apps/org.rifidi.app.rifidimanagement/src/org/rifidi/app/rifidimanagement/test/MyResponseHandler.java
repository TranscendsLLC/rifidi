package org.rifidi.app.rifidimanagement.test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class MyResponseHandler
		extends DefaultHandler {

	//Variable to get current value 
	private StringBuffer buffer = new StringBuffer();
	
	//Variable to get message value from xml response
	private String messageValue;
	
	// this method is called every time the parser gets an open tag '<'  
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
	
		super.startElement(uri, localName, qName, attributes);
		
		// TODO Auto-generated method stub
		buffer.setLength(0);
		
	}
	
	// calls by the parser whenever '>' end tag is found in xml
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		super.endElement(uri, localName, qName);
		// TODO Auto-generated method stub
		if (qName.equals("message")) {
			
			messageValue = buffer.toString(); 
			
		} 
		
		
	}
	
	// prints data stored in between '<' and '>' tags  
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		
		super.characters(ch, start, length);
		// TODO Auto-generated method stub
		
		buffer.append(ch, start, length);
		
		
	}

	/**
	 * @return the messageValue
	 */
	public String getMessageValue() {
		return messageValue;
	}
	
	
	
	
	
}
