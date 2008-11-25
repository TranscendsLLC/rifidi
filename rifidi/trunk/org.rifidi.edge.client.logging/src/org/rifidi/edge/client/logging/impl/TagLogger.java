package org.rifidi.edge.client.logging.impl;


import java.io.CharArrayReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.edgeserver.RemoteReader;
import org.rifidi.edge.client.connections.edgeserver.listeners.ReaderMessageListener;
import org.rifidi.edge.client.connections.edgeserver.listeners.EdgeServerConnectionListener;
import org.rifidi.edge.client.connections.edgeserver.registry.listeners.ServerRegistryListener;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;
import org.rifidi.edge.core.readerplugin.messages.impl.TagMessage;



public class TagLogger implements ReaderMessageListener {
	
	private static Log logger = LogFactory.getLog(TagLogger.class);
	
	JAXBContext context;
	
	Unmarshaller unmarshaller;
	Marshaller marshaller;
	
	
	
	MessageLog log = new MessageLog();

	File logFile;
	
	public TagLogger(String file) throws JAXBException, FileNotFoundException {
		logger.debug("Constructing TagLogger");
		context = JAXBContext.newInstance(TagMessage.class,
				EnhancedTagMessage.class, MessageLog.class);
		unmarshaller = context.createUnmarshaller();
		marshaller = context.createMarshaller();
		logFile =  new File(file);
		log.log = new ArrayList<Object>();
	}

	@Override
	public void onMessage(Message m, RemoteReader r) {
		TextMessage message = (TextMessage) m;
		// TODO Auto-generated method stub
		Reader reader;
		org.rifidi.edge.core.readerplugin.messages.Message readerMessage;
		try {
			reader = new CharArrayReader(message.getText().toCharArray());
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();		
			return;
		}
		
		try {

			readerMessage = (org.rifidi.edge.core.readerplugin.messages.Message) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		logger.debug(readerMessage.toXML());
		log.log.add(readerMessage);
		
		try {
			marshaller.marshal(log, logFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
	}
	
}
