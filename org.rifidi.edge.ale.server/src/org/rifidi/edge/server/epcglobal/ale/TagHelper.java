/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.epcglobalinc.tdt.LevelTypeList;
import org.fosstrak.tdt.TDTEngine;
import org.fosstrak.tdt.TDTException;
import org.rifidi.edge.epcglobal.ale.EPC;
import org.rifidi.edge.epcglobal.ale.ECReportGroupListMember;
import org.rifidi.edge.epcglobal.ale.ECReportOutputSpec;

public final class TagHelper {

	/** logger. */
	//private static final Logger LOG = Logger.getLogger(TagHelper.class);

	/** instance of the TDT engine used for tag conversion. */
	private static TDTEngine engine;

	public static final String EXTRA_PARAMS_COMPANYPREFIXLENGTH = "gs1companyprefixlength";
	public static final String EXTRA_PARAMS_FILTER = "filter";
	public static final String EXTRA_PARAMS_TAGLENGTH = "taglength";
	
	/**
	 * private utility class
	 */
	private TagHelper() {
	}
	
	/**
	 * extract the binary representation of the given tag - this method is NOT performing any transformations!
	 * @param tag the tag.
	 * @return the tags binary representation.
	 */
	public static String getBinaryRepresentation(Tag tag) {
		if ((null != tag) && (tag.getTagAsBinary() != null)) {
			return tag.getTagAsBinary();
		}
		//LOG.error("missing binary representation of the tag: " + tag);
		return null;
	}

	/**
	 * determine if a tag is to be included into the output in the format raw decimal.
	 * @param outputSpec the report output specification.
	 * @return true if include, false otherwise. upon exception, false is returned.
	 */
	public static boolean isReportOutputSpecIncludeRawDecimal(ECReportOutputSpec outputSpec) {
		try {
			return outputSpec.isIncludeRawDecimal();
		} catch (Exception ex) {
			//LOG.debug("exception while determining if tag is to be included as raw decimal, thus not including the tag: ", ex);
		}
		return false;
	}

	/**
	 * add a tag in raw decimal format to the group member. the given tag must be submitted with the binary format.
	 * @param tdt the TDT to be used for the transformation from binary to raw decimal representation.
	 * @param groupMember the group member where to add the transformed tag.
	 * @param tag the tag to be transformed.
	 * @return the converted tag if the tag was added, null otherwise.
	 */
	public static String addTagAsRawDecimal(TDTEngine tdt, ECReportGroupListMember groupMember, Tag tag) {
		try {
			String bin = getBinaryRepresentation(tag);
			if (null != bin) {
				EPC epc = new EPC();
				final String converted = TagFormatHelper.formatAsRawDecimal(bin.length(), tdt.bin2dec(bin));
				epc.setValue(converted);
				groupMember.setRawDecimal(epc);
				return converted;
			}
		} catch (Exception ex) {
			//LOG.error("caught exception during tag transformation: ", ex);
		}
		return null;
	}

	/**
	 * determine if a tag is to be included into the output in the format tag encoding.
	 * @param outputSpec the report output specification.
	 * @return true if include, false otherwise. upon exception, false is returned.
	 */
	public static boolean isReportOutputSpecIncludeTagEncoding(ECReportOutputSpec outputSpec) {
		try {
			return outputSpec.isIncludeTag();
		} catch (Exception ex) {
			//LOG.debug("exception while determining if tag is to be included as tag encoding, thus not including the tag: ", ex);
		}
		return false;
	}

	/**
	 * add a tag in tag encoding format to the group member. the given tag must be submitted with the binary format.
	 * @param tdt the TDT to be used for the transformation from binary to tag encoding representation.
	 * @param groupMember the group member where to add the transformed tag.
	 * @param tag the tag to be transformed.
	 * @return the converted tag if the tag was added, null otherwise.
	 */
	public static String addTagAsTagEncoding(TDTEngine tdt, ECReportGroupListMember groupMember, Tag tag) {
		try {
			String bin = getBinaryRepresentation(tag);
			if (null != bin) {
				EPC epc = new EPC();
				final String converted = convert_to_TAG_ENCODING(tag.getTagLength(), tag.getFilter(), tag.getCompanyPrefixLength(), bin, tdt);
				epc.setValue(converted);
				groupMember.setTag(epc);	
				return converted;
			}
		} catch (Exception ex) {
			//LOG.error("caught exception during tag transformation: ", ex);
		}
		return null;
	}

