/*
 * 12/12/2015
 * The original work can be found in
 * https://github.com/Fosstrak/fosstrak-fc/blob/master/fc-server/src/main/java/org/fosstrak/ale/server/
 * 
 */

package org.rifidi.edge.server.epcglobal.ale;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;

public class Pattern {

	/** content of the first field */
	private static final String FIRST_FIELD = "urn";
	/** content of the second field */
	private static final String SECOND_FIELD = "epc";
	/** possible contents of the third field */
	private static final String[] THIRD_FIELDS = new String[] {"urn", "tag", "pat", "id", "idpat", "raw"};
	
	/** how this pattern is used (tag, filter or group) */
	private final PatternUsage usage;
	/** conent of third field */
	private final String thirdField;
	/** type of this pattern */
	private final PatternType type;
	
	/** data fields of this pattern */
	private final List<PatternDataField> dataFields = new ArrayList<PatternDataField>();
	
	/**
	 * Contructor sets the usage and parses the pattern.
	 * 
	 * @param pattern to parse
	 * @param usage of this pattern
	 * @throws ECSpecValidationException if the pattern is invalid
	 */
	public Pattern(String pattern, PatternUsage usage) throws ECSpecValidationExceptionResponse {

		// set fields
		this.usage = usage;
		
		StringBuffer thirdFieldString = new StringBuffer();
		thirdFieldString.append("(");
		for (int i = 0; i < THIRD_FIELDS.length; i++) {
			if (i > 0) thirdFieldString.append(" | ");
			thirdFieldString.append(THIRD_FIELDS[i]);
		}
		thirdFieldString.append(")");
		
		// split pattern and check first fields
		String[] parts = pattern.split(":");
		if (parts.length != 5) {
			throw new ECSpecValidationExceptionResponse("Invalid Pattern '" + pattern + "'." +
					" Pattern must have the form '" + FIRST_FIELD + ":" + SECOND_FIELD + ":" + thirdFieldString + ":tag-type:data-fields'.");
		} else {
			thirdField = parts[2];
			if (!FIRST_FIELD.equals(parts[0]) || !SECOND_FIELD.equals(parts[1]) || !containsString(THIRD_FIELDS, thirdField)) {
				throw new ECSpecValidationExceptionResponse("Invalid Pattern '" + pattern + "'." +
						" Pattern must start with '" + FIRST_FIELD + ":" + SECOND_FIELD + ":" + thirdFieldString + "'.");
			} else {
				
				// get pattern type
				type = PatternType.getType(parts[3]);
				
				// parse data fields
				parseDataFields(parts[4], pattern);
				
			}
		}
		
	}

	/**
	 * This method returns the type of this pattern.
	 * 
	 * @return type of pattern
	 */
	public PatternType getType() {
		
		return type;
		
	}
	
	/**
	 * This method returns the filter of this pattern.
	 * 
	 * @return filter of pattern
	 */
	public PatternDataField getFilter() {
		
		return dataFields.get(0);
		
	}
	
	/**
	 * This method returns the company of this pattern.
	 * 
	 * @return company of pattern
	 */
	public PatternDataField getCompany() {
		
		return dataFields.get(1);
		
	}
	
	/**
	 * This method returns the item of this pattern.
	 * 
	 * @return item of pattern
	 */
	public PatternDataField getItem() {
		
		return dataFields.get(2);
		
	}
	
	/**
	 * This method returns the serial of this patern.
	 * 
	 * @return serial of pattern
	 */
	public PatternDataField getSerial() {
		
		return dataFields.get(3);
		
	}
	
	/**
	 * This method returns the data fields of this pattern.
	 * 
	 * @return list of data fields
	 */
	public List<PatternDataField> getDataFields() {
		return dataFields;
	}
	
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @param pattern to check disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ECSpecValidationException if the pattern is invalid
	 */
	public boolean isDisjoint(String pattern) throws ECSpecValidationExceptionResponse {
		return isDisjoint(new Pattern(pattern, PatternUsage.GROUP));
	}
	
	/**
	 * This method indicates if this pattern is disjoint to the specified pattern.
	 * 
	 * @param pattern to chek disjointness
	 * @return true if the patterns are disjoint and false otherwise
	 * @throws ECSpecValidationException the pattern cannot be validated.
	 */
	public boolean isDisjoint(Pattern pattern) throws ECSpecValidationExceptionResponse {
		if (!type.equals(pattern.getType())) {
			
			// if types are different, then the patterns are disjoint
			return true;
		} else {
			
			// if some corresponding data fields are disjoint, then the patterns are disjoint 
			for (int i = 0; i < dataFields.size(); i++) {
				if (dataFields.get(i).isDisjoint(pattern.getDataFields().get(i))) {
					return true;
				}
			}
			return false;
		}
		 
	}
	
