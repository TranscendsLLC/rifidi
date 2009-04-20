/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.aleread.rifidievents.StartEvent;
import org.rifidi.edge.epcglobal.aleread.rifidievents.StopEvent;

import com.espertech.esper.client.EPServiceProvider;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Timer {
	private ScheduledThreadPoolExecutor executor;
	private TimeRunnable runny;
	private long interval;
	private long intervalStart;
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	/** True if the runnable is executing. */
	private AtomicBoolean running = new AtomicBoolean(false);
	private AtomicReference<Trigger> trigger = new AtomicReference<Trigger>();
	private AtomicReference<ECReports> currentReport = new AtomicReference<ECReports>();

	private AtomicBoolean executing = new AtomicBoolean(false);

	private EPServiceProvider esper;

	public Timer(long interval, Set<Trigger> startTriggers,
			Set<Trigger> stopTriggers, EPServiceProvider esper) {
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
		if (executing.compareAndSet(false, true)) {
			running.compareAndSet(true, false);
			trigger.set(null);
			executor = new ScheduledThreadPoolExecutor(startTriggers.size()
					+ stopTriggers.size() + 1);
			// no triggers, start right now
			if (startTriggers.size() == 0) {
				ECReports report = new ECReports();
				report.setInitiationTrigger(ALEReadAPI.conditionToName
						.get(ALEReadAPI.TriggerCondition.REQUESTED));
				startEventCycle(report);
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
		}
	}

	/**
	 * Stop the timer. NOT THREADSAFE!
	 */
	public void stop() {
		if (executing.compareAndSet(true, false)) {
			executor.shutdownNow();
			executor = null;
		}
	}

	/**
	 * Called by interval timers.
	 */
	public void startEventCycle(ECReports report) {
		intervalStart = System.currentTimeMillis();
		if (currentReport.compareAndSet(null, report)) {
			esper.getEPRuntime().sendEvent(new StartEvent("bubble"));
		}
	}

	/**
	 * Called by triggers.
	 * 
	 * @param trigger
	 */
	public void startEventCycle(Trigger trigger) {
		// fastest way to get the last trigger that executed
		// if the trigger in trigger and the input for the method are the same
		// this means the trigger executed before the previous event cycle ended
		// the newly arrived trigger is stored in trigger
		if (this.trigger.compareAndSet(trigger, null)) {
			ECReports report = new ECReports();
			report.setInitiationTrigger(ALEReadAPI.conditionToName
					.get(ALEReadAPI.TriggerCondition.TRIGGER));
			report.setInitiationTrigger(trigger.getUri());
			startEventCycle(report);
		}
		// if the trigger in trigger and the input for the method are not the
		// same
		// the trigger executed after the previous event cycle has ended
		else if (this.trigger.compareAndSet(null, trigger)) {
			ECReports report = new ECReports();
			report.setInitiationTrigger(ALEReadAPI.conditionToName
					.get(ALEReadAPI.TriggerCondition.TRIGGER));
			report.setInitiationTrigger(trigger.getUri());
			startEventCycle(report);
		}

	}

	/**
	 * Stop the event cycle. Only called by stop triggers.
	 */
	public void stopEventCycle(Trigger trigger) {
		if (currentReport.get() != null) {
			currentReport.get().setTerminationCondition(
					ALEReadAPI.conditionToName
							.get(ALEReadAPI.TriggerCondition.TRIGGER));
			currentReport.get().setTerminationTrigger(trigger.getUri());
			esper.getEPRuntime().sendEvent(new StopEvent("waha"));
		}
	}

	/**
	 * Called when an ECReport is ready.
	 */
	public ECReports callback() {
		ECReports ret = currentReport.getAndSet(null);
		if (startTriggers.size() == 0) {
			long currentTime = System.currentTimeMillis();
			if (intervalStart + interval <= currentTime) {
				intervalStart = currentTime;

				ECReports report = new ECReports();
				report.setInitiationTrigger(ALEReadAPI.conditionToName
						.get(ALEReadAPI.TriggerCondition.REPEAT_PERIOD));
				startEventCycle(report);
			} else {
				time(intervalStart + interval - currentTime);
			}
		} else {
			Trigger trig = trigger.get();
			if (trig != null) {
				startEventCycle(trig);
			}
		}
		return ret;
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
			ECReports report = new ECReports();
			report.setInitiationTrigger(ALEReadAPI.conditionToName
					.get(ALEReadAPI.TriggerCondition.REPEAT_PERIOD));
			startEventCycle(report);
			running.compareAndSet(true, false);
		}

	}
}
