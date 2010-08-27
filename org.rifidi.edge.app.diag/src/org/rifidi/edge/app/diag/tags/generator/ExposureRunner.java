/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import java.util.Collection;
import java.util.List;

import org.rifidi.edge.core.services.notification.data.ReadCycle;

import com.espertech.esper.client.EPRuntime;

/**
 * This class matches up an exposure with a tagset and runs them.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class ExposureRunner<T extends Exposure> implements Runnable {

	/** The tag set to run */
	private final TagSet tagSet;
	/** The list of tag data */
	protected List<AbstractReadData<?>> tagData;
	/** The exposure to run */
	protected final T exposure;
	/** The esper runtime */
	private final EPRuntime epRuntime;
	/** Turns true when the runner should stop */
	private volatile boolean shouldStop = false;
	/** The total number of tags seen so far */
	private int tagsSeen = 0;

	/**
	 * Constructor
	 * 
	 * @param epRuntime
	 *            Esper
	 * @param exposure
	 *            The exposure to run
	 * @param tags
	 *            The list of tags that should be used
	 */
	public ExposureRunner(EPRuntime epRuntime, T exposure,
			List<AbstractReadData<?>> tags) {
		this.epRuntime = epRuntime;
		this.exposure = exposure;
		this.tagSet = new TagSet(tags);
		this.tagData = tags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// set stop timer if we need one
		initializeTimer();
		while (!shouldStop) {
			List<ReadCycle> readCycles;
			if (exposure.getRandom()) {
				readCycles = tagSet.getRandomGroup(calculateGroupSize());
			} else {
				readCycles = tagSet.getNextGroup(calculateGroupSize());
			}
			sendTags(readCycles);
			tagsSeen += readCycles.size();
			checkTags();

			if (!shouldStop) {

				try {
					Thread.sleep(calculateDelay());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	/**
	 * Stop the ExposureRunner. Will not interrupt the runner if it is currently
	 * sleeping
	 */
	public void cancel() {
		this.shouldStop = true;
	}

	/**
	 * Calculate how many tags should be sent to esper at once
	 * 
	 * @return
	 */
	protected abstract int calculateGroupSize();

	/**
	 * Calculate the delay between two batches of tags being sent to esper
	 * 
	 * @return
	 */
	protected abstract long calculateDelay();

	/**
	 * Send tags to esper
	 * 
	 * @param readCycles
	 */
	protected void sendTags(Collection<ReadCycle> readCycles) {
		for (ReadCycle cycle : readCycles) {
			epRuntime.sendEvent(cycle);
		}
	}

	/**
	 * See if we should stop based on how many tags we've seen an the tagcount
	 * stop trigger
	 */
	private void checkTags() {
		if (exposure.getTagCountStopTrigger() <= 0) {
			return;
		}
		if (tagsSeen >= exposure.getTagCountStopTrigger()) {
			cancel();
		}
	}

	/**
	 * Initialize this runner.
	 */
	protected void initializeTimer() {
		if (exposure.getTimerStopTrigger() <= 0l)
			return;
		Thread timer = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(exposure.getTimerStopTrigger());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
				cancel();

			}
		});
		timer.start();
	}

}
