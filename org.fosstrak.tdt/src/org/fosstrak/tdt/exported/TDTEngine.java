/*
 * Copyright (C) 2007 University of Cambridge
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.tdt.exported;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.fosstrak.tdt.Entry;
import org.fosstrak.tdt.EpcTagDataTranslation;
import org.fosstrak.tdt.Field;
import org.fosstrak.tdt.GEPC64Table;
import org.fosstrak.tdt.Level;
import org.fosstrak.tdt.Option;
import org.fosstrak.tdt.PrefixTree;
import org.fosstrak.tdt.Rule;
import org.fosstrak.tdt.Scheme;
import org.fosstrak.tdt.fileservice.FileService;
import org.fosstrak.tdt.types.CompactionMethodList;
import org.fosstrak.tdt.types.ModeList;
import org.fosstrak.tdt.types.PadDirectionList;
import org.fosstrak.tdt.types.TagLengthList;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * <p>
 * This class provides methods for translating an electronic product code (EPC)
 * between various levels of representation including BINARY, TAG_ENCODING,
 * PURE_IDENTITY and LEGACY formats. An additional output level ONS_HOSTNAME may
 * be defined for some coding schemes. When the class TDTEngine is constructed,
 * the path to a local directory must be specified, by passing it as a single
 * string parameter to the constructor method (without any trailing slash or
 * file separator). e.g.
 * 
 * <pre>
 * &lt;code&gt;TDTEngine engine = new TDTEngine(&quot;/opt/TDT&quot;);&lt;/code&gt;
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * The specified directory must contain two subdirectories named auxiliary and
 * schemes. The Tag Data Translation definition files for the various coding
 * schemes should be located inside the subdirectory called <code>schemes</code>
 * . Any auxiliary lookup files (such as <code>ManagerTranslation.xml</code>)
 * should be located inside the subdirectory called <code>auxiliary</code>.
 * </p>
 * 
 * @version $Revision: 1.9 $ $Date: 2006/01/27 13:42:32 $
 */
public class TDTEngine {

	private Log logger = LogFactory.getLog(TDTEngine.class);
	
	// --------------------------/
	// - Class/Member Variables -/
	// --------------------------/

	/**
	 * prefix_tree_map is a map of levels to prefix trees. Each prefix tree is a
	 * Trie structure (see wikipedia) that is useful for quickly finding a
	 * matching prefix.
	 */
	private Map<LevelTypeList, PrefixTree<PrefixMatch>> prefix_tree_map = new HashMap<LevelTypeList, PrefixTree<PrefixMatch>>();

	/**
	 * HashMap gs1cpi is an associative array providing a lookup between either
	 * a GS1 Company Prefix and the corresponding integer-based Company Prefix
	 * Index, where one of these has been registered for use with 64-bit EPCs -
	 * or the reverse lookup from Company Prefix Index to GS1 Company Prefix.
	 * Note that this is an optimization to avoid having to do an xpath trawl
	 * through the CPI table each time.
	 */
	private HashMap<String, String> gs1cpi = new HashMap<String, String>();

	/**
	 * HashMap levelnumber provides a lookup between a string presentation of a
	 * level and the corresponding numeric level 0-4
	 */
	// private HashMap<String,Integer> levelnumber = new
	// HashMap<String,Integer>();
	private FileService fileService;

	private class XMLFilenameFilter implements FilenameFilter {
		private String SUFFIX = ".xml";

		public boolean accept(File dir, String name) {
			return (name.length() >= SUFFIX.length() && name.substring(
					name.length() - SUFFIX.length()).equals(SUFFIX));
		}
	}

	/**
	 * TDTEngine - constructor for a new Tag Data Translation engine
	 * 
	 * @param confdir
	 *            the string value of the path to a configuration directory
	 *            consisting of two subdirectories, <code>schemes</code> and
	 *            <code>auxiliary</code>.
	 * 
	 *            <p>
	 *            When the class TDTEngine is constructed, the path to a local
	 *            directory must be specified, by passing it as a single string
	 *            parameter to the constructor method (without any trailing
	 *            slash or file separator). e.g.
	 *            <code><pre>TDTEngine engine = new TDTEngine("/opt/TDT");</pre></code>
	 *            </p>
	 *            <p>
	 *            The specified directory must contain two subdirectories named
	 *            auxiliary and schemes. The Tag Data Translation definition
	 *            files for the various coding schemes should be located inside
	 *            the subdirectory called <code>schemes</code>. Any auxiliary
	 *            lookup files (such as <code>ManagerTranslation.xml</code>)
	 *            should be located inside the subdirectory called
	 *            <code>auxiliary</code>.
	 * 
	 *            Files within the schemes directory ending in <code>.xml</code>
	 *            are read in and unmarshalled using <a href =
	 *            "http://www.castor.org">Castor</a>.
	 */
	public TDTEngine() {
		ServiceRegistry.getInstance().service(this);
	}

