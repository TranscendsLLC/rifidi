package org.rifidi.edge.client.messageconvertingservice;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import org.rifidi.edge.client.messageconvertingservice.service.MessageConvertingService;
import org.rifidi.edge.core.readerplugin.messages.Message;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;

public class MessageConvertingServiceImpl implements MessageConvertingService {
	private Set<Class<?>> klasses = new HashSet<Class<?>>();
	
	JAXBContext context;
	Unmarshaller unmarshaller;
	
	public MessageConvertingServiceImpl() throws JAXBException {
		klasses.add(TagMessage.class);
		klasses.add(EnhancedTagMessage.class);
		context = JAXBContext.newInstance(klasses.toArray(new Class[0]));
		unmarshaller = context.createUnmarshaller();
		
	}
	
	
	public void addMessageImpl(Class<? extends Message> message) throws JAXBException{
		
		klasses.add(message);
		JAXBContext c = JAXBContext.newInstance(klasses.toArray(new Class[0]));
		Unmarshaller u = context.createUnmarshaller();
		
		context = c;
		unmarshaller = u;
	}
	
	public void removeMessageImpl(Class<? extends Message> message) throws JAXBException{
		klasses.remove(message);
		JAXBContext c = JAXBContext.newInstance(klasses.toArray(new Class[0]));
		Unmarshaller u = context.createUnmarshaller();
		
		context = c;
		unmarshaller = u;
	}
	
	public Message MessageFromXML(String xml) throws JAXBException{
		Reader reader = new CharArrayReader(xml.toCharArray());
		return (Message) unmarshaller.unmarshal(reader);
	}
}
