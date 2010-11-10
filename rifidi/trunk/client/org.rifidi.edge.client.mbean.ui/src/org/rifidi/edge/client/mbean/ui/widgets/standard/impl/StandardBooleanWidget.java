/*
 * StandardBooleanWidget.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.mbean.ui.widgets.standard.impl;

import javax.management.Attribute;

import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.BooleanWidgetData;

/**
 * This is a Widget that draws a control for a boolean MBean
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StandardBooleanWidget<T extends BooleanWidgetData> extends
		AbstractWidget<T> {

	/**
	 * Constructor.
	 * 
	 * @param data
	 */
	public StandardBooleanWidget(T data) {
		super(data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#createControl
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#disable()
	 */
	@Override
	public void disable() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#enable()
	 */
	@Override
	public void enable() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#getValue()
	 */
	@Override
	public Attribute getAttribute() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java
	 * .lang.Object)
	 */
	@Override
	public String setValue(Attribute value) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#validate()
	 */
	@Override
	public String validate() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget
	 * #dispose()
	 */
	@Override
	public void dispose() {

	}

}
