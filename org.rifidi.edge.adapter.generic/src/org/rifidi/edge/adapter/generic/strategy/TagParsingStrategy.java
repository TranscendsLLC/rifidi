/**
 * Interface for tag parsing strategy
 */
package org.rifidi.edge.adapter.generic.strategy;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adapter.generic.dtos.GenericTagDTO;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Tag parsing strategy.  
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class TagParsingStrategy {
	

	/** Logger */
	private static final Log logger = LogFactory.getLog(TagParsingStrategy.class);
	
	/**
	 * Process tags coming in with JSON format: 
	 * 
	 * [{"id":"123456781234567812345678","antenna":1,"timestamp":1483735761055,"reader":"Alien_1","rssi":"123","extrainformation":"Direction:1|Speed:50"},
	 * {"id":"123456781234567800000000","antenna":1,"timestamp":1483735761055,"reader":"Alien_1","rssi":"123","extrainformation":"Direction:1|Speed:50"}]
	 * 
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public static Set<TagReadEvent> processTags(String json, boolean debug) throws IOException{
		Gson gson = new Gson();
		Type type = new TypeToken<List<GenericTagDTO>>(){}.getType();
		List<GenericTagDTO> dtolist = gson.fromJson(json, type);
		return dtoToTagReadEventSet(dtolist, debug);
	}
	
	/**
	 * Decodes a list of GenericTagDTO into a list of TagReadEvents.  
	 * 
	 * @param dtoList
	 * @param restdebug
	 * @return
	 */
	private static Set<TagReadEvent> dtoToTagReadEventSet(List<GenericTagDTO> dtoList, boolean restdebug) {
		Set<TagReadEvent> retval = new HashSet<TagReadEvent>();
		for(GenericTagDTO dto:dtoList) {
			EPCGeneration2Event gen2event = new EPCGeneration2Event();
			String val = dto.getEpc();
			int numbits = val.length() * 4;
			BigInteger epc;
			try {
				epc = new BigInteger(val, 16);
			} catch (Exception e) {
				throw new RuntimeException("Cannot decode ID: " + val);
			}
			gen2event.setEPCMemory(epc, val, numbits);
			TagReadEvent tag = new TagReadEvent(dto.getReader(), gen2event, dto.getAntenna(), dto.getTimestamp());
			if (dto.getRssi() != null && dto.getRssi()!="") {
				tag.addExtraInformation("RSSI", dto.getRssi());
			}
			if (dto.getExtrainformation() != null && dto.getExtrainformation() != "") {
				String[] pairs = dto.getExtrainformation().split("\\|");
				for(String s:pairs) {
					String[] kv = s.split(":");
					String key = kv[0];
					String value = kv[1];
					tag.addExtraInformation(key, value);
				}
			}
			if(restdebug) {
				logger.info("Adding a tag through REST: " + tag.toString());
			}
			retval.add(tag);
		}
		return retval;
	}
	
}
