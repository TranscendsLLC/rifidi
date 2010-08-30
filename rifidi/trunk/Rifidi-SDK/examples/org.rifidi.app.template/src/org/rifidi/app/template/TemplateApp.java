/**
 * 
 */
package org.rifidi.app.template;

import org.rifidi.edge.core.app.api.AbstractRifidiApp;

/**
 * This is an app that developers can use as a template when getting started
 * with a new application.
 */
public class TemplateApp extends AbstractRifidiApp {

	public TemplateApp() {
		// 'Templates' is the group name. 'TemplateApp' is the app name
		super("Templates", "TemplateApp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {
		super._start();

		// subscribe to rifidservices, and create custom esper here.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_stop()
	 */
	@Override
	protected void _stop() {
		super._stop();

		// unsubscribe from rifidiservices here

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		super.initialize();
		// Read in properties here. This method will always be called before the
		// start method.

	}

}
