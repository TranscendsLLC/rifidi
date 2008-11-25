/*
 *  TDTEnginePool.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.fosstrak.tdt.pool;

import org.apache.commons.pool.impl.StackObjectPool;
import org.fosstrak.tdt.exported.TDTEngine;

/**
 * @author kyle
 * 
 */
public class TDTEnginePool {

	private StackObjectPool _pool;

	public TDTEnginePool() {
		_pool = new StackObjectPool(new TDTEnginePoolableObjectFactory(), 10, 0);
	}

	public TDTEngine borrowEngine() throws Exception {
		return (TDTEngine) _pool.borrowObject();
	}

	public void returnEngine(TDTEngine engine) throws Exception {
		_pool.returnObject(engine);
	}

}
