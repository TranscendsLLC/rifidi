package org.rifidi.edge.client.tags.utils;

import java.math.BigInteger;
import java.util.Arrays;

import org.rifidi.edge.core.api.readerplugin.messages.impl.TagMessage;

public class TagContainer implements Comparable<TagContainer> {

	private TagMessage tag;
	private long internalTime;

	public TagContainer() {
		super();
	}

	public TagContainer(TagMessage tag, long internalTime) {
		this.tag = tag;
		this.internalTime = internalTime;
	}

	public TagContainer(TagMessage tag) {
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public TagMessage getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public void setTag(TagMessage tag) {
		this.tag = tag;
	}

	/**
	 * @return the internalTime
	 */
	public long getInternalTime() {
		return internalTime;
	}

	/**
	 * @param internalTime
	 *            the internalTime to set
	 */
	public void setInternalTime(long internalTime) {
		this.internalTime = internalTime;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof TagContainer))
			return false;
		return (compareTo((TagContainer) o) == 0);

	}

	@Override
	public int compareTo(TagContainer o) {
		if (!(o instanceof TagContainer))
			throw new ClassCastException();
		BigInteger bigIntThis = new BigInteger(1, tag.getId());
		BigInteger bigIntO = new BigInteger(1, o.getTag().getId());
		return bigIntThis.compareTo(bigIntO);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(tag.getId());
	}

	/**
	 * Returns a string version of this Object. Used for debugging purposes.
	 * 
	 * @return A string version of this Object.
	 */
	@Override
	public String toString() {
		return tag.toXML();
	}

	public String toXML() {
		return tag.toXML();
	}
}
