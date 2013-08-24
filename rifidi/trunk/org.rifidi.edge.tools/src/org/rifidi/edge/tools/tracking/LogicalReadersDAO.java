/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.tools.tracking;

/**
 * Interface for a Data Access Object that looks up a Logical Reader Name
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface LogicalReadersDAO {

	/**
	 * Get a logical reader name for the given id and antnna
	 * 
	 * @param readerID
	 * @param antenna
	 * @return
	 */
	public String getLogicaReaderName(String readerID, int antenna);

}
