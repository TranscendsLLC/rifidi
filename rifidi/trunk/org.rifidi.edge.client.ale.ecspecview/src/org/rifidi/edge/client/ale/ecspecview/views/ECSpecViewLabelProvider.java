/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.ecspecview.model.ECSpecDecorator;

/**
 * @author kyle
 *
 */
public class ECSpecViewLabelProvider extends LabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if(element instanceof ALEServicePortType){
			return "ALE Server";
		}
		else if(element instanceof ECSpecDecorator){
			return "ECSpec: " + ((ECSpecDecorator)element).getName();
		}else if(element instanceof ArrayOfString){
			return "Subscribers";
		}else if(element instanceof LogicalReaders){
			return "Logical Readers";
		}
		return super.getText(element);
	}
}
