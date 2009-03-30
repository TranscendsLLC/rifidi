/**
 * 
 */
package org.rifidi.edge.client.sal.properties;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * An adapter factory for teh RemoteEdgeServer model object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteEdgeServerAdapterFactory implements IAdapterFactory {

	/** List of classes the RemoteEdgeServer can be adapted to */
	private Class[] classes = new Class[] { IPropertySource2.class };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType.equals(IPropertySource.class)) {
			return new RemoteEdgeServerPropertyAdapter(
					(RemoteEdgeServer) adaptableObject);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapterList()
	 */
	@Override
	public Class[] getAdapterList() {
		return classes;
	}

}
