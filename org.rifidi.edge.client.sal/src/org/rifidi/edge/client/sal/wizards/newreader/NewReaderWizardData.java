/*
 * NewReaderWizardData.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

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
