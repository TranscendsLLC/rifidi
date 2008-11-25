package org.rifidi.edge.client.properties.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;


public class ConnectionViewPropertyAdapterFactory implements IAdapterFactory {

	//private Log logger = LogFactory.getLog(ConnectionViewPropertyAdapterFactory.class);
	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_LIST = {IPropertySheetPage.class };

	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(IPropertySheetPage.class.equals(adapterType)){
			if(adaptableObject instanceof EdgeServerConnectionView){
				return new TabbedPropertySheetPage((EdgeServerConnectionView)adaptableObject);
			}
		}
		return null;
	}

	@Override
	public Class[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
