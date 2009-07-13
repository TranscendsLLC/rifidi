/**
 * 
 */
package org.rifidi.edge.ale.esper.stoppers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.services.esper.events.StopEvent;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Statement that collects stop events.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StopEventTimingStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private volatile EPStatement stopTiming;
	/** Primary keys to identify a unique tag. */
	private final Set<String> primarykeys;
	/** Stream to collect data from. */
	private final String streamName;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param streamName
	 * @param primarykeys
	 */
	public StopEventTimingStatement(final EPAdministrator administrator,
			final String streamName, final Set<String> primarykeys) {
		super(administrator);
		this.primarykeys = primarykeys;
		this.streamName = streamName;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		stopTiming = administrator
				.createEPL("select res,stopEvent from pattern[every-distinct("
						+ assembleKeys(primarykeys) + ") res= " + streamName
						+ " until stopEvent=StopEvent]");
		stopTiming.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (!stopTiming.isStopped()) {
					StopEvent stopper = null;
					stopTiming.stop();
					List<TagReadEvent> testEvents = new ArrayList<TagReadEvent>();
					// extract the collected stop event
					for (EventBean eventBean : newEvents) {
						if ((TagReadEvent[]) eventBean.get("res") != null) {
							for (TagReadEvent event : (TagReadEvent[]) eventBean
									.get("res")) {
								testEvents.add(event);
							}
						}
						stopper = (StopEvent) eventBean.get("stopEvent");
					}
					// send stop signal
					signalStop(ALEReadAPI.TriggerCondition.TRIGGER, stopper,
							testEvents);
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see esperplayground.esper.StatementController#start()
	 */
	@Override
	public synchronized void start() {
		if (stopTiming == null) {
			init();
			return;
		}
		if (!stopTiming.isStarted()) {
			stopTiming.start();
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see esperplayground.esper.StatementController#stop()
	 */
	@Override
	public synchronized void stop() {
		if (stopTiming == null) {
			return;
		}
		if (!stopTiming.isStopped()) {
			stopTiming.stop();
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see esperplayground.esper.StatementController#needsRestart()
	 */
	@Override
	public boolean needsRestart() {
		return true;
	}

}
