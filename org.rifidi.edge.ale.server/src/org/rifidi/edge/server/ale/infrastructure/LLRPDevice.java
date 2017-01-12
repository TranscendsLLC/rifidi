package org.rifidi.edge.server.ale.infrastructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBException;

import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;

public class LLRPDevice extends Device {

	public LLRPDevice() {

	}

	public LLRPDevice(String deviceId, LogicalReader logicalReader) {
		super(deviceId, logicalReader);
	}

	@Override
	public void init() throws ValidationExceptionResponse, IOException, JAXBException {

		//Check if device id is rifidi reader id or rifidi readzone name
		//Initially assume the deviceId is a physical reader id
		String rifidiReaderId = getDeviceId();
		
		rifidiHelper.validateRifidiReader(rifidiReaderId);
		
		ReadZone readZone = null;
		ReaderDTO readerDTO = rifidiHelper.getReader(rifidiReaderId);
		if (readerDTO == null){
//			//this is not a rifidi reader id
//			//check if it is a readzone name
			readZone = rifidiHelper.getReadzone(rifidiReaderId);
			if (readZone != null){
//				//it is a readzone name, then update reader id
				rifidiReaderId = readZone.getReaderID();
			} 
//			
		}
				
		// If settings are null create empty settings by default
		if (this.getSettings() == null) {
			this.setSettings(new HashMap<String, String>());
		}

		AttributeList attributes = fromMapToAttributeList(this.getSettings());
		rifidiHelper.setRifidiReaderProperties(rifidiReaderId, attributes);
		
		if ( readZone == null ){
			readZone = new ReadZone(rifidiReaderId);
		}
		
		this.getRawTagMonitoringService().subscribe(this, readZone);
		setIsInit(true);
		setIsEnabled(true);

	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		if (!getIsRunning()) {
			
			setIsRunning(true);
		}
	}

	@Override
	public void stop() {
			
		this.getRawTagMonitoringService().unsubscribe(this);
		setIsRunning(false);
		
	
	}

	/**
	 * Converts from Map<String,String> to AttributeList
	 * 
	 * @param settings
	 *            Map<String,String>
	 * @return AttributeList containing the attributes
	 */
	private AttributeList fromMapToAttributeList(Map<String, String> settings) {

		AttributeList attributes = new AttributeList();

		for (String key : settings.keySet()) {
			attributes.add(new Attribute(key, settings.get(key)));
		}

		return attributes;
	}

}
