/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.service.TriggerFactoryServiceImpl;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiBoundarySpec;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReadTriggerHandler {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReadTriggerHandler.class);
	/** Threadpool for executing the scheduled triggers. */
	private ScheduledExecutorService triggerpool;
	/** Boundary spec for the triggers. */
	private RifidiBoundarySpec boundarySpec;
	/** Guard for start and stop. */
	private ReentrantLock guard;
	/** True if the handler is running. */
	private boolean running;
	/** Owner of this handler. */
	private ECReportmanager manager;
	private Trigger buhu;

	/**
	 * Constructor.
	 * 
	 * @param manager
	 * @param boundarySpec
	 */
	public ReadTriggerHandler(ECReportmanager manager,
			RifidiBoundarySpec boundarySpec) {
		super();
		guard = new ReentrantLock();
		this.boundarySpec = boundarySpec;

		TriggerFactoryServiceImpl fact = new TriggerFactoryServiceImpl();
		try {
			buhu = fact.createTrigger("urn:epcglobal:ale:trigger:rtc:1000");
			buhu.setTarget(manager);
		} catch (InvalidURIExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		logger.debug("Starting read trigger handler");
		guard.lock();
		try {
			if (!running) {
				running = true;
				// TODO: we might have to manage the size of the pool size but
				// that
				// requires profiling
				triggerpool = new ScheduledThreadPoolExecutor(10);

				// schedule the start triggers
				for (Trigger trigger : boundarySpec.getStartTriggers()) {
					trigger.setTarget(manager);
					triggerpool.scheduleAtFixedRate(trigger, trigger
							.getDelayToNextExec(), trigger.getPeriod(),
							TimeUnit.MILLISECONDS);
				}

				triggerpool.scheduleAtFixedRate(buhu,
						buhu.getDelayToNextExec(), buhu.getPeriod(),
						TimeUnit.MILLISECONDS);
				logger.debug("Scheduled "
						+ boundarySpec.getStartTriggers().size()
						+ " start triggers. ");
				// schedule the stop triggers
				for (Trigger trigger : boundarySpec.getStopTriggers()) {
					trigger.setTarget(manager);
					triggerpool.scheduleAtFixedRate(trigger, trigger
							.getDelayToNextExec(), trigger.getPeriod(),
							TimeUnit.MILLISECONDS);
				}
				logger.debug("Scheduled "
						+ boundarySpec.getStopTriggers().size()
						+ " start triggers. ");
			}
		} finally {
			guard.unlock();
		}

	}

	public void stop() {
		logger.debug("Stopping read trigger handler");
		guard.lock();
		try {
			if (running) {
				// shut down the trigger pool
				logger.debug("Shutting down trigger pool.");
				triggerpool.shutdownNow();
			}
			running = false;
		} finally {
			guard.unlock();
		}
	}
}
