/**
 * 
 */
package org.rifidi.app.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This application uses custom esper to look for unique tag events. If the tag
 * is found in a csv file, it prints out the product name. Please be aware that
 * the esper in this file is for demonstration purposes only. This esper
 * statement has the problem that events are never being removed from the data
 * window. Please see the esper documentation for more information about how to
 * write esper.
 * 
 * You can test this application without hooking up a real RFID reader by using
 * the TagGenerator diagnostic app. A sample dataset and exposure files have
 * been provided. Type 'startTagRunner group2 exposure2' in the OSGi command
 * prompt to kick it off. The TagGenerator app allows you to send tags right
 * into esper without using the Sensor Layer. It's useful for testing
 * applications. You can read more about the TagGenerator in the developer
 * documentation.
 * 
 */
public class AnotherApp extends AbstractRifidiApp {

	/** A sample property */
	private String property1;
	/** A sample property */
	private String property2;
	/**
	 * The list of products, where the key is a tagID and the value is a product
	 * name
	 */
	private HashMap<String, String> products = new HashMap<String, String>();

	public AnotherApp() {
		// 'Examples' is the group Name. 'AnotherApp' is the app name
		super("Examples", "AnotherApp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {

		processProductFile();

		// properties that were read in from the properties file
		System.out.println(property1 + " " + property2);

		// create a window that holds unique events
		addStatement("create window examplewindow.std:firstunique(tag.ID)"
				+ "as TagReadEvent");

		// insert tags into the window
		addStatement("insert into examplewindow select * from ReadCycle[select * from tags where antennaID=3]");

		// listen to events that are added to that window. See the esper
		// documentation for more about esper
		addStatement("select * from examplewindow",
				new StatementAwareUpdateListener() {

					@Override
					public void update(EventBean[] arg0, EventBean[] arg1,
							EPStatement arg2, EPServiceProvider arg3) {
						if (arg0 != null) {
							for (EventBean b : arg0) {
								TagReadEvent tag = (TagReadEvent) b
										.getUnderlying();
								String tagID = tag.getTag().getFormattedID();
								if (products.containsKey(tagID)) {
									System.out.println("Found a product: "
											+ products.get(tagID));
								} else {
									System.out
											.println("Found a tag without a matching product: "
													+ tagID);
								}
							}
						}

					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		// properties are read in from the properties file in the Example
		// folder
		property1 = getProperty("firstProperty", null);
		property2 = getProperty("secondProperty", null);
	}

	/**
	 * This method processes a file called 'acme-customers.csv' in the data
	 * folder. The file consists of two columns, the first is a tagID and the
	 * second is a product name. We put the ID-name values into the hashmap.
	 */
	private void processProductFile() {
		byte[] file = getDataFiles("acme").get("products");
		if (file == null) {
			return;
		}
		BufferedReader reader = new BufferedReader(new StringReader(new String(
				file)));
		try {
			String line = reader.readLine();
			while (line != null) {
				String[] values = line.split(",");
				this.products.put(values[0].trim().toLowerCase(), values[1]
						.trim().toLowerCase());
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#getCommandProvider()
	 */
	@Override
	protected CommandProvider getCommandProvider() {
		return new ExampleCommandProvider();
	}

	/**
	 * Applications can expose a command provider which allow them to interact
	 * with the OSGi console.
	 * 
	 */
	public class ExampleCommandProvider implements CommandProvider {

		/*
		 * Call this method by typing in 'exampleCommand <argument' on the OSGi
		 * command line
		 */
		public Object _exampleCommand(CommandInterpreter intp) {
			String s = intp.nextArgument();
			System.out.println("Example Command: " + s);
			return null;
		}

		@Override
		public String getHelp() {
			return "";
		}
	}

}
