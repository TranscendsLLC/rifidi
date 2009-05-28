/**
 * 
 */
package org.rifidi.edge.ale.esper.stoppers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.messages.TagReadEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiECSpec;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author jochen
 * 
 */
public class DurationTimingStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private EPStatement durationTiming;
	private long durationInMs;
	/** Primary keys to identify a unique tag. */
	private Set<String> primarykeys;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param durationInMs
	 * @param primarykeys
	 */
	public DurationTimingStatement(EPAdministrator administrator,
			long durationInMs, Set<String> primarykeys) {
		super(administrator);
		this.durationInMs = durationInMs;
		this.primarykeys = primarykeys;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		durationTiming = administrator
				.createEPL("select res from pattern[every-distinct("
						+ assembleKeys(primarykeys)
						+ ") res=LogicalReader].win:time_batch("
						+ durationInMs
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

	/* (non-Javadoc)
	 * @see esperplayground.esper.StatementController#needsRestart()
	 */
	@Override
	public boolean needsRestart() {
		return true;
	}

}
