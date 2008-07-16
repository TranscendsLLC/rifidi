package org.rifidi.edge.core.deploy.utilities;

import java.util.List;

import org.osgi.framework.Bundle;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;

public class BundleHolder {

	public Bundle host;
	public Bundle fragment;

	public List<Class<? extends Command>> commands;
	public ReaderPlugin plugin;

	public BundleHolder(Bundle host, Bundle fragment) {
		this.host = host;
		this.fragment = fragment;
	}

}
