package org.rifidi.edge.server.epcglobal.alelr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.notification.TagReadEvent;

import rx.Observer;
import rx.Subscription;
import rx.internal.util.SubscriptionList;

/**
 * @author Daniel Gomez
 */

public final class BaseReader extends Reader{	

	private List<Observer<TagReadEvent>> observers = new ArrayList<Observer<TagReadEvent>>();
			
//	private Collection<LRProperty> properties = new ArrayList<LRProperty>();
		
	private boolean isStarted = false;
	
	private SubscriptionList disposables = new SubscriptionList();
	
	private Subscription subscription;
	
	public BaseReader(){
		
	}

	@Override
	public Subscription getSubscription() {
		// TODO Auto-generated method stub
		return this.subscription;
	}

	@Override
	public void setSubscription(Subscription subscription) {
		// TODO Auto-generated method stub
		this.subscription = subscription;
	}

	
	
	@Override
	public Subscription subscribeObserver(Observer<TagReadEvent> observer) {
		
		if (!this.observers.contains(observer))
        {
            observers.add(observer);
        }

        return new Unsubscriber(this.observers, observer);
	}

	@Override
	public void unsubscribeObserver(){
            if (getSubscription() != null)
                getSubscription().unsubscribe();        
	}
	

	@Override
	public void fireEvent(TagReadEvent e){	
		if(isStarted)
		    for (Observer<TagReadEvent> observer : this.observers)
		    {
		    	observer.onNext(e);
		    }
	
	}

	

	@Override
	public LRSpec getLRSpec() {
		// TODO Auto-generated method stub
		return lrSpec;
	}

	@Override
	public void setLRSpec(LRSpec lrSpec) {
		// TODO Auto-generated method stub
		this.lrSpec = lrSpec;
	}

	@Override
	public Collection<LRProperty> getProperties() {
		// TODO Auto-generated method stub
		return properties;
	}

	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return isStarted;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return readerName;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		readerName = name;
	}

	@Override
	public void update(LRSpec lrSpec) {
		// TODO Auto-generated method stub
		 this.lrSpec = lrSpec;

		 //TODO uncomment this
	        if (this.lrSpec.getProperties().getProperty() == null)
	        {
	            throw new UnsupportedOperationException("no properties specified");
	        }
	        
	        // store the properties
	        properties.clear();
	        properties.addAll(this.lrSpec.getProperties().getProperty());  
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		isStarted = true;		
	}
	
	

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		isStarted = false;
		//TODO review the stop process
		disposables.unsubscribe();
		disposables.clear();
	}

	@Override
	public boolean hasSubscription() {
		if(getSubscription() != null)
			return true;
		return false;
	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}	
	
	
	
}
