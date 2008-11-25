/*
 *  TDTEnginePoolableObjectFactory.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.fosstrak.tdt.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.fosstrak.tdt.exported.TDTEngine;

/**
 * @author kyle
 *
 */
public class TDTEnginePoolableObjectFactory implements PoolableObjectFactory {

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#activateObject(java.lang.Object)
	 */
	@Override
	public void activateObject(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#destroyObject(java.lang.Object)
	 */
	@Override
	public void destroyObject(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#makeObject()
	 */
	@Override
	public Object makeObject() throws Exception {
		return new TDTEngine();
	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#passivateObject(java.lang.Object)
	 */
	@Override
	public void passivateObject(Object arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.apache.commons.pool.PoolableObjectFactory#validateObject(java.lang.Object)
	 */
	@Override
	public boolean validateObject(Object arg0) {
		// TODO Auto-generated method stub
		return true;
	}

}
