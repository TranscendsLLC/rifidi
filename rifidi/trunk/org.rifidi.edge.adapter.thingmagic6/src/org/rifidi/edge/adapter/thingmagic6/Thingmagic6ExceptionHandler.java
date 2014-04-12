/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.thingmagic6;

import com.thingmagic.ReadExceptionListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;

public class Thingmagic6ExceptionHandler implements ReadExceptionListener {

	private Thingmagic6SensorSession session;

	public Thingmagic6ExceptionHandler(Thingmagic6SensorSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thingmagic.ReadExceptionListener#tagReadException(com.thingmagic.
	 * Reader, com.thingmagic.ReaderException)
	 */
	@Override
	public void tagReadException(Reader arg0, ReaderException arg1) {
		//System.out.println("Tag Read Exception!  " + arg1.getMessage() + " ... " + arg1.getCause());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		session.startReading();
	}

}
