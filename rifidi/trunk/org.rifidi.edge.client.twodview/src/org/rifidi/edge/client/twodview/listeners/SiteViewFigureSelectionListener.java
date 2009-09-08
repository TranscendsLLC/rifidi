/*
 * SiteViewFigureSelectionListener.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.twodview.listeners;

import org.eclipse.draw2d.IFigure;

/**
 * TODO: Jochen: Class level comment.  
 * 
 * @author jochen
 */
public interface SiteViewFigureSelectionListener {
	/**
	 * TODO: Method level comment.  
	 * 
	 * @param figure
	 */
	public void figureSelected(IFigure figure);

}
