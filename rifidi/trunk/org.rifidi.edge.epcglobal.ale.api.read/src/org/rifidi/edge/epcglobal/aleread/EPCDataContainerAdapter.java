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
import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.epcglobal.aleread.filters.ALEField;

/**
 * Adapter for extracting information from an EPC tag using TDT.
 * 
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

	/**
	 * Helper method for creating a string that can be consumed by tdt.
	 * 
	 * @param mem
	 * @param length
	 * @return
	 */
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

	/**
	 * Get a field by it's field spec.
	 * 
	 * @param field
	 * @param event
	 * @return
	 */
	public String getField(ALEField field, DatacontainerEvent event) {
		if (ALEFields.EPC.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.EPC_TAG;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.EPC;
			}
			return getEpc(event, type, format);
		}
		if (ALEFields.KILLPWD.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.UINT;
			}
			return getKillPwd(event, type, format);
		}
		if (ALEFields.ACCESSPWD.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.UINT;
			}
			return getAccessPwd(event, type, format);
		}
		if (ALEFields.EPCBANK.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.BITS;
			}
			return getEpcBank(event, type, format);
		}
		if (ALEFields.TIDBANK.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.BITS;
			}
			return getTidBank(event, type, format);
		}
		if (ALEFields.USERBANK.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.BITS;
			}
			return getUserBank(event, type, format);
		}
		if (ALEFields.AFI.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.UINT;
			}
			return getAfi(event, type, format);
		}
		if (ALEFields.NSI.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.UINT;
			}
			return getNsi(event, type, format);
		}
		if (ALEFields.ABSOLUTEADDRESS.equals(field.getName())) {
			ALEDataFormats format = field.getFormat();
			ALEDataTypes type = field.getType();
			if (ALEDataFormats.DEFAULT.equals(format)) {
				format = ALEDataFormats.HEX;
			}
			if (ALEDataTypes.DEFAULT.equals(type)) {
				type = ALEDataTypes.UINT;
			}
			return getAbsoluteAddressField(event, field.getBankId(), field
					.getLength(), field.getOffset(), type, format);
		}
		return null;
	}

	/**
	 * Get the epc field.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
	public String getEpc(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		if (event instanceof EPCGeneration1Event) {
			if (ALEDataTypes.EPC.equals(type)) {
				if (ALEDataFormats.EPC_TAG.equals(format)) {
					return engine.convert(
							createStringFromMem(((EPCGeneration1Event) event)
									.getEPCMemory(),
									((EPCGeneration1Event) event)
											.getEPCMemoryLength()),
							extraparams, LevelTypeList.TAG_ENCODING);
				}
				if (ALEDataFormats.EPC_PURE.equals(format)) {
					return engine.convert(
							createStringFromMem(((EPCGeneration1Event) event)
									.getEPCMemory(),
									((EPCGeneration1Event) event)
											.getEPCMemoryLength()),
							extraparams, LevelTypeList.PURE_IDENTITY);
				}
				if (ALEDataFormats.EPC_HEX.equals(format)) {
					return "urn:epc:raw:"
							+ ((EPCGeneration1Event) event)
									.getEPCMemoryLength()
							+ ".x"
							+ ((EPCGeneration1Event) event).getEPCMemory()
									.toString(16);
				}
				if (ALEDataFormats.EPC_DECIMAL.equals(format)) {
					return "urn:epc:raw:"
							+ ((EPCGeneration1Event) event)
									.getEPCMemoryLength()
							+ "."
							+ ((EPCGeneration1Event) event).getEPCMemory()
									.toString(10);
				}
			}
		}
		// TODO: invalid request, handle it better
		return null;
	}

	/**
	 * Access the kill password field.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
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

	/**
	 * Access the access password field.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
	public String getAccessPwd(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// gen2 doesn't have the access password
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

	/**
	 * Access the epc bank.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
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

	/**
	 * Acces the tid bank.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
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

	/**
	 * Access the user bank.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
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

	/**
	 * Get the afi value.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
	public String getAfi(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2 tags have an AFI
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getAfi();
				}
				if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getAfiDecimal();
				}
			}
		}
		return null;
	}

	/**
	 * Get the nsi value.
	 * 
	 * @param event
	 * @param type
	 * @param format
	 * @return
	 */
	public String getNsi(DatacontainerEvent event, ALEDataTypes type,
			ALEDataFormats format) {
		// only epc gen 2 tags have an AFI
		if (event instanceof EPCGeneration2Event) {
			if (ALEDataTypes.UINT.equals(type)) {
				if (ALEDataFormats.HEX.equals(format)) {
					return ((EPCGeneration2Event) event).getNsi();
				}
				if (ALEDataFormats.DECIMAL.equals(format)) {
					return ((EPCGeneration2Event) event).getNsiDecimal();
				}
			}
		}
		return null;
	}

	/**
	 * Access a field by it's absolute address.
	 * 
	 * @param event
	 * @param bank
	 * @param length
	 * @param offset
	 * @param type
	 * @param format
	 * @return
	 */
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

	/**
	 * Acces a variable field. Currently not supported. TODO: Not supported,
	 * need access to ISO 5962 to implement.
	 * 
	 * @param event
	 * @param bank
	 * @param OID
	 * @return
	 */
	public String getVariableField(DatacontainerEvent event, Integer bank,
			String OID) {

		return null;
	}

	/**
	 * Acces a variable field. Currently not supported. TODO: Not supported,
	 * need access to ISO 5962 to implement.
	 * 
	 * @param event
	 * @param bank
	 * @param OID
	 * @return
	 */
	public String getVariablePatternField(DatacontainerEvent event,
			Integer bank, String OID) {
		// TODO: Not supported, need access to ISO 5962 to implement
		return null;
	}
}
