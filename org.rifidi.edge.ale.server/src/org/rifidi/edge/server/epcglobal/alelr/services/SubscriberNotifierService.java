package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.server.ale.infrastructure.Notifier;
import org.springframework.stereotype.Service;

import rx.Observer;
import rx.Subscription;

@Service
public class SubscriberNotifierService implements NotifierService {

	@Override
	public Notifier createNotifierFromPlugin(String pluginName, String notifierName, String notificationURI)
			throws ValidationExceptionResponse {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Notifier> getNotifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean init(String name)
			throws ValidationExceptionResponse, IOException, JAXBException, CommandSubmissionException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void loadNotifiers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Subscription subscribeObserver(String eventCycleName, Observer<ECReports> observer) {
		// TODO Auto-generated method stub
		return null;
	}

}
