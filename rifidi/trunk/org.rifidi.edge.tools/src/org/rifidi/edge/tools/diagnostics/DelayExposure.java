/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.tools.diagnostics;

import java.util.List;
import java.util.Properties;


import com.espertech.esper.client.EPRuntime;

/**
 * A DelayExposure allows you to define the number of tags to send to esper
 * (groupSize) and then the time to wait after sending that many tags ("delay").
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DelayExposure extends Exposure {

	/***/
	private static final long serialVersionUID = 1L;
	public static final String PROPERTY_DELAY = "delay";
	public static final String PROPERTY_GROUPSIZE = "groupSize";
	
	
	public DelayExposure(Properties defaults) {
		super(defaults);
	}


	public int getDelay() {
		return Integer.parseInt(getProperty(PROPERTY_DELAY));
	}

	public int getGroupSize() {
		return Integer.parseInt(getProperty(PROPERTY_GROUPSIZE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.diagnostics.tags.generator.Exposure#createRunner(java.util
	 * .List, com.espertech.esper.client.EPRuntime)
	 */
	@Override
	public ExposureRunner<DelayExposure> createRunner(List<AbstractReadData<?>> tags,
			EPRuntime epRuntime) {
		return new DelayExposureRunner(epRuntime, this, tags);
	}
}
