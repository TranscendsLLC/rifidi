/**
 * 
 */
package org.rifidi.edge.ale.esper.stoppers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This statement gets started when a new event cycle gets started and waits
 * until the duration expires.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DurationTimingStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private volatile EPStatement durationTiming;
	private final long durationInMs;
	/** Primary keys to identify a unique tag. */
	private final Set<String> primarykeys;
	/** Name of the event stream used for the statement. */
	private final String streamName;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param streamName
	 * @param durationInMs
	 * @param primarykeys
	 */
	public DurationTimingStatement(final EPAdministrator administrator,
			final String streamName, final long durationInMs,
			final Collection<String> primarykeys) {
		super(administrator);
		this.durationInMs = durationInMs;
		this.primarykeys = new HashSet<String>();
		this.primarykeys.addAll(primarykeys);
		this.streamName = streamName;
		primarykeys.addAll(primarykeys);
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		durationTiming = administrator
				.createEPL("select res from pattern[every-distinct("
						+ assembleKeys(primarykeys) + ") res=" + streamName
						+ "].win:time_batch(" + durationInMs
						+ " msec, \"FORCE_UPDATE, START_EAGER\")");
		durationTiming.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents,
					EPStatement statement, EPServiceProvider epServiceProvider) {
				if (!statement.isStopped()) {
					statement.stop();
					List<TagReadEvent> testEvents = new ArrayList<TagReadEvent>();
					if (newEvents != null) {
						for (EventBean eventBean : newEvents) {
							testEvents.add((TagReadEvent) eventBean.get("res"));
						}
					}
					signalStop(ALEReadAPI.TriggerCondition.DURATION, null,
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
		if (durationTiming == null) {
			init();
			return;
		}
		if (!durationTiming.isStarted()) {
			durationTiming.start();
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
		if (durationTiming == null) {
			return;
		}
		if (!durationTiming.isStopped()) {
			durationTiming.stop();
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
