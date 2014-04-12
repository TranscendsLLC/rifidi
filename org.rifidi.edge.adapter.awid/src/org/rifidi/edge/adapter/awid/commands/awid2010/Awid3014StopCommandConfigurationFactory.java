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
package org.rifidi.edge.adapter.awid.commands.awid2010;

import org.rifidi.edge.adapter.awid.awid2010.Awid3014SensorFactory;

/**
 * @author Matthew Dean
 *
 */
public class Awid3014StopCommandConfigurationFactory extends
		Awid2010StopCommandConfigurationFactory {

	public static final String FACTORY3014_ID = "Awid3014-Push-Stop";
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.adapter.awid.commands.awid2010.Awid2010StopCommandConfigurationFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return FACTORY3014_ID;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.adapter.awid.commands.awid2010.Awid2010StopCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return Awid3014SensorFactory.FACTORY_ID;
	}
}
