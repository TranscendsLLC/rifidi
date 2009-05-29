package org.rifidi.edge.ale.esper.timer;

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.esper.events.StartEvent;
import org.rifidi.edge.esper.events.StopEvent;

import com.espertech.esper.client.EventSender;

/**
 * Runnable for time based triggers.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class Trigger implements Runnable {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(Trigger.class);
	/** True if this is a start trigger. */
	private boolean start = true;
	/** Target of the start/stop operations. */
	private Timer target = null;
	/** Period between two trigger events. */
	private Long period;
	/** Offset from midnight. */
	private Long offset;
	/** Difference between system and ale timezone. */
	private Long delta;
	/** Name of this trigger */
	private String uri;
	/** Event Sender*/
	private EventSender eventSender;
	
	public Trigger(String uri) throws InvalidURIExceptionResponse {

		// TODO: throw an exception if esper is not there{
		if (uri.startsWith("urn:epcglobal:ale:trigger:rtc:")) {
			String triggerValues = uri.substring(30);
			String[] values = triggerValues.split("\\.");
			try {
				// <period>.<offset>.<timezone>
				// the first value is the period and has to be supplied
				Long period = 0l;
				Long offset = 0l;
				Long timezone = 0l;
				if (values.length == 1) {
					period = Long.parseLong(triggerValues);
				}
				// get the offset if it exists
				else if (values.length > 1) {
					period = Long.parseLong(values[0]);
					offset = Long.parseLong(values[1]);
				}
				// get the timezone if it exists
				if (values.length == 3) {
					if (!(values[2].charAt(0) == 'Z')) {
						String[] timeVals = values[2].substring(1).split(":");
						timezone = (long) (Integer.parseInt(timeVals[0]) * 60 + Integer
								.parseInt(timeVals[1])) * 60 * 1000;
						if (values[2].charAt(0) == '-') {
							timezone *= -1;
						}
					}
				}
				this.uri = uri;
				this.period = period;
				this.offset = offset;
				this.delta = timezone - TimeZone.getDefault().getRawOffset();
				return;
			} catch (NumberFormatException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI: "+e);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI: "+e);
			}
		}
		throw new InvalidURIExceptionResponse(uri
				+ " is not a valid trigger URI.");

	}

	public long getDelayToNextExec() {
		return ((System.currentTimeMillis() + delta) + offset) % period;
	}

	/**
	 * @param start
	 *            the start to set
	 */
	public void setStart(boolean start) {
		this.start = start;
	}

	/**
	 * @return the start
	 */
	public boolean isStart() {
		return start;
	}

	/**
	 * @return the target
	 */
	public Timer getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(Timer target) {
		this.target = target;
	}

	/**
	 * @return the period
	 */
	public Long getPeriod() {
		return period;
	}

	/**
	 * @return the offset
	 */
	public Long getOffset() {
		return offset;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param eventSender the eventSender to set
	 */
	public void setEventSender(EventSender eventSender) {
		this.eventSender = eventSender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//TODO: do separate classes for the triggers
		if (target != null) {
			if (start) {
				eventSender.sendEvent(new StartEvent(uri));
				return;
			}
			eventSender.sendEvent(new StopEvent(uri));
			return;
		}
		logger.warn("No target associated with trigger " + this);
	}

}