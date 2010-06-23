/**
 * 
 */
package org.rifidi.edge.core.app.api.resources;

import java.util.Properties;

/**
 * A Resource Description is a wrapper around a properties file that describes a
 * resource. The Resource description is used to create and locate resources.
 * 
 * By default, a resource is equal to another resource if the NAME property is
 * equal. A NAME property is required.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class ResourceDescription implements ResourceProperties {

	/** The Reource Properties */
	protected Properties properties = new Properties();

	/**
	 * Set the resource's properties
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties) {
		if (!properties.containsKey(NAME)) {
			throw new IllegalArgumentException(
					"Resource Description properties must contain a property called name");
		}
		this.properties.putAll(properties);

	}

	/**
	 * @return The name of this resource.
	 */
	public String getName() {
		return properties.getProperty(NAME);
	}

	/**
	 * A helper method that throws a Null Pointer if the property is not
	 * available
	 * 
	 * @param name
	 * @return
	 */
	protected String getProperty(String key) {
		String property = properties.getProperty(key);
		if (property == null)
			throw new NullPointerException("Property does not exist: " + key
					+ " in properties " + getName());
		return property;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null)
			return false;

		if (!(obj instanceof ResourceDescription))
			return false;
		ResourceDescription that = (ResourceDescription) obj;
		return that.getName().equals(this.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getProperty(NAME);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	
	

}
