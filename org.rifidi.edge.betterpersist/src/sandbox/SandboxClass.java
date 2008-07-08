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

import org.rifidi.edge.betterpersist.xml.PersistedReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.readerplugin.alien.AlienReaderInfo;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderInfo;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class SandboxClass {
	public void test() {
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

		ReaderInfo llrp2 = new LLRPReaderInfo();
		llrp2.setIpAddress("localhostx");
		llrp2.setPort(20008);
		
		System.out.println(llrp2.getClass().getName());

	}
}
