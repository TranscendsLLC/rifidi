
package org.rifidi.edge.client.ale.logicalreaders;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.ale.logicalreaders.decorators.LRSpecDecorator;
import org.rifidi.edge.client.ale.logicalreaders.decorators.LRSpecSubnodeDecorator;

/**
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class LRLabelProvider extends LabelProvider {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof LRSpecDecorator) {
			return ((LRSpecDecorator) element).getName();
		}
		return super.getText(element);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		if(element instanceof LRSpecSubnodeDecorator){
			return Activator.getDefault().getImageRegistry().get(Activator.ICON_READER);
		}
		if (element instanceof LRSpecDecorator) {
			if(((LRSpecDecorator) element).isIsComposite()){
				return Activator.getDefault().getImageRegistry().get(Activator.ICON_READER);
			}
			return Activator.getDefault().getImageRegistry().get(Activator.ICON_READER_LOCKED);
		}
		return super.getImage(element);
	}

}
