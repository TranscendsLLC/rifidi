
package org.rifidi.edge.client.twodview.views;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * TODO: Kyle: Class level comment.  
 * 
 * @author kyle
 */
public class SiteViewAdapterFactory implements IAdapterFactory {

	/** List of classes the CommandView can be adapted to */
	private Class[] classes = new Class[] { IPropertySheetPage.class };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object,
	 * java.lang.Class)
	 */
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IPropertySheetPage.class)
			return new TabbedPropertySheetPage(
					(ITabbedPropertySheetPageContributor) adaptableObject);
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
