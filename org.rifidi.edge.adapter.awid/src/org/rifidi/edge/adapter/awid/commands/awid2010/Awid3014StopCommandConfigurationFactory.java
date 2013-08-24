/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
