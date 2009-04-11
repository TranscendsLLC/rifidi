/**
 * 
 */
package org.rifidi.edge.client.model.sal.properties;

/**
 * An event that gets fired when the 'dirty' state of a reader changes. The
 * ditry state changes to true when a user modifies a property of the reader. It
 * changes to false when the user commits the properties to the reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DirtyReaderPropertyBean {

	/** The name of the state property */
	public static final String DIRTY_EVENT_PROPERTY = "org.rifidi.edge.client.model.sal.RemoteReader.dirty";
	/** The readerID */
	private String readerID;
	/** Whether or not the reader is dirty */
	private boolean dirty;
	/** The hashstring for this object */
	private String hashString;

	/**
	 * @param readerID
	 * @param dirty
	 */
	public DirtyReaderPropertyBean(String readerID, boolean dirty) {
		super();
		this.readerID = readerID;
		this.dirty = dirty;
		hashString = readerID + dirty;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DirtyReaderPropertyBean) {
			DirtyReaderPropertyBean bean = (DirtyReaderPropertyBean) obj;
			return this.hashString.equals(bean.hashString);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashString.hashCode();
	}
}
