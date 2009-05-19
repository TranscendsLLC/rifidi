
package org.rifidi.edge.client.twodview.sfx;

import org.eclipse.swt.graphics.Color;

/**
 * TODO: Class level comment.  
 * TODO: Method level comment(s).  
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
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
