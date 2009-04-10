/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.epcglobalinc.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.messages.DatacontainerEvent;
import org.rifidi.edge.core.messages.EPCGeneration1Event;
import org.rifidi.edge.core.messages.EPCGeneration2Event;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 *         TODO: we are missing the ability to handle non epc tags!!!!!!!!
 */
public class EPCDataContainerAdapter {

	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(EPCDataContainerAdapter.class);

	/** Tag data translation engine. */
	private TDTEngine engine;
	/** Empty hashmap for feeding to the engine. */
	private HashMap<String, String> extraparams = new HashMap<String, String>();

	/**
	 * Constructor.
	 */
	public EPCDataContainerAdapter() {

		try {
			engine = new TDTEngine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	private String createStringFromMem(BigInteger mem, int length) {
		String memString = mem.toString(2);
		int fill = length - memString.length();
		StringBuilder buildy = new StringBuilder(memString);
		// big integer swallows leading zeroes, reattech 'em
		while (fill > 0) {
			buildy.insert(0, "0");
			fill--;
		}
		return buildy.toString();
	}

	public String getEpc(DatacontainerEvent event) {
		return getEpc(event, ALEDataTypes.EPC, ALEDataFormats.EPC_TAG);
	}

	public String getEpc(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		if (event instanceof EPCGeneration1Event) {
			if (ALEDataTypes.EPC.equals(type)) {
				if (ALEDataFormats.EPC_TAG.equals(format)) {
					return engine.convert(createStringFromMem(
							((EPCGeneration1Event) event).getEPCMemory(),
							((EPCGeneration1Event) event).getEPCMemoryLength()),
							extraparams, LevelTypeList.TAG_ENCODING);
				}
				if (ALEDataFormats.EPC_PURE.equals(format)) {
					return engine.convert(createStringFromMem(
							((EPCGeneration1Event) event).getEPCMemory(),
							((EPCGeneration1Event) event).getEPCMemoryLength()),
							extraparams, LevelTypeList.PURE_IDENTITY);
				}
				if (ALEDataFormats.EPC_HEX.equals(format)) {
					return "urn:epc:raw:"
							+ ((EPCGeneration1Event) event).getEPCMemoryLength()
							+ ".x"
							+ ((EPCGeneration1Event) event).getEPCMemory()
									.toString(16);
				}
				if (ALEDataFormats.EPC_DECIMAL.equals(format)) {
					return "urn:epc:raw:"
							+ ((EPCGeneration1Event) event).getEPCMemoryLength()
							+ "."
							+ ((EPCGeneration1Event) event).getEPCMemory()
									.toString(10);
				}
			}
		}
		// TODO: invalid request, handle it better
		return null;
	}

	public String getKillPwd(DatacontainerEvent event) {
		return getKillPwd(event, ALEDataTypes.UINT, ALEDataFormats.HEX);
	}

	public String getKillPwd(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// gen2 doesn't have the kill password
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getKillPwd();
				} else if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getKillPwdDecimal();
				}
			}
		}
		return null;
	}

	public String getAccessPwd(DatacontainerEvent event) {
		return getAccessPwd(event, ALEDataTypes.UINT, ALEDataFormats.HEX);
	}

	public String getAccessPwd(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// gen2 doesn't have the kill password
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getAccessPwd();
				}
				if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getAccessPwdDecimal();
				}
			}
		}
		return null;
	}

	public String getEpcBank(DatacontainerEvent event) {
		return getEpcBank(event, ALEDataTypes.BITS, ALEDataFormats.HEX);
	}

	public String getEpcBank(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc tags have the bank
		if (event instanceof EPCGeneration1Event) {
			if (ALEDataTypes.BITS.equals(type)) {
				if (ALEDataFormats.EPC_HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getEPCMemoryLength()
							+ ":x"
							+ ((EPCGeneration1Event) event).getEPCMemory()
									.toString(16);
				}
			}
		}
		return null;
	}

	public String getTidBank(DatacontainerEvent event) {
		return getTidBank(event, ALEDataTypes.BITS, ALEDataFormats.HEX);
	}

	public String getTidBank(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2tags have the bank
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.BITS.equals(type)) {
				if (ALEDataFormats.EPC_HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getTIDMemoryLength()
							+ ":x"
							+ ((EPCGeneration2Event) event).getTIDMemory()
									.toString(16);
				}
			}
		}
		return null;
	}

	public String getUserBank(DatacontainerEvent event) {
		return getUserBank(event, ALEDataTypes.BITS, ALEDataFormats.HEX);
	}

	public String getUserBank(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2tags have the bank
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.BITS.equals(type)) {
				if (ALEDataFormats.EPC_HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getUserMemoryLength()
							+ ":x"
							+ ((EPCGeneration2Event) event).getUserMemory()
									.toString(16);
				}
			}
		}
		return null;
	}

	public String getAfi(DatacontainerEvent event) {
		return getEpc(event, ALEDataTypes.UINT, ALEDataFormats.HEX);
	}

	public String getAfi(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2 tags have an AFI
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getAFI();
				}
				if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getAFIDecimal();
				}
			}
		}
		return null;
	}

	public String getNsi(DatacontainerEvent event) {
		return getNsi(event, ALEDataTypes.UINT, ALEDataFormats.HEX);
	}

	public String getNsi(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2 tags have an AFI
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getNSI();
				}
				if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getNSIDecimal();
				}
			}
		}
		return null;
	}

	public String getAbsoluteAddressField(DatacontainerEvent event,
			Integer bank, Integer length, Integer offset) {
		return getAbsoluteAddressField(event, bank, length, offset,
				ALEDataTypes.UINT, ALEDataFormats.HEX);
	}

	public String getAbsoluteAddressField(DatacontainerEvent event,
			Integer bank, Integer length, Integer offset, ALEDataTypes type,
			ALEDataFormats format) {
		if (offset + length > event.getMemoryBank(bank).getLength()) {
			// index out of bounds
			return null;
		}
		if (ALEDataTypes.BITS.equals(type)) {
			if (ALEDataFormats.HEX.equals(format)) {
				return event.getMemoryBank(bank).getLength() + ":x"
						+ event.readMemory(bank, offset, length).toString(16);
			}
			return null;
		}
		if (ALEDataTypes.EPC.equals(type)) {
			if (ALEDataFormats.EPC_TAG.equals(format)) {
				return engine.convert(createStringFromMem(event.readMemory(
						bank, offset, length), event.getMemoryBank(bank)
						.getLength()), extraparams, LevelTypeList.TAG_ENCODING);
			}
			if (ALEDataFormats.EPC_PURE.equals(format)) {
				return engine
						.convert(createStringFromMem(event.readMemory(bank,
								offset, length), event.getMemoryBank(bank)
								.getLength()), extraparams,
								LevelTypeList.PURE_IDENTITY);
			}
			if (ALEDataFormats.EPC_HEX.equals(format)) {
				return "urn:epc:raw:" + length + ".x"
						+ event.readMemory(bank, offset, length).toString(16);
			}
			if (ALEDataFormats.EPC_DECIMAL.equals(format)) {
				return "urn:epc:raw:" + length + "."
						+ event.readMemory(bank, offset, length).toString(10);
			}
			return null;
		}
		if (ALEDataTypes.UINT.equals(type)) {
			if (ALEDataFormats.HEX.equals(format)) {
				return "x"
						+ event.readMemory(bank, offset, length).toString(16);
			}
			if (ALEDataFormats.DECIMAL.equals(format)) {
				return event.readMemory(bank, offset, length).toString(10);
			}
			return null;
		}
		return null;
	}

	public String getVariableField(DatacontainerEvent event, Integer bank,
			String OID) {
		// TODO: Not supported, need access to ISO 5962 to implement
		return null;
	}

	public String getVariablePatternField(DatacontainerEvent event,
			Integer bank, String OID) {
		// TODO: Not supported, need access to ISO 5962 to implement
		return null;
	}
}
