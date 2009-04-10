package org.rifidi.edge.readerplugin.thingmagic;

import java.util.Map;

import javax.jms.Destination;

import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.ReaderSession;
import org.springframework.jms.core.JmsTemplate;

public class ThingMagic4_5Reader extends AbstractReader<ThingMagic4_5ReaderSession>{

	@Override
	public void applyPropertyChanges() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReaderSession createReaderSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroyReaderSession(ReaderSession session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, ReaderSession> getReaderSessions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void setDestination(Destination destination) {
		// TODO Auto-generated method stub
		
	}

	public void setTemplate(JmsTemplate template) {
		// TODO Auto-generated method stub
		
	}

	public void setNotifiyServiceWrapper(
			NotifierServiceWrapper notifierServiceWrapper) {
		// TODO Auto-generated method stub
		
	}

}
