/**
 * 
 */
package org.rifidi.edge.ale.esper.timer;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EventSender;

/**
 * External timer for sending out start and stop events. TODO: create a global
 * timer!
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Timer {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Timer.class);
	private ScheduledThreadPoolExecutor executor;

	/**
	 * Constructor.
	 * 
	 * @param startTriggers
	 * @param stopTriggers
	 * @param esper
	 */
	public Timer(Set<Trigger> startTriggers, Set<Trigger> stopTriggers,
			EPServiceProvider esper) {
		logger.debug("New timer created. ");
		executor = new ScheduledThreadPoolExecutor(2);
		EventSender startSender = esper.getEPRuntime().getEventSender(
				"StartEvent");
		EventSender stopSender = esper.getEPRuntime().getEventSender(
				"StopEvent");
		for (Trigger trigger : startTriggers) {
			trigger.setTarget(this);
			trigger.setEventSender(startSender);
			trigger.setStart(true);
		}
		for (Trigger trigger : stopTriggers) {
			trigger.setTarget(this);
			trigger.setEventSender(stopSender);
			trigger.setStart(false);
		}
		for (Trigger trigger : startTriggers) {
			logger.debug("start trigger created: " + trigger.getPeriod()
					+ " ms");
			executor.scheduleAtFixedRate(trigger, trigger.getDelayToNextExec(),
					trigger.getPeriod(), TimeUnit.MILLISECONDS);
		}
		for (Trigger trigger : stopTriggers) {
			logger
					.debug("stop trigger created: " + trigger.getPeriod()
							+ " ms");
			executor.scheduleAtFixedRate(trigger, trigger.getDelayToNextExec(),
					trigger.getPeriod(), TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * Destroy the timer.
	 */
	public void destroy() {
		executor.shutdownNow();
	}
}
