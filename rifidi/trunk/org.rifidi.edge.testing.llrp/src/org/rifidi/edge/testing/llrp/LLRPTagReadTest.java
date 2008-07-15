package org.rifidi.edge.testing.llrp;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.readerplugin.llrp.plugin.LLRPReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class LLRPTagReadTest {

	private ReaderSessionService readerSessionService;
	private ReaderPluginService readerPluginService;

	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLLRPStream() {
		ReaderInfo ri = new LLRPReaderInfo();
		ri.setIpAddress("192.168.1.104");
		ri.setPort(5084);
		ri.setMaxNumConnectionsAttemps(100);
		ReaderSession readerSession = readerSessionService.createReaderSession(ri);
		try {
			readerSession.executeCommand("StreamTags", null);
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RifidiCommandInterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Inject
	public void setReaderSessionService(ReaderSessionService service) {
		readerSessionService = service;
	}
	
	@Inject 
	public void setReaderPluginService(ReaderPluginService service){
		readerPluginService = service;
	}

}
