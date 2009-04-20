/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent;
import org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent;

import com.espertech.esper.client.EPServiceProvider;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Timer {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Timer.class);
	private ScheduledThreadPoolExecutor executor;
	private TimeRunnable runny;
	private long interval;
	private long intervalStart;
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	//if anybody sees this I am retiring to a job in fast food
	private ReentrantLock mainLock = new ReentrantLock();
	/** True if the runnable is executing. */
	private AtomicBoolean running = new AtomicBoolean(false);
	private boolean started = false;
	private Trigger lastTrigger = null;
	private ECReports currentReport = null;

	private EPServiceProvider esper;

	private String specName;

	public Timer(String specName, long interval, Set<Trigger> startTriggers,
			Set<Trigger> stopTriggers, EPServiceProvider esper) {
		this.specName = specName;
		for (Trigger trigger : startTriggers) {
			trigger.setTarget(this);
		}
		for (Trigger trigger : stopTriggers) {
			trigger.setTarget(this);
		}
		this.startTriggers = startTriggers;
		this.stopTriggers = stopTriggers;
		runny = new TimeRunnable();
		this.interval = interval;
		this.esper = esper;
	}

	/**
	 * Timer to fill the gap between two intervals when the duration is smaller
	 * than the interval.
	 * 
	 * @param millisecs
	 */
	public void time(long millisecs) {
		if (running.compareAndSet(false, true)) {
			executor.schedule(runny, millisecs, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Called when the ECSpec becomes requested. NOT THREADSAFE!
	 */
	public void start() {
		mainLock.lock();
		try {
			running.compareAndSet(true, false);
			lastTrigger = null;
			currentReport = new ECReports();
			executor = new ScheduledThreadPoolExecutor(startTriggers.size()
					+ stopTriggers.size() + 1);
			// no triggers, start right now
			if (startTriggers.size() == 0) {
				currentReport.setInitiationCondition(ALEReadAPI.conditionToName
						.get(ALEReadAPI.TriggerCondition.REQUESTED));
				startEventCycle();
			} else {
				for (Trigger trigger : startTriggers) {
					executor.scheduleAtFixedRate(trigger, trigger
							.getDelayToNextExec(), trigger.getPeriod(),
							TimeUnit.MILLISECONDS);
				}
			}
			for (Trigger trigger : stopTriggers) {
				executor.scheduleAtFixedRate(trigger, trigger
						.getDelayToNextExec(), trigger.getPeriod(),
						TimeUnit.MILLISECONDS);
			}
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Stop the timer. NOT THREADSAFE!
	 */
	public void stop() {
		mainLock.lock();
		try {
			executor.shutdownNow();
			executor = null;
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Called by interval timers.
	 */
	public void startEventCycle() {
		mainLock.lock();
		try {
			logger.debug("Starting event cycle");
			intervalStart = System.currentTimeMillis();
			esper.getEPRuntime().sendEvent(new StartEvent(specName));
			started = true;
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Called by triggers.
	 * 
	 * @param trigger
	 */
	public void startEventCycle(Trigger trigger) {
		mainLock.lock();
		try {
			logger.debug("Received start trigger " + trigger.getUri());
			if (started == true) {
				if (lastTrigger == null) {
					lastTrigger = trigger;
				}
				return;
			} else {
				currentReport.setInitiationCondition(ALEReadAPI.conditionToName
						.get(ALEReadAPI.TriggerCondition.TRIGGER));
				currentReport.setInitiationTrigger(trigger.getUri());
				startEventCycle();
			}
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Stop the event cycle. Only called by stop triggers.
	 */
	public void stopEventCycle(Trigger trigger) {
		mainLock.lock();
		try {
			logger.debug("Received stop trigger " + trigger.getUri());
			if (started) {
				currentReport
						.setTerminationCondition(ALEReadAPI.conditionToName
								.get(ALEReadAPI.TriggerCondition.TRIGGER));
				currentReport.setTerminationTrigger(trigger.getUri());
				logger.debug("Sending StopEvent for " + specName);
				esper.getEPRuntime().sendEvent(new StopEvent(specName));
				started = false;
			}
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Called when an ECReport is ready.
	 */
	public ECReports callback() {
		mainLock.lock();
		try {
			ECReports ret = currentReport;
			currentReport = new ECReports();
			if (startTriggers.size() == 0) {
				long currentTime = System.currentTimeMillis();
				if (intervalStart + interval <= currentTime) {
					intervalStart = currentTime;
					currentReport
							.setInitiationCondition(ALEReadAPI.conditionToName
									.get(ALEReadAPI.TriggerCondition.REPEAT_PERIOD));
					startEventCycle();
				} else {
					time(intervalStart + interval - currentTime);
				}
			} else {
				if (lastTrigger != null) {
					currentReport
							.setInitiationCondition(ALEReadAPI.conditionToName
									.get(ALEReadAPI.TriggerCondition.TRIGGER));
					currentReport.setInitiationTrigger(lastTrigger.getUri());
					lastTrigger = null;
					startEventCycle();
				}
			}
			return ret;
		} finally {
			mainLock.unlock();
		}
	}

	/**
	 * Used for waiting if a duration ends before the interval expires.
	 * 
	 * @author jochen
	 * 
	 */
	private class TimeRunnable implements Runnable {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			startEventCycle();
			running.compareAndSet(true, false);
		}

	}
}
