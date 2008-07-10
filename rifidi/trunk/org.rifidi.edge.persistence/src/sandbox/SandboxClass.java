/* 
 * SandboxClass.java
 *  Created:	Jul 7, 2008
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
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class SandboxClass {
	/**
	 * The log4j logger.debug.
	 */
	private static final Log logger = LogFactory
			.getLog(SandboxClass.class);
	
	public void test() {
		logger.debug("Starting the test");
		PersistedReaderInfo pri = new PersistedReaderInfo();
		AlienReaderInfo ari = new AlienReaderInfo();
		ari.setIpAddress("localhost");
		ari.setPort(20000);
		ari.setUsername("alien");
		ari.setPassword("password");

		AlienReaderInfo ari2 = new AlienReaderInfo();
		ari2.setIpAddress("localhosty");
		ari2.setPort(20008);
		ari2.setUsername("alieny");
		ari2.setPassword("passwordy");

		ReaderInfo llrp = new LLRPReaderInfo();
		llrp.setIpAddress("localhosty");
		llrp.setPort(20009);
		
		ReaderInfo llrp2 = new LLRPReaderInfo();
		llrp2.setIpAddress("localhostx");
		llrp2.setPort(20008);
		
		pri.addReaderInfo(ari);
		pri.addReaderInfo(ari2);
		pri.addReaderInfo(llrp);
		pri.addReaderInfo(llrp2);
		
		try {
			logger.debug("Before the removes");
			pri.removeReader(llrp);
			logger.debug("After the first remove");
			pri.removeReader(llrp2);
			logger.debug("After the second remove");
		} catch (RifidiReaderInfoNotFoundException e) {
			e.printStackTrace();
		}
		
		AlienReaderInfo ari3 = new AlienReaderInfo();
		ari3.setIpAddress("localhostd");
		ari3.setPort(20007);
		ari3.setUsername("alienx");
		ari3.setPassword("passwordx");
		
		pri.addReaderInfo(ari3);
	}
}
