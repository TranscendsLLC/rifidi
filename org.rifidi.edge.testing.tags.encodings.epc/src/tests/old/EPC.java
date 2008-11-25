/*
 *  EPC.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.old;

import java.math.BigInteger;
import java.util.HashMap;

import org.fosstrak.tdt.exported.LevelTypeList;
import org.fosstrak.tdt.exported.TDTEngine;
import org.fosstrak.tdt.exported.TDTException;
import org.fosstrak.tdt.pool.TDTEnginePool;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;
import org.rifidi.edge.tags.util.ConvertingUtil;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * 
 */
public class EPC {

	/**
	 * The header for EPC Pure format
	 */
	private static String EPC_PURE_HEADER = "urn:epc:id:";

	/**
	 * The header for EPC Tag format
	 */
	private static String EPC_TAG_HEADER = "urn:epc:tag:";

	/**
	 * The header for EPC RAW format
	 */
	private static String EPC_RAW_HEADER = "urn:epc:raw:";

	/**
	 * The PC bits which may be used if the EPC is not of a known encoding and
	 * the toggle bit is one
	 */
	private PCBits _pcBits;

	/**
	 * The epc in binary format
	 */
	private String _epcBits = null;

	/**
	 * The epc in pure format
	 */
	private String _pureURI = null;

	/**
	 * The epc in tag format
	 */
	private String _tagURI = null;

	/**
	 * The epc in raw format
	 */
	private String _rawURI = null;

	private TDTEnginePool enginePool = null;

	/**
	 * Create an EPC. The EPC can be in one of the four formats (bits (as a
	 * string of ones and zeros), pure URI, Tag URI, or raw). The PC Bits is the
	 * 16-bit PC section from the C1G2 EPC memory bank
	 * 
	 * @param epc
	 *            the epc in one of the four supported formats
	 * @param pcBits
	 *            a 16-bit binary bit string
	 * @throws IllegalArgumentException
	 *             if epc is null, pcBits is null, or epc is not in the correct
	 *             format
	 */
	public EPC(String epc, String pcBits) {
		if (null == epc) {
			throw new IllegalArgumentException("epc cannot be null");
		}
		if (null == pcBits) {
			throw new IllegalArgumentException("pcBits cannot be null");
		}
		ServiceRegistry.getInstance().service(this);
		_pcBits = new PCBits(pcBits);
		setAppropriateString(epc);
	}

	/**
	 * Create an EPC. The EPC can be in one of the four formats (bits (as a
	 * string of ones and zeros), pure URI, Tag URI, or raw).
	 * 
	 * If this constructor is used, a default PCBits object will be created
	 * which contains the length bits filled in, and 0s for the rest of it
	 * 
	 * @param epc
	 *            the epc in one of the four supported formats
	 * @throws IllegalArgumentException
	 *             if epc is null or epc is not in the correct format
	 */
	public EPC(String epc) {
		if (null == epc) {
			throw new IllegalArgumentException("epc cannot be null");
		}
		ServiceRegistry.getInstance().service(this);
		_pcBits = new PCBits(epc.length());
		setAppropriateString(epc);
	}

	/**
	 * Returns the EPC in the Pure URI format (i.e. begins with urn:epc:id).
	 * 
	 * @return the EPC as a pure URI
	 * @throws CannotConvertEPCException
	 *             if the EPC supplied in the constructor was not in PURE, BITS,
	 *             or TAG format
	 */
	public String toPureURI() throws CannotConvertEPCException {
		if (_pcBits.getToggleBit()) {
			throw new CannotConvertEPCException();
		}
		if (null != _pureURI) {
			return _pureURI;
		} else if (null != _epcBits) {
			return convert(_epcBits, LevelTypeList.PURE_IDENTITY);

		} else if (null != _tagURI) {
			return convert(_tagURI, LevelTypeList.PURE_IDENTITY);
		} else
			throw new CannotConvertEPCException();
	}

	/**
	 * Returns the EPC in the Tag URI format (i.e. begins with urn:epc:tag). If
	 * the toggle bit of the PCBits is set, then follow steps 5-8 of procedure
	 * 5.4 in TDS 1.4 to produce a raw tag with the following format:
	 * urn:epc:raw:N.xAFI.xID
	 * 
	 * @return the EPC as a tag URI or raw URI if it cannot be converted to
	 *         TagURI
	 * @throws CannotConvertEPCException
	 *             if the EPC supplied in the constructor was not in BITS or TAG
	 *             format
	 */
	public String toTagURI() throws CannotConvertEPCException {

		if (_pcBits.getToggleBit() && (_epcBits != null)) {
			// perform step 5-8 of procedure 5.4 in TDS 1.4
			int N = _pcBits.getLength();
			int A = _pcBits.getAFIBits().intValue();
			Double minBits = Math.ceil(new Double(N) / 4);
			String ID_hex = ConvertingUtil.toString(_epcBits, 2, 16,
					minBits.intValue()).toUpperCase();
			String A_hex = Integer.toHexString(A).toUpperCase();

			return EPC_RAW_HEADER + N + ".x" + A_hex + ".x" + ID_hex;
		}
		if (null != _tagURI) {
			return _tagURI;
		} else if (null != _epcBits) {
			try {
				return convert(_epcBits, LevelTypeList.TAG_ENCODING);
			} catch (CannotConvertEPCException e) {
				return toRawURI();
			}

		}
		throw new CannotConvertEPCException();

	}

