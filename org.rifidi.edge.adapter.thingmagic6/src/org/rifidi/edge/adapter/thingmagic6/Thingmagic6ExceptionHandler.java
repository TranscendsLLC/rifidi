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
		//System.out.println("Tag Read Exception!  " + arg1.getMessage() + arg1.getCause());
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		session.startReading();
	}

}
