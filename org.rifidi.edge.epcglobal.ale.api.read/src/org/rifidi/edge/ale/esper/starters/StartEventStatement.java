/**
 * 
 */
package org.rifidi.edge.ale.esper.starters;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiECSpec;
import org.rifidi.edge.esper.events.StartEvent;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * @author jochen
 * 
 */
public class StartEventStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private EPStatement startTimer;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 */
	public StartEventStatement(EPAdministrator administrator) {
		super(administrator);
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		startTimer = administrator
				.createPattern("select startEvent from StartEvent as startEvent");
		startTimer.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				signalStart(RifidiECSpec.STARTREASON_STARTEVENT,
						(StartEvent) newEvents[0].get("startEvent"));
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
		if (startTimer == null) {
			init();
			return;
		}
		if (!startTimer.isStarted()) {
			startTimer.start();
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
		if (startTimer == null) {
			return;
		}
		if (!startTimer.isStopped()) {
			startTimer.stop();
			return;
		}
	}

	/* (non-Javadoc)
	 * @see esperplayground.esper.StatementController#needsRestart()
	 */
	@Override
	public boolean needsRestart() {
		return false;
	}

}
