/**
 * 
 */
package org.rifidi.edge.core.test.app.api;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.app.api.AppState;
import org.rifidi.edge.core.app.api.RifidiApp;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.services.esper.EsperManagementService;

/**
 * @author manoj
 *
 */
public class AbstractRifidiAppTest {
	public class RifidiAppRunner extends AbstractRifidiApp{
		HashMap<String,ReadZone> readZoneHash  = new HashMap<String,ReadZone>(); 

		/**
		 * @param group
		 * @param name
		 */
		public RifidiAppRunner(String group, String name) {
			super(group, name);
			// TODO Auto-generated constructor stub
		}
		
		public HashMap<String,ReadZone> getReadZones() {
			return super.getReadZones();
		}
		
	}
	RifidiAppRunner testSubject;
	
	
		
		
	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#getName()}.
	 */
	@Test
	public void test() {
		
	    testSubject = new RifidiAppRunner("AbstractRifidiAppTest", "rifidi");
	    	    
		assertEquals("AbstractRifidiAppTest", testSubject.getGroup());
		assertEquals("rifidi", testSubject.getName());
		assertEquals(AppState.STOPPED,testSubject.getState());
		boolean caught=false;
		String s = null;
		try{
		testSubject.start();
		}catch(IllegalStateException e){
			caught =true;
			s = e.getMessage();
		}
		assertEquals(true, caught);
		assertEquals("Application cannot be started "
					+ "until EsperManagementService has been injected", s);
		caught =false;
		EsperServiceMock esperService = new EsperServiceMock();
		testSubject.setEsperService(esperService);
		assertEquals(esperService,testSubject.getEsperService());
		
		caught=false;
		s = null;
		/* set up the testSubject so that you can test that the loadReadZones executes correctly */
		//Properties properties = new Properties();
		//properties.setProperty("org.rifidi.home", System.getProperty("user.dir"));
		//System.out.println(System.getProperty("user.dir"));
		//properties.setProperty("org.rifidi.edge.applications", "applications");
		//testSubject.setAppProperties(properties);
		//assertEquals(properties.getProperty("org.rifidi.home"), testSubject.getProperties());
		
		try{
			testSubject.start();
		}catch(IllegalStateException e){
			caught =true;
			s = e.getMessage();
		}
		
		assertEquals(false, caught);
		assertEquals(AppState.STARTED, testSubject.getState());
		
		/* test that the loadReadZones function was excecuted and did what we thought it does*/
		
		HashMap<String,ReadZone> readZones = new HashMap<String, ReadZone>();
		readZones= testSubject.getReadZones();
		
		if(readZones.isEmpty() == true){ 
			System.out.println("empty"); 
		}
		Iterator<String> i = readZones.keySet().iterator();
	
		assertEquals("dock_door", i.next().toString());
		assertEquals("weigh_station", i.next().toString());
				
	}
	
	

}
