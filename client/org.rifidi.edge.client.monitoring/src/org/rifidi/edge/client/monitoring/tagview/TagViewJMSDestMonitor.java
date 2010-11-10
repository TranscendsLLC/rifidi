/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview;

import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.client.monitoring.AbstractJMSDestMonitor;
import org.rifidi.edge.client.monitoring.tagview.model.TagMessageFactory;
import org.rifidi.edge.client.monitoring.tagview.model.TagModel;
import org.rifidi.edge.client.monitoring.tagview.model.TagModelProviderSingleton;

/**
 * This class monitors the JMS destination "org.rifidi.tracking.tags". It parses
 * tag messages received on JMS and adds them to the tag model.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagViewJMSDestMonitor extends AbstractJMSDestMonitor {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.monitoring.AbstractJMSDestMonitor#handleMessage
	 * (java.lang.String)
	 */
	@Override
	protected void handleMessage(final String message) {

		// Adding tags to the model must happen in the eclipse thread.
		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				// Add the incoming tag message to the model
				TagModelProviderSingleton.getInstance().getTags().addTag(
						parseTag(message));

			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.monitoring.AbstractJMSDestMonitor#getDestination()
	 */
	@Override
	protected String getDestination() {
		return "org.rifidi.monitor.tags";
	}

	/**
	 * Parse the incoming message into a TagModel object
	 * 
	 * @param message
	 * @return
	 */
	private TagModel parseTag(String message) {
		return TagMessageFactory.parse(message);
	}

}
