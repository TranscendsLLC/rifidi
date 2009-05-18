package org.rifidi.edge.client.mbean.ui.widgets.data;

import javax.management.MBeanAttributeInfo;

/**
 * The base abstract class for the data used to construct widgets
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractWidgetData {

	/** The MBeanAttributeInfo for this form */
	protected MBeanAttributeInfo element;

	/**
	 * Construct a new WidgetData object
	 * 
	 * @param element
	 *            The JDOM element for the 'form element'
	 */
	public AbstractWidgetData(MBeanAttributeInfo element) {
		this.element = element;
	}

	/**
	 * @return The name of this 'form element'
	 */
	public String getName() {
		return element.getName();
	}

	/**
	 * 
	 * 
	 * @return The display name of this 'form element'
	 */
	public String getDisplayName() {
		return (String) element.getDescriptor().getFieldValue("displayName");
	}

	/**
	 * 
	 * 
	 * @return the default value for this 'form element'
	 */
	public abstract Object getDefaultValue();

	/**
	 * 
	 * 
	 * @return true if this 'form element' should be allowed to be edited by the
	 *         user, false otherwise
	 */
	public boolean isEditable() {
		return element.isWritable();
	}

	/**
	 * @return The type of this 'form element'.
	 * @see FormElementType
	 */
	public String getType() {
		return element.getType();
	}

	/**
	 * Get the order value for this element
	 * 
	 * @return
	 */
	public Float getOrder() {
		return (Float) element.getDescriptor().getFieldValue(
				"org.rifidi.edge.ordervalue");
	}

	/**
	 * Get description
	 * 
	 * @return
	 */
	public String getDescription() {
		return element.getDescription();
	}

}
