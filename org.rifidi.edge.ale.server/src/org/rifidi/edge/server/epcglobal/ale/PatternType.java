/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;

public enum PatternType {

	//ORANGE: add tagFormat EPC for Filtering : sgtin-96, sscc-96, grai-96
	
	GID_96,
	SGTIN_64,
	SSCC_64,
	SGTIN_96,
	SSCC_96,
	GRAI_96;	

	
	private static final String GID_96_STRING = "gid-96";
	private static final String SGTIN_64_STRING = "sgtin-64";
	private static final String SSCC_64_STRING = "sscc-64";
	private static final String SGTIN_96_STRING = "sgtin-96";
	private static final String SSCC_96_STRING = "sscc-96";
	private static final String GRAI_96_STRING = "grai-96";
	
	private static final int GID_96_DATAFIELDS = 3;
	private static final int SGTIN_64_DATAFIELDS = 4;
	private static final int SSCC_64_DATAFIELDS = 3;
	private static final int SGTIN_96_DATAFIELDS = 4;
	private static final int SSCC_96_DATAFIELDS = 3;
	private static final int GRAI_96_DATAFIELDS = 3;
	
	
	/**
	 * This method returns the pattern type of a types string represenation.
	 * 
	 * @param type string representation of the type
	 * @return pattern type
	 * @throws ECSpecValidationException if the string representation is invalid
	 */
	public static PatternType getType(String type) throws ECSpecValidationExceptionResponse {

		if (GID_96_STRING.equals(type)) {
			return GID_96;
		} else if (SGTIN_64_STRING.equals(type)) {
			return SGTIN_64;
		} else if (SSCC_64_STRING.equals(type)) {
			return SSCC_64;	
		}else if (SGTIN_96_STRING.equals(type)){
			return SGTIN_96;
		}else if (SSCC_96_STRING.equals(type)){
			return SSCC_96;
		}else if (GRAI_96_STRING.equals(type)){
			return GRAI_96;
		}else {
			throw new ECSpecValidationExceptionResponse("Unknown Tag Format '" + type + "'. Known formats are" +
					" '" + GID_96_STRING + "', '" + SGTIN_64_STRING + "', '" + SGTIN_96_STRING + "' and '" + SSCC_64_STRING + "', '" + SSCC_96_STRING + "', '" + GRAI_96_STRING + "'.");
		}
		
	}
	
	/**
	 * This method returns the number of data fields the pattern type has.
	 * 
	 * @return number of data fields
	 */
	public int getNumberOfDatafields() {
		
		if (this == GID_96) {
			return GID_96_DATAFIELDS;
		} else if (this == SGTIN_64) {
			return SGTIN_64_DATAFIELDS;
		} else if (this == SSCC_64) {
			return SSCC_64_DATAFIELDS;
		} else if (this == SGTIN_96) {
			return SGTIN_96_DATAFIELDS;
		}else if (this == SSCC_96) {
			return SSCC_96_DATAFIELDS;
		}else if (this == GRAI_96) {
			return GRAI_96_DATAFIELDS;
		}else {
			return -1;
		}
		
	}
	
	/**
	 * This method returns a string representation of the pattern type.
	 * 
	 * @return string representation of the pattern type
	 */
	public String toSring() {
		
		if (this == GID_96) {
			return GID_96_STRING;
		} else if (this == SGTIN_64) {
			return SGTIN_64_STRING;
		} else if (this == SSCC_64) {
			return SSCC_64_STRING;
		} else if (this == SGTIN_96) {
			return SGTIN_96_STRING;
		} else if (this == SSCC_96) {
			return SSCC_96_STRING;
		} else if (this == GRAI_96) {
			return GRAI_96_STRING;
		} else {
			return null;
		}
		
	}
	
}