	/**
	 * This method indicates if a tag is member of this filter or group pattern.
	 * If the pattern is a tag pattern, the return value is null.
	 * 
	 * @param tagURI to check for member
	 * @return true if tag is member of this pattern and false otherwise
	 * @throws ECSpecValidationException if the tag pattern is invalid
	 * @throws ImplementationException if an implementation exception occurs
	 */
	public boolean isMember(String tagURI) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		
		if (usage == PatternUsage.TAG) {
			return false;
		}
		
		// create pattern of usage TAG ('*' and 'X' are not allowed)
		Pattern tag = new Pattern(tagURI, PatternUsage.TAG);
			
		// check type
		if (tag.getType().equals(getType())) {
				
			// get data fields
			List<PatternDataField> tagDataFields = tag.getDataFields();
				
			// check number of data fields
			if (tagDataFields.size() == dataFields.size()) {
				
				// check contents of data fields
				for (int i = 0; i < dataFields.size(); i++) {
					if (!dataFields.get(i).isMember(tagDataFields.get(i).getValue())) {
						return false;
					}
				}
				return true;
					
			}
		}
		
		return false;
		
	}

	/**
	 * This method returns the group name for a tag depending on this group pattern.
	 * If the pattern is not a group pattern or the tag is not a member of this group pattern,
	 * the return value is null. 
	 * 
	 * @param tagURI to get the group name from
	 * @return group name
	 * @throws ImplementationException if an implementation exception occurs
	 * @throws ECSpecValidationException if the tag pattern is invalid
	 */
	public String getGroupName(String tagURI) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse {
		
		if (usage != PatternUsage.GROUP || !isMember(tagURI)) {
			return null;
		}
		
		try {
			
			// create pattern of usage TAG ('*', 'X' and ranges are not allowed)
			Pattern tag = new Pattern(tagURI, PatternUsage.TAG);
			
			// clone pattern to create a group name
			Pattern groupName = new Pattern(this.toString(), PatternUsage.GROUP);
			
			// get data fields
			List<PatternDataField> tagDataFields = tag.getDataFields();
			List<PatternDataField> groupNameDataFields = groupName.getDataFields();
			
			// replace 'X' in group name
			for (int i = 0; i < groupNameDataFields.size(); i++) {
				if (groupNameDataFields.get(i).isX()) {
					groupNameDataFields.set(i, tagDataFields.get(i));
				}
			}
			
			return groupName.toString();
			
		} catch (ECSpecValidationExceptionResponse e) {
			throw new ImplementationExceptionResponse(e.getMessage());
		}
		
	}
	
	/**
	 * This method returns a string representation of this pattern.
	 * 
	 * @return string representation
	 */
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(FIRST_FIELD);
		buffer.append(":");
		buffer.append(SECOND_FIELD);
		buffer.append(":");
		buffer.append(thirdField);
		buffer.append(":");
		buffer.append(type.toSring());
		buffer.append(":");
		
		for (PatternDataField dataField : dataFields) {
			buffer.append(dataField.toString());
			buffer.append(".");
		}
		
		return buffer.substring(0, buffer.length() - 1);
		
	}
	
	//
	// private methods
	//
	
	/**
	 * This method parses the data fields of this pattern.
	 * 
	 * @param dataFieldsString to parse
	 * @param pattern the whole pattern
	 * @throws ECSpecValidationException if the data field string is invalid
	 */
	private void parseDataFields(String dataFieldsString, String pattern) throws ECSpecValidationExceptionResponse {
		
		// split data fields
		String[] dataFieldsStringArray = dataFieldsString.split("\\.");
		
		// check number of data fields
		int nbrOfDataFields = type.getNumberOfDatafields();
		if (dataFieldsStringArray.length < nbrOfDataFields) {
			throw new ECSpecValidationExceptionResponse("Too less data fields '" + dataFieldsString + "' in pattern '" +
					pattern +"'. Pattern Format '" + type + "' expects " + nbrOfDataFields + " data fields.");
		} else if (dataFieldsStringArray.length > nbrOfDataFields) {
			throw new ECSpecValidationExceptionResponse("Too many data fields '" + dataFieldsString + "' in pattern '" +
					pattern + "'. Pattern Format '" + type + "' expects " + nbrOfDataFields + " data fields.");
		}
		
		// check format of each field
		for (int i = 0; i < dataFieldsStringArray.length; i++) {
			dataFields.add(new PatternDataField(dataFieldsStringArray[i], usage));
		}
		
	}
	
	/**
	 * This method indicates, if the needle string is an element of the haystack string array.
	 * 
	 * @param haystack string array which possibly contains the needle string
	 * @param needle string to search in the haystack string array
	 * @return true if the haystack contains the needle and false otherwise
	 */
	private boolean containsString(String[] haystack, String needle) {
		
		if (needle == null) {
			for (String element : haystack) {
				if (element == null) {
					return true;
				}
			}
			return false;
		} else {
			boolean found = false;
			for (String element : haystack) {
				if (needle.equals(element)) {
					found = true;
				}
			}
			return found;
		}
		
	}

}
