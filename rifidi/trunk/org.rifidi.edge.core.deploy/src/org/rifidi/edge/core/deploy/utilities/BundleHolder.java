package org.rifidi.edge.core.deploy.utilities;

import org.osgi.framework.Bundle;

public class BundleHolder {

	public Bundle host;
	public Bundle fragment;
	
	public BundleHolder(Bundle host, Bundle fragment) {
		this.host = host;
		this.fragment = fragment;
	}
	

}
