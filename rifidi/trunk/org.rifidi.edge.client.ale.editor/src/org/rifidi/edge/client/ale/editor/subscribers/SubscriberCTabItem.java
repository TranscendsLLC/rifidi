/* 
 *  SubscriberCTabItem.java
 *  Created:	Apr 22, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.editor.subscribers;

import java.awt.event.TextEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Text;
import org.rifidi.edge.client.ale.models.ecspec.RemoteSpecModelWrapper;
import org.rifidi.edge.client.ale.models.lrspec.RemoteLrSpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SubscriberCTabItem extends CTabItem {

	private Text text = null;

	/**
	 * @param parent
	 * @param style
	 */
	public SubscriberCTabItem(CTabFolder parent, int style, RemoteSpecModelWrapper wrapper) {
		super(parent, style);
		parent.setLayout(new FillLayout());
		// The text widget to display the subscribers
		text = new Text(parent, SWT.MULTI | SWT.READ_ONLY);
		this.setControl(text);
		String textText="";
		for (String string : wrapper.getSubscribers()) {
			textText+=textText+"\n";
		}
		text.setText(textText);
	}

}
