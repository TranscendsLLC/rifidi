/**
 * 
 */
package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.rifidi.app.ale.AleApp;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.service.tagmonitor.LimitStableSetService;
import org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;
import org.rifidi.edge.server.ale.infrastructure.Device;
import org.rifidi.edge.server.ale.infrastructure.LLRPDevice;
import org.rifidi.edge.server.ale.rest.MqttPublisher;
import org.rifidi.edge.server.epcglobal.ale.Tag;
import org.rifidi.edge.server.epcglobal.alelr.BaseReader;
import org.rifidi.edge.server.epcglobal.alelr.Reader;
import org.rifidi.edge.utils.RifidiHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

/**
 * @author Daniel Gomez
 * @author manuel
 *
 */
 

public class PhysicalDeviceService implements DeviceService {

	private ConcurrentMap<String, Pair<Subscription,Pair<BaseReader, Device>>> devices = new ConcurrentHashMap<String, Pair<Subscription, Pair<BaseReader,Device>>>();
	
	
	private SensorManagerService sensorManagerService;

	private CommandManagerService commandManagerService;

	private SessionDTO session;
	
	public ReaderDAO readerDAO;

	private RawTagMonitoringService rawTagMonitoringService;
	
	private ReadZoneMonitoringService readZoneMonitoringService;
	
	private StableSetService stableSetService;
	
	private LimitStableSetService limitStableSetService;
	
	// Reference to AleApp to get the readzones
//	private AleApp aleApp;
	
	private RifidiHelper rifidiHelper;
	
	
	public PhysicalDeviceService() {

	}

	public Iterable<String> GetDevices() {
		Set<String> keys = this.devices.keySet();
		List<String> devices = new ArrayList<String>();
		for (String key : keys) {
			
			devices.add(this.devices.get(key).getValue().getRight().getDeviceId());
		}

		return devices;
	}

	@Override
	public Subscription subscribeObserver(LogicalReader logicalReader, Observer<TagReadEvent> observer) {

		if (devices.containsKey(logicalReader.getName())) {

			return devices.get(logicalReader.getName()).getValue().getRight().subscribeObserver(observer);
		}

		return null;
	}

