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
 * External timer for sending out start and stop events.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Timer {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Timer.class);
	private ScheduledThreadPoolExecutor executor;
	private Set<Trigger> startTriggers;
	private Set<Trigger> stopTriggers;
	private EPServiceProvider esper;

	/**
	 * Constructor.
	 * 
	 * @param startTriggers
	 * @param stopTriggers
	 * @param esper
	 */
	public Timer(Set<Trigger> startTriggers, Set<Trigger> stopTriggers,
			EPServiceProvider esper) {
		executor = new ScheduledThreadPoolExecutor(2);
		EventSender startSender=esper.getEPRuntime().getEventSender("StartEvent");
		EventSender stopSender=esper.getEPRuntime().getEventSender("StopEvent");
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
		this.startTriggers = startTriggers;
		this.stopTriggers = stopTriggers;
		for (Trigger trigger : startTriggers) {
			System.out.println("startdelay "+trigger.getDelayToNextExec());
			executor.scheduleAtFixedRate(trigger, trigger.getDelayToNextExec(),
					trigger.getPeriod(), TimeUnit.MILLISECONDS);
		}
		for (Trigger trigger : stopTriggers) {
			System.out.println("stopdelay "+trigger.getDelayToNextExec());
			executor.scheduleAtFixedRate(trigger, trigger.getDelayToNextExec(),
					trigger.getPeriod(), TimeUnit.MILLISECONDS);
		}
		this.esper = esper;
	}
}
