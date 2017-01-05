package org.rifidi.edge.server.ale.infrastructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.JAXBException;

import org.rifidi.app.ale.AleApp;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.service.tagmonitor.LimitStableSetService;
import org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.StableSetService;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalService;
import org.rifidi.edge.api.service.tagmonitor.UniqueTagBatchIntervalSubscriber;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.utils.RifidiHelper;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;

public abstract class Device extends Observable<TagReadEvent>
		implements RawTagSubscriber{
	private static rx.Observable.OnSubscribe<TagReadEvent> f;

	private SensorManagerService sensorManagerService;

	private CommandManagerService commandManagerService;

	public ReaderDAO readerDAO;

	private RawTagMonitoringService rawTagMonitoringService;


	//private UniqueTagBatchIntervalService uniqueTagBatchIntervalService;

	private String deviceId;
	private LogicalReader logicalReader;
	private Map<String, String> settings;

	private boolean isRunning;
	private boolean isEnabled;
	private boolean isInit;

	private List<Observer<TagReadEvent>> observers = new ArrayList<Observer<TagReadEvent>>();

	
	protected RifidiHelper rifidiHelper;

	// Reference to AleApp to get the readzones
	//	private AleApp aleApp;

	public Device() {
		super(f);
	}

	public Device(String deviceId, LogicalReader logicalReader) {
		super(f);
		this.deviceId = deviceId;
		this.logicalReader = logicalReader;
		
	}

	public abstract void init()
			throws ValidationExceptionResponse, IOException, JAXBException;

	public abstract void start();

	public abstract void stop();

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public LogicalReader getLogicalReader() {
		// TODO Auto-generated method stub
		return this.logicalReader;
	}

	public void setLogicalReader(LogicalReader logicalReader) {
		// TODO Auto-generated method stub
		this.logicalReader = logicalReader;
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		if (this.isRunning != isRunning) {
			this.isRunning = isRunning;
			if (!this.isRunning) {
				
			} else {
				
			}
		}
	}

	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public Boolean getIsInit() {
		return isInit;
	}

	public void setIsInit(Boolean isInit) {
		this.isInit = isInit;
	}

	@Override
	public void tagArrived(TagReadEvent tag) {
		// TODO Auto-generated method stub
		tag.addExtraInformation(logicalReader.getName(), logicalReader.getName());
		for (Observer<TagReadEvent> observer : this.observers) {
			observer.onNext(tag);
		}
	}


	public Subscription subscribeObserver(Observer<TagReadEvent> observer) {

		if (!this.observers.contains(observer)) {
			observers.add(observer);
		}

		return new Unsubscriber(this.observers, observer);
	}

	class Unsubscriber implements Subscription {
		private List<Observer<TagReadEvent>> _observers;
		private Observer<TagReadEvent> _observer;

		public Unsubscriber(List<Observer<TagReadEvent>> observers, Observer<TagReadEvent> observer) {
			this._observers = observers;
			this._observer = observer;
		}

		@Override
		public boolean isUnsubscribed() {
			// TODO Auto-generated method stub
			if (this._observer != null && !this._observers.contains(this._observer))
				return true;
			return false;
		}

		@Override
		public void unsubscribe() {
			// TODO Auto-generated method stub
			if (this._observer != null && this._observers.contains(this._observer)) {
				this._observers.remove(_observer);
			}
		}
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

	public CommandManagerService getCommandManagerService() {
		return commandManagerService;
	}

	public ReaderDAO getReaderDAO() {
		return readerDAO;
	}

	public RawTagMonitoringService getRawTagMonitoringService() {
		return rawTagMonitoringService;
	}


	public SensorManagerService getSensorManagerService() {
		return sensorManagerService;
	}

	public RifidiHelper getRifidiHelper() {
		return rifidiHelper;
	}

	public void setRifidiHelper(RifidiHelper rifidiHelper) {
		this.rifidiHelper = rifidiHelper;
	}

	
	
}
