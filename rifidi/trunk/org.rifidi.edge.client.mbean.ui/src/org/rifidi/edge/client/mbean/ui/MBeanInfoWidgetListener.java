/*
 * MBeanInfoWidgetListener.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.mbean.ui;

/**
 * A class that wants to listen to changes to widgets in a form should use this
 * interface
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface MBeanInfoWidgetListener {

	/**
	 * This is called when the user has finished editing data in a widget (and
	 * has hit the enter key, for example). This means that the widget is dirty
	 * 
	 * @param newData
	 *            The new value of the widget
	 */
	public void dataChanged(String widgetName, String newData);

	/**
	 * This method is called when the user has released a key. It can be used to
	 * do validation on key stroked, for example
	 */
	public void keyReleased(String widgetName);

	/**
	 * Called whenever a widget decides that it is no longer dirty.
	 * 
	 * @param widgetName
	 */
	public void clean(String widgetName);

}
