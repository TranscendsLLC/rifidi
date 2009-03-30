/**
 * 
 */
package org.rifidi.edge.client.sal.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * An adapter for the RemtoeEdgeServer to be an IPropertySoruce
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteEdgeServerPropertyAdapter implements IPropertySource2 {

	/** The model object to be adapted */
	private RemoteEdgeServer edgeServer;
	/** Property Descriptors */
	private IPropertyDescriptor[] propertyDescriptors;

	public RemoteEdgeServerPropertyAdapter(RemoteEdgeServer edgeServer) {
		this.edgeServer = edgeServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable
	 * (java.lang.Object)
	 */
	@Override
	public boolean isPropertyResettable(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource2#isPropertySet(java.lang
	 * .Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if (propertyDescriptors == null) {
			IPropertyDescriptor stateDescriptor = new PropertyDescriptor(
					RemoteEdgeServer.STATE_PROPERTY, "State");
			this.propertyDescriptors = new IPropertyDescriptor[] { stateDescriptor };
		}
		return propertyDescriptors;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(RemoteEdgeServer.STATE_PROPERTY)) {
			return edgeServer.getState();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
	 * .lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java
	 * .lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		// TODO Auto-generated method stub

	}

}
