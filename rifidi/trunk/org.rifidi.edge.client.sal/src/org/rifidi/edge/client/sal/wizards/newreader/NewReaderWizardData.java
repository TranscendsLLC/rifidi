
package org.rifidi.edge.client.sal.wizards.newreader;

import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.RemoteReaderFactory;

/**
 * This class stores data that is collected from the NewReaderWizard
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class NewReaderWizardData {
	/** The factory that the user chose */
	protected RemoteReaderFactory factory;
	/** The connection properties that the user chose */
	protected AttributeList attributes;
}
