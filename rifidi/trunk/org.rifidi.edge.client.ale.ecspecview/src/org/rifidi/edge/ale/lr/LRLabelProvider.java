/**
 * 
 */
package org.rifidi.edge.ale.lr;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.rifidi.edge.ale.lr.decorators.LRSpecDecorator;
import org.rifidi.edge.ale.lr.decorators.LRSpecSubnodeDecorator;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LRLabelProvider extends LabelProvider {

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
