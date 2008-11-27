/* 
 *  ReaderAlphaImageFigure.java
 *  Created:	Sep 4, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.dashboard.twodview.sfx;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReaderAlphaImageFigure extends AlphaImageFigure {

	@Override
	public IFigure getToolTip() {
		this.setToolTip(this.createToolTip());
		return super.getToolTip();
	}

	RemoteReader reader;

	/**
	 * @return the reader
	 */
	public RemoteReader getReader() {
		return reader;
	}

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(RemoteReader reader) {
		this.reader = reader;
	}

	/**
	 * @param image
	 * @param reader
	 */
	public ReaderAlphaImageFigure(Image image, RemoteReader reader) {
		super(image);
		this.reader = reader;
		this.setToolTip(this.createToolTip());
	}

	public RemoteReaderID getReaderId() {
		return this.reader.getID();
	}
	
	private IFigure createToolTip(){
		Label ttl=null;
		if(reader==null){
			ttl = new Label("I AM JUST A DUMMY\nsorry, no status here");
		}else{
		ttl = new Label(reader.getDescription()+"\n"+reader.getReaderState());
		}
		return ttl;
	}
	
}
