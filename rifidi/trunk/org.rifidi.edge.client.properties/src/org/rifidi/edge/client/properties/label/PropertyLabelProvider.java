package org.rifidi.edge.client.properties.label;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;

public class PropertyLabelProvider extends LabelProvider {
	
	Log logger = LogFactory.getLog(PropertyLabelProvider.class);

	
	@Override
	public String getText(Object element){
		
		if(element instanceof TreeSelection){
			Object o =((TreeSelection)element).getFirstElement();
			if(o instanceof RemoteReader){
				RemoteReader rr = (RemoteReader)o;
				return "Properties for Reader " + rr.getID();
			}
			
		}
		return "";
	}
}
