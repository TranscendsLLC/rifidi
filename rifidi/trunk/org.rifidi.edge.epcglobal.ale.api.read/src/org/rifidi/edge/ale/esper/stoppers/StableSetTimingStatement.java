/**
 * 
 */
package org.rifidi.edge.ale.esper.stoppers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * A set is stable when after certain time interval no new tags have appeared.
 * This statement will wait for that condition, terminate and return the
 * collected tags.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StableSetTimingStatement extends AbstractSignalStatement {
	private long stableSetInMs;
	/** Timing statements. */
	private volatile EPStatement emptyStableSet;
	private volatile EPStatement stableSet;
	/** Primary keys to identify a unique tag. */
	private final Set<String> primarykeys;
	private final String streamName;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param streamName
	 * @param stableSetInMs
	 * @param primarykeys
	 */
	public StableSetTimingStatement(final EPAdministrator administrator,
			final String streamName, final long stableSetInMs,
			final Set<String> primarykeys) {
		super(administrator);
		this.stableSetInMs = stableSetInMs;
		this.primarykeys = primarykeys;
		this.streamName = streamName;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		emptyStableSet = administrator
				.createEPL("select 0 from pattern[timer:interval("
						+ stableSetInMs + " msec) and not " + streamName + "]");
		emptyStableSet.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (!emptyStableSet.isStopped()) {
					emptyStableSet.stop();
					if (!stableSet.isStopped()) {
						stableSet.stop();
					}
					List<TagReadEvent> ret = Collections.emptyList();
					signalStop(ALEReadAPI.TriggerCondition.STABLE_SET, null,
							ret);
				}
			}
		});
		stableSet = administrator
				.createEPL("select rstream res from pattern[every-distinct("
						+ assembleKeys(primarykeys) + ") res=" + streamName
						+ "].win:time_accum(" + stableSetInMs + " msec)");
		stableSet.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (!stableSet.isStopped()) {
					stableSet.stop();
					if (!emptyStableSet.isStopped()) {
						emptyStableSet.stop();
					}
					List<TagReadEvent> testEvents = new ArrayList<TagReadEvent>();
					for (EventBean eventBean : newEvents) {
						testEvents.add((TagReadEvent) eventBean.get("res"));
					}
					signalStop(ALEReadAPI.TriggerCondition.STABLE_SET, null,
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
		if (stableSet == null) {
			init();
			return;
		}
		if (!stableSet.isStarted()) {
			stableSet.start();
			emptyStableSet.start();
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
		if (stableSet == null) {
			return;
		}
		if (!stableSet.isStopped()) {
			stableSet.stop();
		}
		if (!emptyStableSet.isStopped()) {
			emptyStableSet.stop();
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
