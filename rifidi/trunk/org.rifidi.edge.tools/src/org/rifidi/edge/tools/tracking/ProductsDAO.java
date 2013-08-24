/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.tools.tracking;

/**
 * A Data Access Object that looks up a product name
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ProductsDAO {

	/**
	 * Returns the product name for the given ID
	 * 
	 * @param ID
	 * @return
	 */
	public String getProductName(String ID);
}
