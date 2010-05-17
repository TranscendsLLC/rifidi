/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.ReadCycle;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * @author Owner
 *
 */
public class ReadCycleFactory {
	
	public static ReadCycle createReadCycle(String id, String readerID){
		Set<TagReadEvent> tags = new HashSet<TagReadEvent>();
		EPCGeneration2Event tag = new EPCGeneration2Event();
		EPCGeneration2Event tag2 = new EPCGeneration2Event();
		try{
		tag.setEPCMemory(new BigInteger(Hex.decodeHex(id.toCharArray())), 96);
		tag2.setEPCMemory(new BigInteger(Hex.decodeHex("12346677".toCharArray())), 96);
		}catch(DecoderException e){
			tag.setEPCMemory(new BigInteger("0"), 96);
		}
		tags.add(new TagReadEvent(readerID, tag, 3, System.currentTimeMillis()));
		tags.add(new TagReadEvent(readerID, tag2, 3, System.currentTimeMillis()));
		ReadCycle cycle = new ReadCycle(tags, readerID, System.currentTimeMillis());
		return cycle;
	}

}
