package org.rifidi.edge.persistence.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.persistence.service.PersistenceService;
import org.rifidi.edge.persistence.utilities.JAXBUtility2;

public class PersistanceServiceImpl implements PersistenceService, ReaderPluginListener, ReaderSessionListener {

	Document rootDoc;
	JDOMFactory factory = new DefaultJDOMFactory();
	SAXBuilder saxbuilder;
	
	public PersistanceServiceImpl(){
		System.out.println("created");
	}
	
	@Override
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addEvent(ReaderSession readerSession) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEvent(ReaderSession readerSession) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start(String fileName) {
		//check if file name exists
		File file = new File(fileName);
		saxbuilder = new SAXBuilder();
		if(file.exists()){
			try {
				rootDoc = saxbuilder.build(file);
				loadSessionsIfAvailable();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(fileName);
				writeXML(fos);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public void addReaderInfo(ReaderInfo readerInfo){
		//convert readerInfo to JDOM element using Jaxb
		Element e =JAXBUtility2.toJDOMElement(readerInfo, saxbuilder);
		writeXML(System.out, e);
		
		//lookup readerInfoList where new element should go
		
		//if readerInfoList does not exist, create it
		
		//insert node into it
	}
	
	public void reamoveReaderInfo(ReaderInfo readerInfo){
		//get IP and port of readerInfo
		
		//search through List of readerInfos in document to find it
		
		//remove it if it exists
	}
	
	public void loadSessionsIfAvailable(){
		//for each reader type in ReaderInfoList, check if plugin is available
		//if available, load reader Session
	}
	
	/**
	 * Called when a new ReaderPlugin becomes available
	 * @param readerInfoType
	 */
	public void loadReaderSessions(String readerInfoType){
		//find ReaderInfoList with type == readerInfoType
		
		//if type exists iterate through readerIfos
		
		//for each readerInfo, restore using Jaxb and load into readersessions
	}
	
	private void writeXML(OutputStream out){
		try {
			XMLOutputter serializer = new XMLOutputter();
			serializer.output(rootDoc, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeXML(OutputStream out, Element element){
		try {
			XMLOutputter serializer = new XMLOutputter();
			serializer.output(element, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
