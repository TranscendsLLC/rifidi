/*
 * 
 * ALEField.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.read.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rifidi.edge.ale.api.read.data.ECFieldSpec;
import org.rifidi.edge.ale.read.ALEDataFormats;
import org.rifidi.edge.ale.read.ALEDataTypes;
import org.rifidi.edge.ale.read.ALEFields;

/**
 * This matcher extracts field information from a pattern. All ALE fields and encodings 
 * specified in ALE 1.1 chapter 6 are supported.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ALEField {

	private static Pattern pattern_abs = Pattern
			.compile("@(\\d*)\\.(\\d*)(\\.\\d*)?$");
	private static Pattern pattern_var = Pattern.compile("@\\d*\\.[a-zA-Z]$");
	private ALEFields name = null;
	private ALEDataFormats format = ALEDataFormats.DEFAULT;
	private ALEDataTypes type = ALEDataTypes.DEFAULT;
	private Integer bankId = 0;
	private Integer offset = 0;
	private Integer length = 0;
	/** The spec this object was created from. */
	private ECFieldSpec fieldSpec;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 */
	public ALEField(ECFieldSpec spec) {
		fieldSpec = spec;
		Matcher absMatcher = pattern_abs.matcher(spec.getFieldname());
		if ("epc".equals(spec.getFieldname())) {
			this.name = ALEFields.EPC;
		} else if ("killPwd".equals(spec.getFieldname())) {
			this.name = ALEFields.KILLPWD;
		} else if ("accessPwd".equals(spec.getFieldname())) {
			this.name = ALEFields.ACCESSPWD;
		} else if ("epcBank".equals(spec.getFieldname())) {
			this.name = ALEFields.EPCBANK;
		} else if ("tidBank".equals(spec.getFieldname())) {
			this.name = ALEFields.TIDBANK;
		} else if ("userBank".equals(spec.getFieldname())) {
			this.name = ALEFields.USERBANK;
		} else if ("afi".equals(spec.getFieldname())) {
			this.name = ALEFields.AFI;
		} else if (absMatcher.find()) {
			this.name = ALEFields.ABSOLUTEADDRESS;
			bankId = Integer.parseInt(absMatcher.group(1));
			offset = Integer.parseInt(absMatcher.group(2));
			if (absMatcher.groupCount() == 4) {
				length = Integer.parseInt(absMatcher.group(3).substring(1));
			}
		} else if (pattern_var.matcher(spec.getFieldname()).find()) {
			this.name = ALEFields.VARIABLEFIELD;
		}

		if (spec.getDatatype() == null || spec.getDatatype() == "") {
			type = ALEDataTypes.DEFAULT;
		} else if ("bits".equals(spec.getDatatype())) {
			type = ALEDataTypes.BITS;
		} else if ("epc".equals(spec.getDatatype())) {
			type = ALEDataTypes.EPC;
		} else if ("iso-15962-string".equals(spec.getDatatype())) {
			type = ALEDataTypes.ISO_15962_STRING;
		} else if ("uint".equals(spec.getDatatype())) {
			type = ALEDataTypes.UINT;
		}

		if (spec.getFormat() == null || spec.getFormat() == "") {
			format = ALEDataFormats.DEFAULT;
		} else if ("decimal".equals(spec.getFormat())) {
			format = ALEDataFormats.DECIMAL;
		} else if ("hex".equals(spec.getFormat())) {
			format = ALEDataFormats.HEX;
		} else if ("epc-pure".equals(spec.getFormat())) {
			format = ALEDataFormats.EPC_PURE;
		} else if ("epc-tag".equals(spec.getFormat())) {
			format = ALEDataFormats.EPC_TAG;
		} else if ("epc-decimal".equals(spec.getFormat())) {
			format = ALEDataFormats.EPC_DECIMAL;
		} else if ("epc-hex".equals(spec.getFormat())) {
			format = ALEDataFormats.EPC_HEX;
		} else if ("string".equals(spec.getFormat())) {
			format = ALEDataFormats.STRING;
		}

	}

	/**
	 * @return the fieldSpec
	 */
	public ECFieldSpec getFieldSpec() {
		return fieldSpec;
	}

	/**
	 * @return the name
	 */
	public ALEFields getName() {
		return name;
	}

	/**
	 * @return the format
	 */
	public ALEDataFormats getFormat() {
		return format;
	}

	/**
	 * @return the type
	 */
	public ALEDataTypes getType() {
		return type;
	}

	/**
	 * @return the bankId
	 */
	public Integer getBankId() {
		return bankId;
	}

	/**
	 * @return the offset
	 */
	public Integer getOffset() {
		return offset;
	}

	/**
	 * @return the length
	 */
	public Integer getLength() {
		return length;
	}

}
