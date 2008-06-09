package test;


import org.junit.Assert;
import org.junit.Before;

import org.junit.Test;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.session.SessionRegistryServiceImpl;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class SesionRegistoryTest {

	
	
	private SessionRegistryService sessionRegistryService;
	
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}

	
	@Test
	public void testGetSessionRegistry(){
		Assert.assertNotNull(sessionRegistryService);
	}
	
	@Test
	public void testCreateSession(){
		SessionRegistryService sessionRegistryService2 = new SessionRegistryServiceImpl();
		
		int id = sessionRegistryService2.createReaderSession(new DummyConnectionInfo());
		
		Assert.assertTrue(id >= 0);
	}
	
	@Inject
	public void setSessionRegistryService(SessionRegistryService regSer){
		this.sessionRegistryService = regSer;
	}

}
