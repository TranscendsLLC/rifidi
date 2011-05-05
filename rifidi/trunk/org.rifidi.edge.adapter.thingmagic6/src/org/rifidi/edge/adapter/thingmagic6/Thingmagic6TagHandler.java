/**
 * 
 */
package org.rifidi.edge.adapter.thingmagic6;

import com.thingmagic.ReadListener;
import com.thingmagic.Reader;
import com.thingmagic.TagReadData;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class Thingmagic6TagHandler implements ReadListener {

	public Thingmagic6SensorSession session;

	public Thingmagic6TagHandler(Thingmagic6SensorSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * @see com.thingmagic.ReadListener#tagRead(com.thingmagic.Reader, com.thingmagic.TagReadData)
	 */
	@Override
	public void tagRead(Reader arg0, TagReadData arg1) {
		System.out.println(arg1.epcString() + ", " + arg1.getTag().getProtocol());
	}
	

	// private ReadCycle createReadCycle(TagReadData trd) {
	// Set<TagReadEvent> tagreaderevents = new HashSet<TagReadEvent>();
	// TagData tag = (TagData) trd.getTag();
	// //Assuming it is a gen2 at the moment
	//		
	// EPCGeneration2Event gen2event = new EPCGeneration2Event();
	// gen2event.setEPCMemory(memBank, length)
	//		
	// TagReadEvent tagevent = new TagReadEvent(session.getReaderID(), fdasdf,
	// trd.getAntenna(), trd.getTime());
	//		
	// return new ReadCycle(tagreaderevents, session.getReaderID(), System
	// .currentTimeMillis());
	// }

}
