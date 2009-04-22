/**
 * 
 */
package org.rifidi.edge.ale.lr.decorators;

import org.rifidi.edge.ale.lr.LRTreeContentProvider;
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LRSpecSubnodeDecorator extends LRSpecDecorator {
	/** Parent of this node. */
	private LRSpecDecorator parent;

	public LRSpecSubnodeDecorator(String name, LRSpec decorated,
			LRSpecDecorator parent, LRTreeContentProvider lrTreeContentProvider) {
		super(name, decorated, lrTreeContentProvider);
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public LRSpecDecorator getParent() {
		return parent;
	}

}
