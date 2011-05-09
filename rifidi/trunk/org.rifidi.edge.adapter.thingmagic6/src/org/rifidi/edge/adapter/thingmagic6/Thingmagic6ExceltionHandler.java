package org.rifidi.edge.adapter.thingmagic6;

import com.thingmagic.ReadExceptionListener;
import com.thingmagic.Reader;
import com.thingmagic.ReaderException;

public class Thingmagic6ExceltionHandler implements ReadExceptionListener {

	@Override
	public void tagReadException(Reader arg0, ReaderException arg1) {
		System.out.println("Tag Read Exception!  " + arg1.getMessage());
	}

}