	/**
	 * Returns the EPC in the Raw URI format (i.e. begins with urn:epc:raw)
	 * 
	 * @return the EPC as a raw URI
	 * @throws CannotConvertEPCException
	 *             if the EPC supplied in the constructor was not in BITS or RAW
	 *             format
	 */
	public String toRawURI() throws CannotConvertEPCException {
		if (null != _rawURI) {
			return _rawURI;
		}
		if (null != _epcBits) {

			int numBits = _epcBits.length();
			int minBits = ConvertingUtil.roundUpDivision(numBits, 4);
			String hex = ConvertingUtil.toString(_epcBits, 2, 16, minBits)
					.toUpperCase();
			return EPC_RAW_HEADER + numBits + ".x" + hex;

		} else
			throw new CannotConvertEPCException();
	}

	/**
	 * Returns the EPC as a bit string (i.e. as a string of 1s and 0s)
	 * 
	 * @return the EPC as a bit string
	 * @throws CannotConvertEPCException
	 *             if the EPC supplied in the constructor was not in BITS or TAG
	 *             format
	 */
	public String toBitString() throws CannotConvertEPCException {
		String retVal;

		if (null != _epcBits) {
			retVal = _epcBits;
		} else if (null != _tagURI) {
			retVal = convert(_tagURI, LevelTypeList.BINARY);

		} else {
			throw new CannotConvertEPCException();
		}
		return retVal;
	}

	/**
	 * Returns the EPC as Gen2TagMemory (according to procedure 5.6 of TDS 1.4.
	 * The gen2tag memory consists of a bit string where the first 16 bits are
	 * the PC bits and the other bits are the epc value.
	 * 
	 * This method returns the same string as the toBitString() method except
	 * that the first 16 bits are the PC Bits.
	 * 
	 * @return A binary string
	 * @throws CannotConvertEPCException
	 *             if the EPC supplied was not in BITS, TAG, or RAW (in the form
	 *             of urn:epc:raw:N.A.V) format
	 */
	public String toGen2Memory() throws CannotConvertEPCException {
		try {
			return _pcBits + toBitString();
		} catch (CannotConvertEPCException e) {
			if (null != _rawURI) {
				String[] NAV = _rawURI.split("\\.");
				// if form is urn:epc:raw:N.A.V
				if (NAV.length == 3) {
					// get N
					String N = NAV[0].split(":")[3];
					// get A
					String A = NAV[1].substring(1);
					// get V
					String V = NAV[2].substring(1);

					Double n = Double.parseDouble(N);
					int L = new Double((Math.ceil(n / 16))).intValue();
					if (L >= 32) {
						throw new CannotConvertEPCException();
					}
					int afi = Integer.parseInt(A, 16);
					if (afi >= 256) {
						throw new CannotConvertEPCException();
					}
					BigInteger value = new BigInteger(V, 16);
					if (value.bitLength() > n.intValue()) {
						throw new CannotConvertEPCException();
					}
					String pcBits = ConvertingUtil.toString(
							Integer.toString(L), 10, 2, 5)
							+ "00"
							+ "1"
							+ ConvertingUtil.toString(Integer.toString(afi),
									10, 2, 8);
					return pcBits + ConvertingUtil.toString(V, 16, 2, (16 * L));
				}
			}
			throw new CannotConvertEPCException();
		}

	}

	/**
	 * A private helper method to convert an EPC into a given format
	 * 
	 * @param input
	 *            The epc to convert
	 * @param level
	 *            the format to convert it to
	 * @return an epc in the specified format
	 * @throws CannotConvertEPCException
	 *             If there was a problem converting the EPC or with the TDT
	 *             engine
	 */
	private String convert(String input, LevelTypeList level)
			throws CannotConvertEPCException {
		TDTEngine engine = null;
		try {
			engine = enginePool.borrowEngine();
			String retVal = engine.convert(input,
					new HashMap<String, String>(), level);
			enginePool.returnEngine(engine);
			return retVal;
		} catch (TDTException ex) {
			throw new CannotConvertEPCException();
		} catch (Exception e) {
			throw new CannotConvertEPCException();
		} finally {
			try {
				if (engine != null) {
					enginePool.returnEngine(engine);
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * A private helper method that sets the correct private variable depending
	 * on which type the epc is
	 * 
	 * @param epc
	 *            the epc to set
	 */
	private void setAppropriateString(String epc) {
		if (isBits(epc)) {
			_epcBits = epc;
		} else if (isPure(epc)) {
			_pureURI = epc;
		} else if (isTag(epc)) {
			_tagURI = epc;
		} else if (isRaw(epc)) {
			_rawURI = epc;
		} else {
			throw new IllegalArgumentException("Format of " + epc
					+ " is not recognized");
		}
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the bits format
	 */
	private boolean isBits(String epc) {
		for (int i = 0; i < epc.length(); i++) {
			if ('0' != epc.charAt(i) && '1' != epc.charAt(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the pure format
	 */
	private boolean isPure(String epc) {
		return epc.substring(0, EPC_PURE_HEADER.length()).equals(
				EPC_PURE_HEADER);
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the tag format
	 */
	private boolean isTag(String epc) {
		return epc.substring(0, EPC_TAG_HEADER.length()).equals(EPC_TAG_HEADER);
	}

	/**
	 * 
	 * @param epc
	 *            the epc to test
	 * @return true if the epc is in the raw format
	 */
	private boolean isRaw(String epc) {
		return epc.substring(0, EPC_RAW_HEADER.length()).equals(EPC_RAW_HEADER);
	}

	@Inject
	public void setEnginePool(TDTEnginePool pool) {
		this.enginePool = pool;
	}

}
