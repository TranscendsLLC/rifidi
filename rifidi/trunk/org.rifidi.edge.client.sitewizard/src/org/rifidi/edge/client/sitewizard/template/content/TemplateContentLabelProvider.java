/* 
 * TemplateContentLabelProvider.java
 *  Created:	Aug 12, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.template.content;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class TemplateContentLabelProvider extends LabelProvider {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		return ((TemplateContent) element).getName();
	}
}
