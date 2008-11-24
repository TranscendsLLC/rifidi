package org.rifidi.edge.testing.llrp;

import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.readerplugin.llrp.plugin.LLRPReaderInfo;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class LLRPTagReadTest {

	private ReaderSessionService readerSessionService;

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
		ri.setMaxNumConnectionsAttempts(100);
		ReaderSession readerSession = readerSessionService
				.createReaderSession(ri);
		try {
			CommandConfiguration cc = new CommandConfiguration("StreamTags",
					new HashSet<CommandArgument>());
			readerSession.executeCommand(cc);

		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RifidiCommandInterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RifidiCommandNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Inject
	public void setReaderSessionService(ReaderSessionService service) {
		readerSessionService = service;
	}


}
