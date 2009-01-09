package org.rifidi.dynamicswtforms.ui.widgets.listeners;

/**
 * A class that wants to listen to changes to widgets in a form should use this
 * interface
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface DynamicSWTWidgetListener {

	/**
	 * This is called when the user has finished editing data in a widget (and
	 * has hit the enter key, for example)
	 * 
	 * @param newData
	 *            The new value of the widget
	 */
	public void dataChanged(String newData);

	/**
	 * This method is called when the user has released a key. It can be used to
	 * do validation on key stroked, for example
	 */
	public void keyReleased();

}
