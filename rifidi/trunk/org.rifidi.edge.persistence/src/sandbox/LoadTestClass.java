/* 
 * LoadTestClass.java
 *  Created:	Jul 11, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package sandbox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.persistence.xml.PersistedReaderInfo;
import org.rifidi.edge.readerplugin.llrp.plugin.LLRPReaderInfo;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LoadTestClass {

	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory.getLog(LoadTestClass.class);

	/**
	 * 
	 */
	public void test() {
		logger.debug("Starting the load test");
		PersistedReaderInfo pri = new PersistedReaderInfo();
		pri.loadFromFile();

		ReaderInfo llrp2 = new LLRPReaderInfo();
		llrp2.setIpAddress("localhosty");
		llrp2.setPort(20009);

		try {
			pri.removeReader(llrp2);
		} catch (RifidiReaderInfoNotFoundException e) {
			e.printStackTrace();
		}
	}
}
