/* 
 *  SpecModelWrapper.java
 *  Created:	Apr 17, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.serviceprovider;

import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public abstract class SpecModelWrapper {
	protected SpecDataManager parent = null;
	protected String name = "NewEcSpec";

	/**
	 * @return the parent
	 */
	public SpecDataManager getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(SpecDataManager parent) {
		this.parent = parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 */
	public SpecModelWrapper(String name, SpecDataManager parent) {
		this.name=name;
		this.parent=parent;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public abstract ECSpec getEcSpec();
	
	public abstract String define();
	
	public abstract String undefine();

}
