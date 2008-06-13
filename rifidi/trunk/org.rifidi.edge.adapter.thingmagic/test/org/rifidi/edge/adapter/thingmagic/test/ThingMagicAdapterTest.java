package org.rifidi.edge.adapter.thingmagic.test;

import java.util.List;

import javax.jms.ConnectionFactory;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.adapter.thingmagic.ThingMagicConnectionInfo;
import org.rifidi.edge.adapter.thingmagic.ThingMagicReaderAdapter;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ThingMagicAdapterTest {

	private ConnectionFactory connectionFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
		System.out.println("JUnit Test " + this.getClass().getName()
				+ " loaded.");
	}

	@After
	public void tearDown() throws Exception {
		System.out.println("JUnit Test " + this.getClass().getName()
				+ " unloaded.");
	}

	@Test
	public void testConnect() {
		ThingMagicConnectionInfo info = new ThingMagicConnectionInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(8080);

		ThingMagicReaderAdapter adapter = new ThingMagicReaderAdapter(info);

		try {
			adapter.connect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Assert.fail();
		}

		List<TagRead> tagReads = null;
		try {
			tagReads = adapter.getNextTags();
		} catch (RifidiAdapterIllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Illegal Adapter State.");
		}
		Assert.assertNotNull(tagReads);

		if (tagReads.size() == 0) {
			System.out.println("There are no tags read.");
		} else {
			for (TagRead tagRead : tagReads) {
				System.out.println(tagRead.toXML());
			}
		}
		
		try {
			adapter.disconnect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Error while disconecting.");
		}
	}

	/*
	 * @Test public void testTagRead(){ //TODO Jerry implement this test. }
	 */

	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
