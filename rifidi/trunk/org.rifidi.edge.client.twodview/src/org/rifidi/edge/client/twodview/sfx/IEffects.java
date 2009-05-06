/* 
 *  IEffects.java
 *  Created:	Sep 3, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.sfx;
//TODO: Comments
import org.eclipse.swt.graphics.Color;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public interface IEffects {
	public int getMinAlpha();
	public int getMaxAlpha();
	public int getTimePeriod();
	public Color getColor();
	public void setMinAlpha(int minAlpha);
	public void setMaxAlpha(int maxAlpha);
	public void setTimePeriod(int time);
	public void setColor(Color color);
}
