package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.ale.infrastructure.Notifier;
import org.rifidi.edge.server.epcglobal.alelr.Reader;

import rx.Observer;
import rx.Subscription;

public interface NotifierService {
	
		Notifier createNotifierFromPlugin(String pluginName, String notifierName, String notificationURI) throws ValidationExceptionResponse;
		
	    Iterable<Notifier> getNotifiers();
	    boolean init(String name) throws ValidationExceptionResponse, IOException, JAXBException, CommandSubmissionException;
	    
	    //load-> puede ser save current state de rifidi
	    void loadNotifiers();
	    	     
	    Subscription subscribeObserver(String eventCycleName, Observer<ECReports> observer);
}