	@Inject
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
		try {
			processFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processFiles() throws FileNotFoundException, MarshalException,
			ValidationException, TDTException {
		long t = System.currentTimeMillis();
		ArrayList<URL> files = null;

		files = fileService.getFiles(File.separator + "data" + File.separator + "schemes");

		if (files.size() == 0)
			throw new TDTException("Cannot find schemes in " + File.separator
					+ "data" + File.separator + "schemes");
		Unmarshaller unmar = new Unmarshaller();
		for (URL url : files) {
			try {
				InputStreamReader reader = new InputStreamReader(url
						.openStream());
				EpcTagDataTranslation tdt = (EpcTagDataTranslation) unmar
						.unmarshal(EpcTagDataTranslation.class, reader);

				initFromTDT(tdt);
			} catch (IOException e) {
				logger.debug("cannot open resource: " + url.getFile());
			}
		}

		// need to populate the hashmap gs1cpi from the ManagerTranslation.xml
		// table in auxiliary.
		try {
			GEPC64Table cpilookup = (GEPC64Table) Unmarshaller.unmarshal(
					GEPC64Table.class, new InputStreamReader(fileService
							.getFile(
									File.separator + "data" + File.separator
											+ "auxiliary" + File.separator
											+ "ManagerTranslation.xml")
							.openStream()));
			for (Enumeration e = cpilookup.enumerateEntry(); e
					.hasMoreElements();) {
				Entry entry = (Entry) e.nextElement();
				String comp = entry.getCompanyPrefix();
				String indx = Integer.toString(entry.getIndex());
				gs1cpi.put(indx, comp);
				gs1cpi.put(comp, indx);
			}
		} catch (IOException e) {
			throw new TDTException(
					"Problem when unmarshalling ManagerTranslation.xml ");
		}

	}

	// -----------/
	// - Methods -/
	// -----------/

	private class PrefixMatch {
		private Scheme s;
		private Level level;

		public PrefixMatch(Scheme s, Level level) {
			this.s = s;
			this.level = level;
		}

		public Scheme getScheme() {
			return s;
		}

		public Level getLevel() {
			return level;
		}
	}

	/** initialise various indices */
	private void initFromTDT(EpcTagDataTranslation tdt) {
		Scheme[] scheme = tdt.getScheme();
		for (Scheme ss : scheme) {

			// create an index so that we can find a scheme based on tag length

			for (Level level : ss.getLevel()) {
				String s = level.getPrefixMatch();
				if (s != null) {
					// insert into prefix tree according to level type.
					PrefixTree<PrefixMatch> prefix_tree = prefix_tree_map
							.get(level.getType());
					if (prefix_tree == null) {
						prefix_tree = new PrefixTree<PrefixMatch>();
						prefix_tree_map.put(level.getType(), prefix_tree);
					}
					prefix_tree.insert(s, new PrefixMatch(ss, level));
				}
			}

		}
	}

	/**
	 * Given an input string, and optionally a tag length, find a scheme / level
	 * with a matching prefix and tag length.
	 */
	private PrefixMatch findPrefixMatch(String input, TagLengthList tagLength) {
		List<PrefixMatch> match_list = new ArrayList<PrefixMatch>();

		for (PrefixTree<PrefixMatch> tree : prefix_tree_map.values()) {

			List<PrefixMatch> list = tree.search(input);
			if (!list.isEmpty()) {
				if (tagLength == null)
					match_list.addAll(list);
				else {
					for (PrefixMatch match : list)
						if (match.getScheme().getTagLength() == tagLength)
							match_list.add(match);
				}
			}
		}

		if (match_list.isEmpty())
			throw new TDTException(
					"No schemes or levels matched the input value");
		else if (match_list.size() > 1)
			throw new TDTException(
					"More than one scheme/level matched the input value");
		else
			return match_list.get(0);
	}

	/**
	 * Given an input string, level, and optionally a tag length, find a
	 * matching prefix.
	 */
	private PrefixMatch findPrefixMatch(String input, TagLengthList tagLength,
			LevelTypeList level_type) {
		List<PrefixMatch> match_list = new ArrayList<PrefixMatch>();
		PrefixTree<PrefixMatch> tree = prefix_tree_map.get(level_type);
		assert tree != null;
		List<PrefixMatch> list = tree.search(input);
		if (!list.isEmpty()) {
			if (tagLength == null)
				match_list.addAll(list);
			else {
				for (PrefixMatch match : list)
					if (match.getScheme().getTagLength() == tagLength)
						match_list.add(match);
			}
		}
		if (match_list.isEmpty())
			throw new TDTException(
					"No schemes or levels matched the input value");
		else if (match_list.size() > 1)
			throw new TDTException(
					"More than one scheme/level matched the input value");
		else
			return match_list.get(0);
	}

	/**
	 * Translates the input string of a specified input level to a specified
	 * outbound level of the same coding scheme. For example, the input string
	 * value may be a tag-encoding URI and the outbound level specified by
	 * string outboundlevel may be BINARY, in which case the return value is a
	 * binary representation expressed as a string.
	 * 
	 * <p>
	 * Note that this version of the method requires that the user specify the
	 * input level, rather than searching for it. However it still automatically
	 * finds the scheme used.
	 * </p>
	 * 
	 * @param input
	 *            input tag coding
	 * @param inputLevel
	 *            level such as BINARY, or TAG_ENCODING.
	 * @param tagLength
	 *            tag length such as VALUE_64 or VALUE_96.
	 * @param inputParameters
	 *            a map with any additional properties.
	 * @param outputLevel
	 *            required output level.
	 * @return output tag coding
	 */
	public String convert(String input, LevelTypeList inputLevel,
			TagLengthList tagLength, Map<String, String> inputParameters,
			LevelTypeList outputLevel) {

		PrefixMatch match = findPrefixMatch(input, tagLength, inputLevel);

		return convertLevel(match.getScheme(), match.getLevel(), input,
				inputParameters, outputLevel);
	}

	/**
	 * The convert method translates a String input to a specified outbound
	 * level of the same coding scheme. For example, the input string value may
	 * be a tag-encoding URI and the outbound level specified by string
	 * outboundlevel may be BINARY, in which case the return value is a binary
	 * representation expressed as a string.
	 * 
	 * @param input
	 *            the identifier to be converted.
	 * @param inputParameters
	 *            additional parameters which need to be provided because they
	 *            cannot always be determined from the input value alone.
	 *            Examples include the taglength, companyprefixlength and filter
	 *            values.
	 * @param outputLevel
	 *            the outbound level required for the ouput. Permitted values
	 *            include BINARY, TAG_ENCODING, PURE_IDENTITY, LEGACY and
	 *            ONS_HOSTNAME.
	 * @return the identifier converted to the output level.
	 */
	public String convert(String input, Map<String, String> inputParameters,
			LevelTypeList outputLevel) {

		TagLengthList tagLength = null;
		if (inputParameters.containsKey("taglength")) {
			// in principle, the user should provide a
			// TagLengthList object in the parameter list.
			String s = inputParameters.get("taglength");
			tagLength = TagLengthList.valueOf(s);
		}

		PrefixMatch match = findPrefixMatch(input, tagLength);

		return convertLevel(match.getScheme(), match.getLevel(), input,
				inputParameters, outputLevel);
	}

	/**
	 * convert from a particular scheme / level
	 */
	private String convertLevel(Scheme tdtscheme, Level tdtlevel, String input,
			Map<String, String> inputParameters, LevelTypeList outboundlevel) {

		String outboundstring;
		Map<String, String> extraparams =
		// new NoisyMap
		(new HashMap<String, String>(inputParameters));

		// get the scheme's option key, which is the name of a
		// parameter whose value is matched to the option key of the
		// level.

		String optionkey = tdtscheme.getOptionKey();
		String optionValue = extraparams.get(optionkey);
		// the name of a parameter which allows the appropriate option
		// to be selected

		// now consider the various options within the scheme and
		// level for each option element inside the level, check
		// whether the pattern attribute matches as a regular
		// expression

		String matchingOptionKey = null;
		Option matchingOption = null;
		Matcher prefixMatcher = null;
		for (Enumeration e = tdtlevel.enumerateOption(); e.hasMoreElements();) {
			Option opt = (Option) e.nextElement();
			if (optionValue == null || optionValue.equals(opt.getOptionKey())) {
				// possible match

				Matcher matcher = Pattern.compile(opt.getPattern()).matcher(
						input);
				if (matcher.matches()) {
					if (prefixMatcher != null)
						throw new TDTException("Multiple patterns matched");
					prefixMatcher = matcher;
					matchingOptionKey = opt.getOptionKey();
					matchingOption = opt;
				}
			}
		}
		if (prefixMatcher == null)
			throw new TDTException("No patterns matched");

		optionValue = matchingOptionKey;

		for (Enumeration e = matchingOption.enumerateField(); e
				.hasMoreElements();) {
			Field field = (Field) e.nextElement();
			int seq = field.getSeq();

			String strfieldname = field.getName();
			int fieldlength = field.getLength();
			String strfieldvalue = prefixMatcher.group(seq);
			// System.out.println("   processing field " + strfieldname + " = '"
			// + strfieldvalue + "'");

			if (field.getCompaction() == null) {
				// if compaction is null, treat field as an integer

				if (field.getCharacterSet() != null) { // if the character set
					// is specified
					Matcher charsetmatcher = Pattern.compile(
							"^" + field.getCharacterSet() + "$").matcher(
							strfieldvalue);
					if (!charsetmatcher.matches()) {
						throw new TDTException(
								"field "
										+ strfieldname
										+ " ("
										+ strfieldvalue
										+ ") does not conform to the allowed character set ("
										+ field.getCharacterSet() + ") ");
					}
				}

				BigInteger bigvalue = null;

				if (tdtlevel.getType() == LevelTypeList.BINARY) { // if the
					// input was
					// BINARY
					bigvalue = new BigInteger(strfieldvalue, 2);
					extraparams.put(strfieldname, bigvalue.toString());
				} else {
					if (field.getDecimalMinimum() != null
							|| field.getDecimalMaximum() != null)
						bigvalue = new BigInteger(strfieldvalue);
					extraparams.put(strfieldname, strfieldvalue);
				}

				if (field.getDecimalMinimum() != null) { // if the decimal
					// minimum is
					// specified
					BigInteger bigmin = new BigInteger(field
							.getDecimalMinimum());

					if (bigvalue.compareTo(bigmin) == -1) { // throw an
						// exception if the
						// field value is
						// less than the
						// decimal minimum
						throw new TDTException("field " + strfieldname + " ("
								+ bigvalue + ") is less than DecimalMinimum ("
								+ field.getDecimalMinimum() + ") allowed");
					}
				}

				if (field.getDecimalMaximum() != null) { // if the decimal
					// maximum is
					// specified
					BigInteger bigmax = new BigInteger(field
							.getDecimalMaximum());

					if (bigvalue.compareTo(bigmax) == 1) { // throw an excpetion
						// if the field
						// value is greater
						// than the decimal
						// maximum
						throw new TDTException("field " + strfieldname + " ("
								+ bigvalue
								+ ") is greater than DecimalMaximum ("
								+ field.getDecimalMaximum() + ") allowed");
					}
				}

				// after extracting the field, it may be necessary to pad it.

				padField(extraparams, field);

			} else {
				// compaction is specified - interpret binary as a string value
				// using a truncated byte per character

				CompactionMethodList compaction = field.getCompaction();
				PadDirectionList padDir = field.getPadDir();
				String padchar = field.getPadChar();
				String s;
				if (compaction == CompactionMethodList.VALUE_5)
					// "5-bit"
					s = bin2uppercasefive(strfieldvalue);
				else if (compaction == CompactionMethodList.VALUE_4)
					// 6-bit
					s = bin2alphanumsix(strfieldvalue);
				else if (compaction == CompactionMethodList.VALUE_3)
					// 7-bit
					s = bin2asciiseven(strfieldvalue);
				else if (compaction == CompactionMethodList.VALUE_2)
					// 8-bit
					s = bin2bytestring(strfieldvalue);
				else
					throw new Error("unsupported compaction method "
							+ compaction);
				extraparams.put(strfieldname, stripPadChar(s, padDir, padchar));

			}

		} // for each field;

		/**
		 * the EXTRACT rules are performed after parsing the input, in order to
		 * determine additional fields that are to be derived from the fields
		 * obtained by the pattern match process
		 */

		int seq = 0;
		for (Enumeration e = tdtlevel.enumerateRule(); e.hasMoreElements();) {
			Rule tdtrule = (Rule) e.nextElement();
			if (tdtrule.getType() == ModeList.EXTRACT) {
				assert seq < tdtrule.getSeq() : "Rule out of sequence order";
				seq = tdtrule.getSeq();
				processRules(extraparams, tdtrule);
			}
		}

		/**
		 * Now we need to consider the corresponding output level and output
		 * option. The scheme must remain the same, as must the value of
		 * optionkey (to select the corresponding option element nested within
		 * the required outbound level)
		 */

		Level tdtoutlevel = findLevel(tdtscheme, outboundlevel);
		Option tdtoutoption = findOption(tdtoutlevel, optionValue);

		/**
		 * the FORMAT rules are performed before formatting the output, in order
		 * to determine additional fields that are required for preparation of
		 * the outbound format
		 */

		seq = 0;
		for (Enumeration e = tdtoutlevel.enumerateRule(); e.hasMoreElements();) {
			Rule tdtrule = (Rule) e.nextElement();
			if (tdtrule.getType() == ModeList.FORMAT) {
				assert seq < tdtrule.getSeq() : "Rule out of sequence order";
				seq = tdtrule.getSeq();
				processRules(extraparams, tdtrule);
			}
		}

		/**
		 * Now we need to ensure that all fields required for the outbound
		 * grammar are suitably padded etc. processPadding takes care of firstly
		 * padding the non-binary fields if padChar and padDir, length are
		 * specified then (if necessary) converting to binary and padding the
		 * binary representation to the left with zeros if the bit string is has
		 * fewer bits than the bitLength attribute specifies. N.B. TDTv1.1 will
		 * be more specific about bit-level padding rather than assuming that it
		 * is always to the left with the zero bit.
		 */

		// System.out.println(" prior to processPadding, " + extraparams);
		for (Enumeration e = tdtoutoption.enumerateField(); e.hasMoreElements();) {
			Field field = (Field) e.nextElement();
			// processPadding(extraparams, field, outboundlevel, tdtoutoption);

			padField(extraparams, field);
			if (outboundlevel == LevelTypeList.BINARY)
				binaryPadding(extraparams, field);

		}

		/**
		 * Construct the output from the specified grammar (in ABNF format)
		 * together with the field values stored in inputparams
		 */

		outboundstring = buildGrammar(tdtoutoption.getGrammar(), extraparams);

		// System.out.println("final extraparams = " + extraparams);
		// System.out.println("returned " + outboundstring);
		return outboundstring;
	}

	/**
	 * 
	 * Converts a binary string into a large integer (numeric string)
	 */
	private String bin2dec(String binary) {
		BigInteger dec = new BigInteger(binary, 2);
		return dec.toString();
	}

	/**
	 * 
	 * Converts a large integer (numeric string) to a binary string
	 */
	private String dec2bin(String decimal) {
		BigInteger bin = new BigInteger(decimal);
		return bin.toString(2);
	}

	/**
	 * 
	 * Converts a hexadecimal string to a binary string
	 */
	private String hex2bin(String hex) {
		BigInteger bin = new BigInteger(hex.toLowerCase(), 16);
		return bin.toString(2);
	}

	/**
	 * 
	 * Converts a binary string to a hexadecimal string
	 */
	private String bin2hex(String binary) {
		BigInteger hex = new BigInteger(binary, 2);
		return hex.toString(16).toUpperCase();
	}

	/**
	 * Returns a string built using a particular grammar. Single-quotes strings
	 * are counted as literal strings, whereas all other strings appearing in
	 * the grammar require substitution with the corresponding value from the
	 * extraparams hashmap.
	 */
	private String buildGrammar(String grammar, Map<String, String> extraparams) {
		StringBuilder outboundstring = new StringBuilder();
		String[] fields = Pattern.compile("\\s+").split(grammar);
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].substring(0, 1).equals("'")) {
				outboundstring.append(fields[i].substring(1,
						fields[i].length() - 1));
			} else {
				outboundstring.append(extraparams.get(fields[i]));
			}
		}

		return outboundstring.toString();
	}

	/**
	 * 
	 * Converts the value of a specified fieldname from the extraparams map into
	 * binary, either handling it as a large integer or taking into account the
	 * compaction of each ASCII byte that is specified in the TDT definition
	 * file for that particular field
	 */
	private String fieldToBinary(Field field, Map<String, String> extraparams) {
		// really need an index to find field number given fieldname;

		String fieldname = field.getName();
		String value = extraparams.get(fieldname);
		CompactionMethodList compaction = field.getCompaction();

		if (compaction == null) {
			value = dec2bin(value);
		} else {
			if (compaction == CompactionMethodList.VALUE_5) {
				value = uppercasefive2bin(value);
			} else if (compaction == CompactionMethodList.VALUE_4) {
				value = alphanumsix2bin(value);
			} else if (compaction == CompactionMethodList.VALUE_3) {
				value = asciiseven2bin(value);
			} else if (compaction == CompactionMethodList.VALUE_2) {
				value = bytestring2bin(value);
			} else
				throw new Error("Unsupported compaction " + compaction);
		}

		return value;
	}

	/**
	 * pad a value according the field definition.
	 */
	private void padField(Map<String, String> extraparams, Field field) {
		String name = field.getName();
		String value = extraparams.get(name);
		PadDirectionList padDir = field.getPadDir();
		int requiredLength = field.getLength();

		// assert value != null;
		if (value == null)
			return;

		String padCharString = field.getPadChar();
		// if no pad char specified, don't attempt padding
		if (padCharString == null)
			return;
		assert padCharString.length() > 0;
		char padChar = padCharString.charAt(0);

		StringBuilder buf = new StringBuilder(requiredLength);
		if (padDir == PadDirectionList.LEFT) {
			for (int i = 0; i < requiredLength - value.length(); i++)
				buf.append(padChar);
			buf.append(value);
		} else if (padDir == PadDirectionList.RIGHT) {
			buf.append(value);
			for (int i = 0; i < requiredLength - value.length(); i++)
				buf.append(padChar);
		}
		assert buf.length() == requiredLength;
		if (requiredLength != value.length()) {
			// System.out.println("    updated " + name + " to '" + buf + "'");
			extraparams.put(name, buf.toString());
		}
		/*
		 * else { StringBuilder mybuf = new StringBuilder(); for (int i = 0; i <
		 * value.length(); i++) { if (i > 0) mybuf.append(',');
		 * mybuf.append('\''); mybuf.append(value.charAt(i));
		 * mybuf.append('\''); }
		 * 
		 * 
		 * System.out.println("    field " + name + " not padded as " +
		 * mybuf.toString() + " is already " + requiredLength +
		 * " characters long"); }
		 */
	}

	/**
	 * If the outbound level is BINARY, convert the string field to binary, then
	 * pad to the left with the appropriate number of zero bits to reach a
	 * number of bits specified by the bitLength attribute of the TDT definition
	 * file.
	 */

	private void binaryPadding(Map<String, String> extraparams, Field tdtfield) {
		String fieldname = tdtfield.getName();
		int reqbitlength = tdtfield.getBitLength();
		String value;

		String binaryValue = fieldToBinary(tdtfield, extraparams);
		if (binaryValue.length() < reqbitlength) {
			int extraBitLength = reqbitlength - binaryValue.length();

			StringBuilder zeroPaddedBinaryValue = new StringBuilder("");
			for (int i = 0; i < extraBitLength; i++) {
				zeroPaddedBinaryValue.append("0");
			}
			zeroPaddedBinaryValue.append(binaryValue);
			value = zeroPaddedBinaryValue.toString();
		} else {
			if (binaryValue.length() > reqbitlength)
				throw new TDTException("Binary value [" + binaryValue
						+ "] for field " + fieldname
						+ " exceeds maximum allowed " + reqbitlength
						+ " bits.  Decimal value was "
						+ extraparams.get(fieldname));

			value = binaryValue;
		}
		extraparams.put(fieldname, value);

	}

	/**
	 * Removes leading or trailing characters equal to padchar from the
	 * start/end of the string specified as the first parameter. The second
	 * parameter specified the stripping direction as "LEFT" or "RIGHT" and the
	 * third parameter specifies the character to be stripped.
	 */
	private String stripPadChar(String field, PadDirectionList paddir,
			String padchar) {
		String rv;
		if (paddir == null || padchar == null)
			rv = field;
		else {
			String pattern;
			if (paddir == PadDirectionList.RIGHT)
				pattern = "[" + padchar + "]+$";
			else
				// if (paddir == PadDirectionList.LEFT)
				pattern = "^[" + padchar + "]+";

			rv = field.replaceAll(pattern, "");

		}
		return rv;
	}

	/**
	 * 
	 * Adds additional entries to the extraparams hashmap by processing various
	 * rules defined in the TDT definition files. Typically used for string
	 * processing functions, lookup in tables, calculation of check digits etc.
	 */
	private void processRules(Map<String, String> extraparams, Rule tdtrule) {
		String tdtfunction = tdtrule.getFunction();
		int openbracket = tdtfunction.indexOf("(");
		assert openbracket != -1;
		String params = tdtfunction.substring(openbracket + 1, tdtfunction
				.length() - 1);
		String rulename = tdtfunction.substring(0, openbracket);
		String[] parameter = params.split(",");
		String newfieldname = tdtrule.getNewFieldName();
		// System.out.println(tdtfunction + " " + parameter[0] + " " +
		// extraparams.get(parameter[0]));
		/**
		 * Stores in the hashmap extraparams the value obtained from a lookup in
		 * a specified XML table.
		 * 
		 * The first parameter is the given value already known. This is denoted
		 * as $1 in the corresponding XPath expression
		 * 
		 * The second parameter is the string filename of the table which must
		 * be present in the auxiliary subdirectory
		 * 
		 * The third parameter is the column in which the supplied input value
		 * should be sought
		 * 
		 * The fourth parameter is the column whose value should be read for the
		 * corresponding row, in order to obtain the result of the lookup.
		 * 
		 * The rule in the definition file may contain an XPath expression and a
		 * URL where the table may be obtained.
		 */
		if (rulename.equals("TABLELOOKUP")) {
			// parameter[0] is given value
			// parameter[1] is table
			// parameter[2] is input column supplied
			// parameter[3] is output column required
			assert parameter.length == 4 : "incorrect number of parameters to tablelookup "
					+ params;
			if (parameter[1].equals("tdt64bitcpi")) {
				String s = extraparams.get(parameter[0]);
				assert s != null : tdtfunction + " when " + parameter[0]
						+ " is null";
				String t = gs1cpi.get(s);
				assert t != null : "gs1cpi[" + s + "] is null";
				assert newfieldname != null;
				extraparams.put(newfieldname, t);
				// extraparams.put(newfieldname,
				// gs1cpi.get(extraparams.get(parameter[0])));
			} else { // JPB! the following is untested
				String tdtxpath = tdtrule.getTableXPath();
				String tdttableurl = tdtrule.getTableURL();
				String tdtxpathsub = tdtxpath.replaceAll("\\$1", extraparams
						.get(parameter[0]));
				extraparams.put(newfieldname, xpathlookup(
						"ManagerTranslation.xml", tdtxpathsub));
			}
		}

		/**
		 * Stores the length of the specified string under the new fieldname
		 * specified by the corresponding rule of the definition file.
		 */
		if (rulename.equals("LENGTH")) {
			assert extraparams.get(parameter[0]) != null : tdtfunction
					+ " when " + parameter[0] + " is null";
			if (extraparams.get(parameter[0]) != null) {
				extraparams.put(newfieldname, Integer.toString(extraparams.get(
						parameter[0]).length()));
			}
		}

		/**
		 * Stores a GS1 check digit in the extraparams hashmap, keyed under the
		 * new fieldname specified by the corresponding rule of the definition
		 * file.
		 */
		if (rulename.equals("GS1CHECKSUM")) {
			assert extraparams.get(parameter[0]) != null : tdtfunction
					+ " when " + parameter[0] + " is null";
			if (extraparams.get(parameter[0]) != null) {
				extraparams.put(newfieldname, gs1checksum(extraparams
						.get(parameter[0])));
			}
		}

		/**
		 * Obtains a substring of the string provided as the first parameter. If
		 * only a single second parameter is specified, then this is considered
		 * as the start index and all characters from the start index onwards
		 * are stored in the extraparams hashmap under the key named
		 * 'newfieldname' in the corresponding rule of the definition file. If a
		 * second and third parameter are specified, then the second parameter
		 * is the start index and the third is the length of characters
		 * required. A substring consisting characters from the start index up
		 * to the required length of characters is stored in the extraparams
		 * hashmap, keyed under the new fieldname specified by the corresponding
		 * rule of the defintion file.
		 */
		if (rulename.equals("SUBSTR")) {
			assert extraparams.get(parameter[0]) != null : tdtfunction
					+ " when " + parameter[0] + " is null";
			if (parameter.length == 2) {
				if (extraparams.get(parameter[0]) != null) {
					int start = getIntValue(parameter[1], extraparams);
					if (start >= 0) {
						extraparams.put(newfieldname, extraparams.get(
								parameter[0]).substring(start));
					}
				}

			}
			if (parameter.length == 3) { // need to check that this variation is
				// correct - c.f. Perl substr
				assert extraparams.get(parameter[0]) != null : tdtfunction
						+ " when " + parameter[0] + " is null";
				if (extraparams.get(parameter[0]) != null) {
					int start = getIntValue(parameter[1], extraparams);
					int end = getIntValue(parameter[2], extraparams);
					if ((start >= 0) && (end >= 0)) {
						extraparams.put(newfieldname, extraparams.get(
								parameter[0]).substring(start, start + end));
					}
				}

			}
		}

		/**
		 * Concatenates specified string parameters together. Literal values
		 * must be enclosed within single or double quotes or consist of
		 * unquoted digits. Other unquoted strings are considered as fieldnames
		 * and the corresponding value from the extraparams hashmap are
		 * inserted. The result of the concatenation (and substitution) of the
		 * strings is stored as a new entry in the extraparams hashmap, keyed
		 * under the new fieldname specified by the rule.
		 */
		if (rulename.equals("CONCAT")) {
			StringBuilder buffer = new StringBuilder();
			for (int p1 = 0; p1 < parameter.length; p1++) {
				Matcher matcher = Pattern.compile("\"(.*?)\"|'(.*?)'|[0-9]")
						.matcher(parameter[p1]);
				if (matcher.matches()) {
					buffer.append(parameter[p1]);
				} else {
					assert extraparams.get(parameter[p1]) != null : tdtfunction
							+ " when " + parameter[p1] + " is null";
					if (extraparams.get(parameter[p1]) != null) {
						buffer.append(extraparams.get(parameter[p1]));
					}
				}

			}
			extraparams.put(newfieldname, buffer.toString());
		}
	}

	/**
	 * 
	 * Returns the value of a specified fieldname from the specified hashmap and
	 * returns an integer value or throws an exception if the value is not an
	 * integer
	 */
	private int getIntValue(String fieldname, Map<String, String> extraparams) {
		Matcher checkint = Pattern.compile("^\\d+$").matcher(fieldname);
		int rv;
		if (checkint.matches()) {
			rv = Integer.parseInt(fieldname);
		} else {
			if (extraparams.containsKey(fieldname)) {
				rv = Integer.parseInt(extraparams.get(fieldname));
			} else {
				rv = -1;
				throw new TDTException("No integer value for " + fieldname
						+ " can be found - check extraparams");
			}
		}
		return rv;
	}

	/**
	 * 
	 * Performs an XPATH lookup in an xml document whose filename is specified
	 * by the first parameter (assumed to be in the auxiliary subdirectory) The
	 * XPATH expression is supplied as the second string parameter The return
	 * value is of type string e.g. this is currently used primarily for looking
	 * up the Company Prefix Index for encoding a GS1 Company Prefix into a
	 * 64-bit EPC tag
	 */
	private String xpathlookup(String xml, String expression) {

		try {

			// Parse the XML as a W3C document.
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			String file =  File.separator + "data" + File.separator + "auxiliary"
					+ File.separator + xml;
			InputStream stream = fileService.getFile(file).openStream();
			Document document = builder.parse(stream);

			XPath xpath = XPathFactory.newInstance().newXPath();

			String rv = (String) xpath.evaluate(expression, document,
					XPathConstants.STRING);

			return rv;

		} catch (ParserConfigurationException e) {
			System.err.println("ParserConfigurationException caught...");
			e.printStackTrace();
			return null;
		} catch (XPathExpressionException e) {
			System.err.println("XPathExpressionException caught...");
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			System.err.println("SAXException caught...");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("IOException caught...");
			e.printStackTrace();
			return null;
		}

	}

	// auxiliary functions

	/**
	 * 
	 * Converts a binary string input into a byte string, using 8-bits per
	 * character byte
	 */
	private String bytestring2bin(String bytestring) {
		String binary;
		StringBuilder buffer = new StringBuilder("");
		int len = bytestring.length();
		byte[] bytes = bytestring.getBytes();
		for (int i = 0; i < len; i++) {
			buffer.append(padBinary(dec2bin(Integer.toString(bytes[i])), 8));
		}
		binary = buffer.toString();
		return binary;
	}

	/**
	 * 
	 * Converts a byte string input into a binary string, using 8-bits per
	 * character byte
	 */
	private String bin2bytestring(String binary) {
		String bytestring;
		StringBuilder buffer = new StringBuilder("");
		int len = binary.length();
		for (int i = 0; i < len; i += 8) {
			int j = Integer.parseInt(bin2dec(padBinary(binary.substring(i,
					i + 8), 8)));
			buffer.append((char) j);
		}
		bytestring = buffer.toString();
		return bytestring;
	}

	/**
	 * 
	 * Converts an ASCII string input into a binary string, using 7-bit
	 * compaction of each ASCII byte
	 */
	private String asciiseven2bin(String asciiseven) {
		String binary;
		StringBuilder buffer = new StringBuilder("");
		int len = asciiseven.length();
		byte[] bytes = asciiseven.getBytes();
		for (int i = 0; i < len; i++) {
			buffer.append(padBinary(dec2bin(Integer.toString(bytes[i] % 128)),
					8).substring(1, 8));
		}
		binary = buffer.toString();
		return binary;
	}

	/**
	 * 
	 * Converts a binary string input into an ASCII string output, assuming that
	 * 7-bit compaction was used
	 */
	private String bin2asciiseven(String binary) {
		String asciiseven;
		StringBuilder buffer = new StringBuilder("");
		int len = binary.length();
		for (int i = 0; i < len; i += 7) {
			int j = Integer.parseInt(bin2dec(padBinary(binary.substring(i,
					i + 7), 8)));
			buffer.append((char) j);
		}
		asciiseven = buffer.toString();
		return asciiseven;
	}

	/**
	 * Converts an alphanumeric string input into a binary string, using 6-bit
	 * compaction of each ASCII byte
	 */
	private String alphanumsix2bin(String alphanumsix) {
		String binary;
		StringBuilder buffer = new StringBuilder("");
		int len = alphanumsix.length();
		byte[] bytes = alphanumsix.getBytes();
		for (int i = 0; i < len; i++) {
			buffer
					.append(padBinary(dec2bin(Integer.toString(bytes[i] % 64)),
							8).substring(2, 8));
		}
		binary = buffer.toString();
		return binary;
	}

	/**
	 * 
	 * Converts a binary string input into a character string output, assuming
	 * that 6-bit compaction was used
	 */
	private String bin2alphanumsix(String binary) {
		String alphanumsix;
		StringBuilder buffer = new StringBuilder("");
		int len = binary.length();
		for (int i = 0; i < len; i += 6) {
			int j = Integer.parseInt(bin2dec(padBinary(binary.substring(i,
					i + 6), 8)));
			if (j < 32) {
				j += 64;
			}
			buffer.append((char) j);
		}
		alphanumsix = buffer.toString();
		return alphanumsix;
	}

	/**
	 * Converts an upper case character string input into a binary string, using
	 * 5-bit compaction of each ASCII byte
	 */
	private String uppercasefive2bin(String uppercasefive) {
		String binary;
		StringBuilder buffer = new StringBuilder("");
		int len = uppercasefive.length();
		byte[] bytes = uppercasefive.getBytes();
		for (int i = 0; i < len; i++) {
			buffer
					.append(padBinary(dec2bin(Integer.toString(bytes[i] % 32)),
							8).substring(3, 8));
		}
		binary = buffer.toString();
		return binary;
	}

	/**
	 * 
	 * Converts a binary string input into a character string output, assuming
	 * that 5-bit compaction was used
	 */
	private String bin2uppercasefive(String binary) {
		String uppercasefive;
		StringBuilder buffer = new StringBuilder("");
		int len = binary.length();
		for (int i = 0; i < len; i += 5) {
			int j = Integer.parseInt(bin2dec(padBinary(binary.substring(i,
					i + 5), 8)));
			buffer.append((char) (j + 64));
		}
		uppercasefive = buffer.toString();
		return uppercasefive;
	}

	/**
	 * Pads a binary value supplied as a string first parameter to the left with
	 * leading zeros in order to reach a required number of bits, as expressed
	 * by the second parameter, reqlen. Returns a string value corresponding to
	 * the binary value left padded to the required number of bits.
	 */
	private String padBinary(String binary, int reqlen) {
		String rv;
		int l = binary.length();
		int pad = (reqlen - (l % reqlen)) % reqlen;
		StringBuilder buffer = new StringBuilder("");
		for (int i = 0; i < pad; i++) {
			buffer.append("0");
		}
		buffer.append(binary);
		rv = buffer.toString();
		return rv;
	}

	/**
	 * Calculates the check digit for a supplied input string (assuming that the
	 * check digit will be the digit immediately following the supplied input
	 * string). GS1 (formerly EAN.UCC) check digit calculation methods are used.
	 */
	private String gs1checksum(String input) {
		int checksum;
		int weight;
		int total = 0;
		int len = input.length();
		int d;
		for (int i = 0; i < len; i++) {
			if (i % 2 == 0) {
				weight = -3;
			} else {
				weight = -1;
			}
			d = Integer.parseInt(input.substring(len - 1 - i, len - i));
			total += weight * d;
		}
		checksum = (10 + total % 10) % 10;
		return Integer.toString(checksum);
	}

	/**
	 * find a level by its type in a scheme. This involves iterating through the
	 * list of levels. The main reason for doing it this way is to avoid being
	 * dependent on the order in which the levels are coded in the xml, which is
	 * not explicitly constrained.
	 */
	private Level findLevel(Scheme scheme, LevelTypeList levelType) {
		Level level = null;
		for (Enumeration e = scheme.enumerateLevel(); e.hasMoreElements();) {
			Level lev = (Level) e.nextElement();
			if (lev.getType() == levelType) {
				level = lev;
				break;
			}
		}
		if (level == null)
			throw new Error("Couldn't find type " + levelType + " in scheme "
					+ scheme);
		return level;
	}

	/**
	 * find a option by its type in a scheme. This involves iterating through
	 * the list of options. The main reason for doing it this way is to avoid
	 * being dependent on the order in which the options are coded in the xml,
	 * which is not explicitly constrained.
	 */
	private Option findOption(Level level, String optionKey) {
		Option option = null;

		for (Enumeration e = level.enumerateOption(); e.hasMoreElements();) {
			Option opt = (Option) e.nextElement();
			if (opt.getOptionKey().equals(optionKey)) {
				option = opt;
				break;
			}
		}
		if (option == null)
			throw new Error("Couldn't find option for " + optionKey
					+ " in level " + level);

		return option;
	}
}
