package com.thinkify.rfid;

import java.util.ArrayList;

/**
 * ThinkifyTaglist extends from ArrayList, for holding onto a ordered collection
 * of ThinkifyTag objects. Additional methods were added to this class for
 * initializing the list based on a taglist string from a reader, and updating
 * the list based on new tag data.
 * 
 * @author David Krull
 */
public class ThinkifyTaglist extends ArrayList<ThinkifyTag> {
	private static final long serialVersionUID = 1L;

	public ThinkifyTaglist() {
		super();
	}
	
	/**
	 * Initializes this ThinkifyTaglist, using the \r\n-separated string of taglist
	 * data received directly from the reader. Each line of taglistString is split
	 * off and parsed into a ThinkifyTag, which is then added to this ThinkifyTaglist.
	 * 
	 * @param taglistString the raw taglist data from the reader
	 */
	public ThinkifyTaglist(String taglistString) {
		super();
		updateFromTaglistString(taglistString);
	}
	
	
	/**
	 * Parses a single "TAG=" line of tag data from the reader, creates a
	 * ThinkifyTag for that line, and adds/updates this ThinkifyTaglist with
	 * that new data.
	 * 
	 * @param tagLine the single line of raw tag data from the reader
	 */
	public void updateFromTagString(String tagLine) {
		if (tagLine == null || tagLine.equals("")) return;
		
		ThinkifyTag tag = new ThinkifyTag(tagLine);
		updateTag(tag);
	}
	
	
	/**
	 * Parses an entire \r\n-separated string of taglist data received directly
	 * from the reader, creates ThinkifyTags for each line, and adds/updates
	 * this ThinkifyTaglist with the new data.
	 * 
	 * @param taglistString the raw taglist data from the reader
	 */
	public void updateFromTaglistString(String taglistString) {
		if (taglistString == null) taglistString = "";
		String[] tagLines = taglistString.split("\n");
		
		for (String tagLine : tagLines) {
			updateFromTagString(tagLine);
		}
	}
	
	
	/**
	 * Adds the given newTag to this ThinkifyTaglist. This may produce duplicate
	 * entries in the ThinkifyTaglist with the same EPC. To merge new data into
	 * existing tag entries, use updateTag() instead.
	 * 
	 * @param newTag the ThinkifyTag to add to this ThinkifyTaglist
	 */
	public void addTag(ThinkifyTag newTag) {
		this.add(newTag);
	}
	
	
	/**
	 * If the given newTag already exists in this ThinkifyTaglist, the new
	 * information is merged with the existing tag entry, using ThinkifyTag.update().
	 * If the newTag doesn't already exist, it is added to this ThinkifyTaglist.
	 * 
	 * @param newTag the ThinkifyTag data to add/merge
	 */
	public void updateTag(ThinkifyTag newTag) {
		// If newTag exists then updated it, else add it.
		int existingTagIndex = this.indexOf(newTag);
		if (existingTagIndex >= 0) {
			this.get(existingTagIndex).update(newTag);
		} else {
			this.add(newTag);
		}
	}
}
