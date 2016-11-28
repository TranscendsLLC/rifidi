package org.rifidi.edge.server.epcglobal.alelr;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rifidi.edge.epcglobal.alelr.ImplementationException;
import org.rifidi.edge.epcglobal.alelr.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.epcglobal.alelr.services.ReaderService;

import rx.Observable;
import rx.Observer;
import rx.Subscription;


/**
 * @author Daniel Gomez
 */

public abstract class Reader extends Observable<TagReadEvent> implements Observer<TagReadEvent> {	
	
	/** logger. */
	private static final Logger log = Logger.getLogger(Reader.class);
	
	/**
	 * all logical readers must define this property in the properties for the automatic reader creation.<br/>
	 * the property contains the FQN of the readers implementing class. 
	 */
	public static final String PROPERTY_READER_TYPE = "ReaderType";

	private static rx.Observable.OnSubscribe<TagReadEvent> onSubscribe;

	
	/**
	 * constructor for the logical reader.
	 */
	public Reader() {
		super(onSubscribe);
		// TODO Auto-generated constructor stub
	}

	/** name of the reader. */
	protected String readerName;
	
	/** property value pair for the reader configuration. */
	protected Map<String, String> readerProperties = new HashMap<String, String>();
	
	/** LRSpec for the reader. */
	protected LRSpec lrSpec;
	
	/** LRpoperties for the reader. */
	protected List<LRProperty> properties = new LinkedList<LRProperty>();
	
	/** indicates whether the reader is started or not. */
	protected boolean started = false;
	
	/** indicates whether the reader is connected or not. */
	protected boolean connected = false;
	
	/**
	 * handle to the logical reader manager that created this reader.
	 */	
	protected ReaderService readerService;
	
	/**
	 * initializes a Logical Reader. this method must be called befor the Reader can
	 * be used.
 	 * @param name the name for the reader encapsulated by this reader.
	 * @param spec the specification that describes the current reader.
	 * @throws ImplementationException whenever an internal error occurs.
	 */
	public void initialize(String name, LRSpec spec) throws ImplementationExceptionResponse, ValidationExceptionResponse {
		if (name == null) {
			log.log(Level.DEBUG,"reader name is null - aborting.");
			throw new ImplementationExceptionResponse("no reader name specified");
		}
		if (spec == null) {
			log.log(Level.DEBUG,"spec is null - aborting.");
			throw new ImplementationExceptionResponse("no spec specified");
		}

		this.readerName = name;
		this.lrSpec = spec;
		
		if (spec.getProperties() == null) {
			log.log(Level.DEBUG,"no properties specified - aborting.");
			throw new ImplementationExceptionResponse("no properties specified");
		}
		 // store the properties
        properties.addAll(this.lrSpec.getProperties().getProperty());      
	}
		
	
	public abstract Subscription getSubscription();
	public abstract void setSubscription(Subscription subscription);	
	public abstract Subscription subscribeObserver(Observer<TagReadEvent> observer);	
	public abstract void unsubscribeObserver();	
	public abstract void fireEvent(TagReadEvent tagReadEvent);

	
	/**
	 * This method sets the LRSpec.
	 * 
	 * @param spec The spec. 
	 */
	public void setLRSpec(LRSpec spec) {
		lrSpec = spec;
	}
	
	/**
	 * This method returns the spec.
	 * 
	 * @return This is the spec of the logical reader.
	 */
	public LRSpec getLRSpec() {
		return lrSpec;	
	}
	
	/**
	 * This method adds the LRProperty property to the logical reader named name.
	 * @param name name of the property
	 * @param property The property. 
	 */
	public void addProperty(String name, LRProperty property) {
		if(readerName == name)
			properties.add(property);
	}
	
	/**
	 * This method returns the properties.
	 * @return returns a list of LRProperty  
	 */
	public Collection<LRProperty> getProperties() {
		return properties;
	}
	
	/**
	 * flags the reader as started.
	 */
	protected void setStarted() {
		started = true;
	}
	
	/**
	 * flags the reader as stopped.
	 */
	protected void setStopped() {
		started = false;
	}
	
	/**
	 * tells whether the reader is started or not.
	 * @return boolean true or false
	 */
	public boolean isStarted() {
		return started;
	}
	
	/**
	 * This method returns the name of the logical reader.
	 * 
	 * @return name of this logical reader
	 */
	public String getName() {
		return readerName;
	}

	/**
	 * This method sets the name of the logical reader.
	 * 
	 * @param name name of this logical reader
	 */
	public void setName(String name) {
		readerName = name;
	}
	
	public abstract void update(LRSpec lrSpec) throws ImplementationExceptionResponse;	
	public abstract void start();
	public abstract void stop();
	public abstract boolean hasSubscription();
	
	/**
	 * flags the reader as connected.
	 */
	public void setConnected() {
		connected = true;
	}
	
	/**
	 * flags the reader as disconnected.
	 */
	public void setDisconnected() {
		connected = false;
	}
	
	public boolean isConnected(){
		return connected;		
	}	
	
	@Override
	public void onNext(TagReadEvent tagReadEvent) {
		tagReadEvent.addExtraInformation(this.getName(),this.getName());		
		fireEvent(tagReadEvent);
	}
	
	class Unsubscriber implements Subscription
    {
        private List<Observer<TagReadEvent>> _observers;
        private Observer<TagReadEvent> _observer;

        public Unsubscriber(List<Observer<TagReadEvent>> observers,
            Observer<TagReadEvent> observer)
        {
            this._observers = observers;
            this._observer = observer;
        }


		@Override
		public boolean isUnsubscribed() {
			// TODO Auto-generated method stub
			if(this._observer != null && !this._observers.contains(this._observer))
				return true;
			return false;
		}

		@Override
		public void unsubscribe() {
			// TODO Auto-generated method stub
			if (this._observer != null
	                && this._observers.contains(this._observer))
	            {
	                this._observers.remove(_observer);
	            }
		}
    }

	
}
