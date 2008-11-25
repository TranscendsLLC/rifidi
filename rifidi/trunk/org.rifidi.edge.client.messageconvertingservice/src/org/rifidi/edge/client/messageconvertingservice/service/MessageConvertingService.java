package org.rifidi.edge.client.messageconvertingservice.service;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.core.readerplugin.messages.Message;

public interface MessageConvertingService {
	public void addMessageImpl(Class<? extends Message> message) throws JAXBException;
	public void removeMessageImpl(Class<? extends Message> message) throws JAXBException;
	public Message MessageFromXML(String xml) throws JAXBException;
}
