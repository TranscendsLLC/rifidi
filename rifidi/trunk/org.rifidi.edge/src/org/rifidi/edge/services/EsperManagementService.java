/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

import com.espertech.esper.client.EPServiceProvider;

/**
 * Service for handling Esper instances.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface EsperManagementService {
	/**
	 * Get an esper service provider.
	 * 
	 * @return
	 */
	EPServiceProvider getProvider();
}