	public void unsubscribeObserver(LogicalReader logicalReader) {
		if (devices.containsKey(logicalReader.getName())) {
			if (!devices.get(logicalReader.getName()).getKey().isUnsubscribed())
				devices.get(logicalReader.getName()).getKey().unsubscribe();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.server.epcglobal.alelr.services.DeviceService#Init(java.
	 * lang.String)
	 */
	public boolean init(String deviceName)
			throws ValidationExceptionResponse, IOException, JAXBException, CommandSubmissionException {
		if (devices.containsKey(deviceName)) {
			devices.get(deviceName).getValue().getRight().init();
			return true;

		}
		// throw a new exception the device doesn't exist.
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.server.epcglobal.alelr.services.DeviceService#LoadDevices
	 * ()
	 */
	@Override
	public void loadDevices() {
		// TODO Auto-generated method stub

	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.server.epcglobal.alelr.services.DeviceService#Start(java.
	 * lang.String)
	 */
	@Override
	public boolean start(String deviceName) {
		// TODO Auto-generated method stub


		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.server.epcglobal.alelr.services.DeviceService#Stop(java.
	 * lang.String)
	 */
	@Override
	public boolean stop(String deviceName) {
		// TODO Auto-generated method stub

		

		return false;
	}

	

	/**
	 * Processes a chain of semicolon separated properties and checks whether it
	 * is a well formed pair
	 * 
	 * @param propertiesChain
	 *            separated values of properties, for example:
	 *            (prop1=val2;prop2=val2;prop3=val3)
	 * @return AttributeList containing the attributes
	 * @throws Exception
	 *             if any property has no recognizable value
	 */
	private AttributeList getProcessedAttributes(String propertiesChain) throws Exception {

		AttributeList attributes = new AttributeList();

		// Check if propertiesChain has properties to process...
		if (propertiesChain != null && !propertiesChain.isEmpty()) {

			String[] splitProp = propertiesChain.split(";");

			for (String pair : splitProp) {

				String[] prop = pair.split("=");

				// check if property has a property and a value
				if (prop.length == 2) {

					// It has property and value
					attributes.add(new Attribute(prop[0], prop[1]));

				} else {

					// Property with no recognizable value, for example
					// Port=123=456, or Port,
					throw new Exception("Property with no recognizable value: " + prop[0]);

				}
			}

		}

		return attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.server.epcglobal.alelr.services.DeviceService#
	 * ApplySettings(java.lang.String, java.lang.String)
	 */
//	@Override
//	public boolean applySettings(String deviceName, String settings) {
//		// TODO Auto-generated method stub
//
//		try {
//
//			// stop reader session only if processing or connected or loggingin
//			boolean stopAndStart = false;
//
//			SessionDTO sessionDTO = getSession(deviceName);
//			if (sessionDTO != null && sessionDTO.getStatus() != null
//					&& (sessionDTO.getStatus().equals(SessionStatus.PROCESSING)
//							|| sessionDTO.getStatus().equals(SessionStatus.CONNECTING)
//							|| sessionDTO.getStatus().equals(SessionStatus.LOGGINGIN))) {
//				stopAndStart = true;
//			}
//
//			if (stopAndStart) {
//				sensorManagerService.stopSession(deviceName, sessionDTO.getID());
//			}
//
//			// setproperties
//			AttributeList attributes = getProcessedAttributes(settings);
//			sensorManagerService.setReaderProperties(deviceName, attributes);
//
//			// start reader session
//			if (stopAndStart) {
//				sensorManagerService.startSession(deviceName, sessionDTO.getID());
//			}
//
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return false;
//	}

	@Override
	public Reader createDeviceFromPlugin(String pluginName, String deviceName, LogicalReader logicalReader) throws ValidationExceptionResponse, IOException, JAXBException {
		// TODO Auto-generated method stub

		if (pluginName == "LLRP") {			
			Device device = new LLRPDevice();
			device.setDeviceId(deviceName);
			device.setLogicalReader(logicalReader);
			device.setCommandManagerService(commandManagerService);
			device.setRawTagMonitoringService(rawTagMonitoringService);
			device.setReaderDAO(readerDAO);
			device.setSensorManagerService(sensorManagerService);
//			device.setAleApp(aleApp);
			device.setRifidiHelper(rifidiHelper);
			device.init();
			device.start();			
			BaseReader baseReader = new BaseReader();			
			Subscription subscription = device.subscribeObserver(baseReader);
			devices.putIfAbsent(deviceName, new MutablePair<Subscription,Pair<BaseReader,Device>>(subscription,new MutablePair<BaseReader,Device>(baseReader, device)));
			return baseReader;
		}

		return null;
	}
	
	
    //Added by Alejandro 2016-04-27
	@Override
	public void removeDevice(String deviceName){
		System.out.println("PhysicalDeviceService.removeDevice: " + deviceName);
		
		Pair<Subscription, Pair<BaseReader, Device>> device = devices.remove(deviceName);
		
		device.getKey().unsubscribe();
		device.getRight().getRight().stop();
		device.getRight().getRight().setRifidiHelper(null);
		device.getRight().getRight().setCommandManagerService(null);
		device.getRight().getRight().setLogicalReader(null);
		device.getRight().getRight().setRawTagMonitoringService(null);
		device.getRight().getRight().setReaderDAO(null);
		device.getRight().getRight().setSensorManagerService(null);
		device.setValue(new ImmutablePair<BaseReader,Device>(null, null));
		System.out.println("PhysicalDeviceService.removeDevice, Device removed: " + device);
		System.out.println("PhysicalDeviceService.removeDevice.devices.size(): " + devices.size());
	}

	public boolean AttachLogicalReaderToDevice(String deviceName, LogicalReader logicalReader) {
		Pair<Subscription,Pair<BaseReader, Device>> subscriptionAnddevice = devices.get(deviceName);
		if (subscriptionAnddevice != null) {
			subscriptionAnddevice.getRight().getRight().setLogicalReader(logicalReader);
			return true;
		}

		return false;
	}

	
	

	/**
	 * Called by spring. This method injects the SensorManagerService into the
	 * application.
	 * 
	 * @param rzms
	 */
	public void setSensorManagerService(SensorManagerService sensorManagerService) {
		this.sensorManagerService = sensorManagerService;
	}

	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	public void setCommandManagerService(CommandManagerService commandManagerService) {
		this.commandManagerService = commandManagerService;
	}
	
	public void setRawTagMonitoringService(RawTagMonitoringService rawTagMonitoringService) {
		this.rawTagMonitoringService = rawTagMonitoringService;
	}
	
	public void setReadZoneMonitoringService(ReadZoneMonitoringService readZoneMonitoringService) {
		this.readZoneMonitoringService = readZoneMonitoringService;
	}
	
	public void setStableSetService(StableSetService stableSetService) {
		this.stableSetService = stableSetService;
	}

	public void setLimitStableSetService(LimitStableSetService limitStableSetService) {
		this.limitStableSetService = limitStableSetService;
	}

	public RifidiHelper getRifidiHelper() {
		return rifidiHelper;
	}

	public void setRifidiHelper(RifidiHelper rifidiHelper) {
		this.rifidiHelper = rifidiHelper;
	}
	
	

//	public AleApp getAleApp() {
//		return aleApp;
//	}
//
//	public void setAleApp(AleApp aleApp) {
//		this.aleApp = aleApp;
//	}
//	

}
