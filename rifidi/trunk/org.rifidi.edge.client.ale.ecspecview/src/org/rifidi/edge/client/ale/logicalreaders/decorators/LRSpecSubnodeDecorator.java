/**
 * 
 */
package org.rifidi.edge.client.ale.logicalreaders.decorators;
//TODO: Comments
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec;
import org.rifidi.edge.client.ale.logicalreaders.LRTreeContentProvider;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class LRSpecSubnodeDecorator extends LRSpecDecorator {
	/** Parent of this node. */
	private LRSpecDecorator parent;

	public LRSpecSubnodeDecorator(String name, LRSpec decorated,
			LRSpecDecorator parent, LRTreeContentProvider lrTreeContentProvider) {
		super(name, decorated);
		this.parent = parent;
	}

	/**
	 * @return the parent
	 */
	public LRSpecDecorator getParent() {
		return parent;
	}

}