	/**
	 * determine if a tag is to be included into the output in the format raw hex.
	 * @param outputSpec the report output specification.
	 * @return true if include, false otherwise. upon exception, false is returned.
	 */
	public static boolean isReportOutputSpecIncludeRawHex(ECReportOutputSpec outputSpec) {
		try {
			return outputSpec.isIncludeRawHex();
		} catch (Exception ex) {
			//LOG.debug("exception while determining if tag is to be included as raw hex, thus not including the tag: ", ex);
		}
		return false;
	}

	/**
	 * add a tag in raw hex format to the group member. the given tag must be submitted with the binary format.
	 * @param tdt the TDT to be used for the transformation from binary to raw hex representation.
	 * @param groupMember the group member where to add the transformed tag.
	 * @param tag the tag to be transformed.
	 * @return the converted tag if the tag was added, null otherwise.
	 */
	public static String addTagAsRawHex(TDTEngine tdt, ECReportGroupListMember groupMember, Tag tag) {
		try {
			String bin = getBinaryRepresentation(tag);
			if (null != bin) {
				EPC epc = new EPC();
				final String converted = TagFormatHelper.formatAsRawHex(bin.length(), tdt.bin2hex(bin));
				epc.setValue(converted);
				groupMember.setRawHex(epc);	
				return converted;
			}
		} catch (Exception ex) {
			//LOG.error("caught exception during tag transformation: ", ex);
		}
		return null;
	}

	/**
	 * determine if a tag is to be included into the output in the format EPC.
	 * @param outputSpec the report output specification.
	 * @return true if include, false otherwise. upon exception, false is returned.
	 */
	public static boolean isReportOutputSpecIncludeEPC(ECReportOutputSpec outputSpec) {
		try {
			return outputSpec.isIncludeEPC();
		} catch (Exception ex) {
			//LOG.debug("exception while determining if tag is to be included as EPC, thus not including the tag: ", ex);
		}
		return false;
	}

	/**
	 * add a tag in EPC format to the group member. the given tag must be submitted with the binary format.
	 * @param tdt the TDT to be used for the transformation from binary to EPC representation.
	 * @param groupMember the group member where to add the transformed tag.
	 * @param tag the tag to be transformed.
	 * @return the converted tag if the tag was added, null otherwise.
	 */
	public static String addTagAsEPC(TDTEngine tdt, ECReportGroupListMember groupMember, Tag tag) {
		try {
			String bin = getBinaryRepresentation(tag);
			if (null != bin) {
				EPC epc = new EPC();
				final String converted = convert_to_PURE_IDENTITY(tag.getTagLength(), tag.getFilter(), tag.getCompanyPrefixLength(), bin, tdt);
				epc.setValue(converted);
				groupMember.setEpc(epc);
				return converted;
			}
		} catch (Exception ex) {
			//LOG.error("caught exception during tag transformation: ", ex);
		}
		//LOG.debug("instead setting tag as pure URI");
		// TODO: check with the EPC/ALE Specification if this is correct.
		EPC epc = new EPC();
		epc.setValue(tag.getTagIDAsPureURI());
		groupMember.setEpc(epc);		
		return tag.getTagIDAsPureURI();
	}
	
	//---------------------------------- TAG CONVERSION -----------------------------------------------

	/**
	 * allows to inject a new instance of the TDT engine into the TagHelper.
	 * @param tdt the new TDT to be used.
	 */
	public static void setTDTEngine(TDTEngine tdt) {
		engine = tdt;
	}
	
	/**
	 * returns a handle onto the currently used TDT engine.
	 * @return the used TDT engine.
	 */
	public static synchronized TDTEngine getTDTEngine() {
		if (engine == null) {
			try {
				//LOG.debug("Initialize TDT Engine for tag translation.");
				URL auxiliary = TagHelper.class.getClassLoader().getResource("auxiliary/ManagerTranslation.xml");
				//Set<URL> schemes = new HashSet<URL>(Collections.list((TagHelper.class.getClassLoader().getResources("schemes/SGTIN-96.xml"))));
				URL schemes = TagHelper.class.getClassLoader().getResource("schemes/SGTIN-96.xml");
				engine = new TDTEngine(auxiliary, schemes);
			} catch (Exception e) {
				//LOG.error("could not create an instance of the TDT Engine - aborting: ", e);
				throw new RuntimeException("could not create an instance of the TDT Engine - aborting: ", e);
			}
		}
		return engine;
	}
	
