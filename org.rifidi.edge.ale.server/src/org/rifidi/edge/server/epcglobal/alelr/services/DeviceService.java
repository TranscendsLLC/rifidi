package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
//import org.rifidi.edge.server.ale.infrastructure.TagReadEvent;
import org.rifidi.edge.server.epcglobal.alelr.Reader;

import rx.Observer;
import rx.Subscription;
import rx.Observable.OnSubscribe;

public interface DeviceService {

	
	//cuando el reader esta prendido pero no hay ninguna sesion escuchando, es como si estubiera deshabilitado
	//boolean enable(String deviceName, boolean enable);
    
    //Device GetDevice(string deviceName);
    //Iterable<Device> getDevices();
    boolean init(String deviceName) throws ValidationExceptionResponse, IOException, JAXBException, CommandSubmissionException;
    
    //load-> puede ser save current state de rifidi
    void loadDevices();
    
    //Added by Alejandro 2016-04-27
    void removeDevice(String deviceName);
    
    //iniciar a leer
    boolean start(String deviceName);
    boolean stop(String deviceName);
    
    //setproperties al sensor fisico
    //boolean applySettings(String deviceName, String settings);
    
    //obtener los eventos de los lectores
    //Iterable<TagReadEvent> getEvents(int lastNSeconds);
    
    //obtener los eventos del lector
    //Iterable<TagReadEvent> getEvents(String deviceName, int lastNSeconds);
     
    Subscription subscribeObserver(LogicalReader logicalReaderName, Observer<TagReadEvent> observer);


	Reader createDeviceFromPlugin(String pluginName, String deviceName, LogicalReader logicalReader)
			throws ValidationExceptionResponse, IOException, JAXBException;

	
}
