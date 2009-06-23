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
 * Statement to listen for destroy events.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class DestroyEventTimingStatement extends AbstractSignalStatement {
	/** Timing statement. */
	private EPStatement stopTiming;
	/** Primary keys to identify a unique tag. */
	private Set<String> primarykeys;

	/**
	 * Constructor.
	 * 
	 * @param administrator
	 * @param specName
	 * @param primarykeys
	 */
	public DestroyEventTimingStatement(EPAdministrator administrator,
			Set<String> primarykeys) {
		super(administrator);
		this.primarykeys = primarykeys;
	}

	/**
	 * Initialize the statement.
	 */
	private void init() {
		stopTiming = administrator
				.createEPL("select res,stopEvent from pattern[every-distinct("
						+ assembleKeys(primarykeys)
						+ ") res=LogicalReader until destroyEvent=DestroyEvent]");
		stopTiming.addListener(new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (!stopTiming.isStopped()) {
					StopEvent stopper = null;
					stopTiming.stop();
					List<TagReadEvent> testEvents = new ArrayList<TagReadEvent>();
					for (EventBean eventBean : newEvents) {
						if ((TagReadEvent[]) eventBean.get("res") != null) {
							for (TagReadEvent event : (TagReadEvent[]) eventBean
									.get("res")) {
								testEvents.add(event);
							}
						}
						stopper = (StopEvent) eventBean.get("destroyEvent");
					}
					signalStop(ALEReadAPI.TriggerCondition.UNDEFINE, stopper,
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
