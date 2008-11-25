/* 
 * ReaderLabelProvider.java
 *  Created:	Aug 12, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.reader.content;

import org.eclipse.jface.viewers.LabelProvider;
import org.rifidi.edge.client.sitewizard.creator.ReaderObject;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class ReaderLabelProvider extends LabelProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof ReaderObject) {
			return ((ReaderObject) element).getName();
		} else if (element instanceof ReaderObjectList) {
			return ((ReaderObjectList) element).getText();
		}
		return null;
	}
}
