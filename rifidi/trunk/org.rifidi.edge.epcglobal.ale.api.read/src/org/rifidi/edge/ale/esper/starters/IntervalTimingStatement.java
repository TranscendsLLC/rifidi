/**
 * 
 */
package org.rifidi.edge.ale.esper.starters;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author jochen
 * 
 */
public class IntervalTimingStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private EPStatement intervalTimer;
	/** Interval length. */
	private long intervalInMs;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param intervalInMs
	 */
	public IntervalTimingStatement(EPAdministrator administrator,
			long intervalInMs) {
		super(administrator);
		this.intervalInMs = intervalInMs;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		intervalTimer = administrator.createPattern("timer:interval("
				+ intervalInMs + " msec)");
		intervalTimer.addListener(new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents,
					EPStatement statement, EPServiceProvider epServiceProvider) {
				if (statement.isStopped()) {
					// activated because data gets purged from window
				} else {
					// order matters, first stop it, then signal listeners
					statement.stop();
					signalStart(ALEReadAPI.TriggerCondition.REPEAT_PERIOD, null);
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
		if (intervalTimer == null) {
			init();
			return;
		}
		if (!intervalTimer.isStarted()) {
			intervalTimer.start();
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
		if (intervalTimer == null) {
			return;
		}
		if (!intervalTimer.isStopped()) {
			intervalTimer.stop();
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
