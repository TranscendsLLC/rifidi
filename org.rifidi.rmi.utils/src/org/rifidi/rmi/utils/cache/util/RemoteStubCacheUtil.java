/*
 *  RemoteStubCacheUtil.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.rmi.utils.cache.util;

import java.rmi.Remote;

import org.rifidi.rmi.utils.cache.RemoteStubCache;
import org.rifidi.rmi.utils.cache.ServerDescription;

/**
 * 
 * This class is used by RMI Command objects who need access to the cache --
 * this breaks the "invisibility" concept of the cache somewhat, but may be
 * necessary for performance reasons
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteStubCacheUtil {

	public static void put(ServerDescription desc, Remote stub) {
		RemoteStubCache.put(desc, stub);
	}
	
	public static void remove(ServerDescription desc){
		RemoteStubCache.removeStubFromCache(desc);
	}

}