	/**
	 * converts a given input with the given TDT to the desired output format.
	 * @param input the tag to convert. Must be in binary format or in TAG_ENCODING.
	 * @param extraparms conversion parameters.
	 * @param outputLevel the destination format.
	 * @return the converted tag.
	 * @throws TDTException whenever a tag conversion error occurs.
	 */
	public static String convert(String input, Map<String, String> extraparms, LevelTypeList outputLevel, TDTEngine tdt) throws TDTException {
		try {
			return tdt.convert(input, extraparms, outputLevel);			
		} catch (NullPointerException npe) {
			//LOG.error("caught NullPointerException during transformation - could not perform the transformation ", npe);
			throw new TDTException("caught NullPointerException during transformation - could not perform the transformation " + npe.getMessage());
		}
	}
	
	/**
	 * converts a given tag through TDT into LEGACY format - convenience method using the TagHelpers internal TDT for the transformation.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range  depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_LEGACY(String tagLength, String filter, String companyPrefixLength, String tag) {
		return convert_to_LEGACY(tagLength, filter, companyPrefixLength, tag, getTDTEngine());
	}
	
	/**
	 * converts a given tag through tdt into LEGACY format.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range  depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_LEGACY(String tagLength, String filter, String companyPrefixLength, String tag, TDTEngine tdt) {		
		LevelTypeList outputLevel = LevelTypeList.LEGACY;
		Map<String, String> extraparms = createExtraParams(tagLength, filter, companyPrefixLength);		
		return convert(tag, extraparms, outputLevel, tdt);
	}
	
	/**
	 * converts a given tag through TDT into PURE_IDENTITY format - used the TagHelpers internal TDT for the transformation.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_PURE_IDENTITY(String tagLength, String filter, String companyPrefixLength, String tag) {
		return convert_to_PURE_IDENTITY(tagLength, filter, companyPrefixLength, tag, getTDTEngine());		
	}
	
	/**
	 * converts a given tag through tdt into PURE_IDENTITY format.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @param tdt the TDT to use for the transformation.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_PURE_IDENTITY(String tagLength, String filter, String companyPrefixLength, String tag, TDTEngine tdt) {		
		LevelTypeList outputLevel = LevelTypeList.PURE_IDENTITY;
		Map<String, String> extraparms = createExtraParams(tagLength, filter, companyPrefixLength);				
		return convert(tag, extraparms, outputLevel, tdt);		
	}

	/**
	 * convenience method to convert the given tag to TAG_ENCODING format - the TagHelpers internal TDT is used. 
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_TAG_ENCODING(String tagLength, String filter, String companyPrefixLength, String tag) {
		return convert_to_TAG_ENCODING(tagLength, filter, companyPrefixLength, tag, getTDTEngine());
	}
	
	/**
	 * convert the given tag to TAG_ENCODING format.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @param tag the tag to convert in binary format or in TAG_ENCODING.
	 * @param tdt the TDT to use for the transformation.
	 * @return a converted tag or null if exception during conversion.
	 */
	public static String convert_to_TAG_ENCODING(String tagLength, String filter, String companyPrefixLength, String tag, TDTEngine tdt) {		
		LevelTypeList outputLevel = LevelTypeList.TAG_ENCODING;
		Map<String, String> extraparms = createExtraParams(tagLength, filter, companyPrefixLength);		
		return convert(tag, extraparms, outputLevel, tdt);
	}
	
	/**
	 * add the given parameters to the TDT extra parameters map.
	 * @param tagLength the inbound taglength must be specified as "64" or "96".
	 * @param filter the inbound filter value must be specified - range depends on coding scheme.
	 * @param companyPrefixLength length of the EAN.UCC Company Prefix must be specified for GS1 coding schemes. if set to null parameter is ignored.
	 * @return map wrapping the parameters.
	 */
	public static Map<String, String> createExtraParams(String tagLength, String filter, String companyPrefixLength) {
		Map<String, String> extraparms = new HashMap<String, String> ();		
		if (null != tagLength) {
			extraparms.put(EXTRA_PARAMS_TAGLENGTH, tagLength);
		}
		if (null != filter) {
			extraparms.put(EXTRA_PARAMS_FILTER, filter);
		}
		if (null != companyPrefixLength) {
			extraparms.put(EXTRA_PARAMS_COMPANYPREFIXLENGTH, companyPrefixLength);
		}
		return extraparms;
	}
}
