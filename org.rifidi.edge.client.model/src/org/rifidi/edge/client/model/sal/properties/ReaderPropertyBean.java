package org.rifidi.edge.client.model.sal.properties;

import javax.management.Attribute;

public class ReaderPropertyBean {

	/** The name of the state property */
	public static final String READER_PROPERTY_BEAN = "org.rifidi.edge.client.model.sal.RemoteReader.property";
	/** The readerID */
	private String readerID;
	/** The attribute to change */
	private Attribute attribute;
	private String hashstring;

	/**
	 * @param readerID
	 * @param attribute
	 */
	public ReaderPropertyBean(String readerID, Attribute attribute) {
		super();
		this.readerID = readerID;
		this.attribute = attribute;
		if (attribute != null) {
			this.hashstring = READER_PROPERTY_BEAN + "." + readerID + "."
					+ attribute.getName() + "." + attribute.getValue();
		} else {
			this.hashstring = READER_PROPERTY_BEAN + "." + readerID + "."
					+ attribute;
		}
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ReaderPropertyBean) {
			ReaderPropertyBean bean = (ReaderPropertyBean) obj;
			return this.hashstring.equals(bean.hashstring);
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
		return hashstring.hashCode();
	}

}
