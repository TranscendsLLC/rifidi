/*
 * 
 * StartEventStatement.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.esper.starters;

import org.rifidi.edge.ale.esper.AbstractSignalStatement;
import org.rifidi.edge.core.services.esper.StartEvent;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Statement that sends a signal as soon as a start event arrives.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class StartEventStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private EPStatement startTimer;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param specName
	 */
	public StartEventStatement(EPAdministrator administrator) {
		super(administrator);
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		startTimer = administrator
				.createEPL("select startEvent from StartEvent as startEvent");
		startTimer.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				signalStart(ALEReadAPI.TriggerCondition.TRIGGER,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see esperplayground.esper.StatementController#needsRestart()
	 */
	@Override
	public boolean needsRestart() {
		return false;
	}

}
