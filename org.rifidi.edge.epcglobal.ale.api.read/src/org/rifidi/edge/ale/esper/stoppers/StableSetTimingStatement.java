/**
 * 
 */
package org.rifidi.edge.ale.esper.stoppers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiECSpec;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * @author jochen
 * 
 */
public class StableSetTimingStatement extends AbstractSignalStatement {
	private long stableSetInMs;
	/** Timing statements. */
	private EPStatement emptyStableSet;
	private EPStatement stableSet;
	/** Primary keys to identify a unique tag. */
	private Set<String> primarykeys;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param stableSetInMs
	 * @param primarykeys
	 */
	public StableSetTimingStatement(EPAdministrator administrator,
			long stableSetInMs, Set<String> primarykeys) {
		super(administrator);
		this.stableSetInMs = stableSetInMs;
		this.primarykeys = primarykeys;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		emptyStableSet = administrator
				.createEPL("select 0 from pattern[timer:interval("
						+ stableSetInMs + " msec) and not TestEvent]");
		emptyStableSet.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (!emptyStableSet.isStopped()) {
					emptyStableSet.stop();
					if (!stableSet.isStopped()) {
						stableSet.stop();
					}
					List<TagReadEvent> ret = Collections.emptyList();
					signalStop(ALEReadAPI.TriggerCondition.STABLE_SET, null, ret);
				}
			}
		});
		stableSet = administrator
				.createEPL("select rstream res from pattern[every-distinct("
						+ assembleKeys(primarykeys)
						+ ") res=LogicalReader].win:time_accum("
						+ stableSetInMs + " msec)");
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
