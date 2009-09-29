/*
 * 
 * RifidiEdgeServerCommands.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.provisioning;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.service.obr.RepositoryAdmin;

/**
 * Command line commands for the edge server.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class OBRCommands implements CommandProvider {

	private BundleContext context = null;
	private RepositoryAdmin repoAdmin = null;
	private ObrCommandImpl impl;
	
	/**
	 * @param context
	 * @param repoAdmin
	 */
	public OBRCommands(BundleContext context, RepositoryAdmin repoAdmin) {
		this.context = context;
		this.repoAdmin = repoAdmin;
		this.impl=new ObrCommandImpl(context, repoAdmin);
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(BundleContext context) {
		this.context = context;
	}

	/**
	 * @param repoAdmin the repoAdmin to set
	 */
	public void setRepoAdmin(RepositoryAdmin repoAdmin) {
		this.repoAdmin = repoAdmin;
	}

	/**
	 * Get the available reader factory IDs
	 * 
	 * @param intp
	 * @return
	 */
	public Object _obr(CommandInterpreter intp) {
		StringBuffer buffer = new StringBuffer("obr ");
		ByteArrayOutputStream errors=new ByteArrayOutputStream();
		ByteArrayOutputStream output=new ByteArrayOutputStream();
		for (String arg = intp.nextArgument(); arg != null; arg = intp
				.nextArgument()) {
			buffer.append(arg);
			buffer.append(' ');
		}
		
		impl.execute(buffer.toString(), new PrintStream(output), new PrintStream(errors));
		intp.print(output.toString());
		intp.print(errors.toString());
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("---Rifidi Edge Server Commands---\n");
		return buffer.toString();
	}
}
