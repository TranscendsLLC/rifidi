package org.rifidi.edge.epcglobal.aleread;

import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EPRuntime;

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
	private RifidiECSpec target = null;
	/** Period between two trigger events. */
	private Long period;
	/** Offset from midnight. */
	private Long offset;
	/** Difference between system and ale timezone. */
	private Long delta;
	/** The esper runtime. */
	private EPRuntime runtime;

	/**
	 * Constructor.
	 * 
	 * @param period
	 * @param offset
	 * @param timezone
	 * @param target
	 */
	public Trigger(Long period, Long offset, Long timezone, EPRuntime runtime) {
		super();
		this.period = period;
		this.offset = offset;
		this.delta = timezone - TimeZone.getDefault().getRawOffset();
		this.runtime = runtime;
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
	public RifidiECSpec getTarget() {
		return target;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (target != null) {
			if (start) {
				runtime.sendEvent(new StartEvent(target.getName()));
				return;
			}
			target.stop();
			return;
		}
		logger.warn("No target associated with trigger " + this);
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(RifidiECSpec target) {
		this.target = target;
	}

}