package org.rifidi.edge.core.deploy.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.rifidi.edge.core.deploy.monitor.FileMonitor;
import org.rifidi.edge.core.deploy.monitor.FileMonitorListener;
import org.rifidi.edge.core.deploy.service.DeployService;
import org.rifidi.edge.core.deploy.utilities.BundleHolder;
import org.rifidi.edge.core.deploy.xml.CommandExtension;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
public class DeployServiceImpl implements DeployService, FileMonitorListener {

	private Log logger = LogFactory.getLog(DeployServiceImpl.class);

	private final String COMMANDXML = "command.xml";

	private BundleContext context;
	private ReaderPluginService readerPluginService;

	private HashMap<String, FileMonitor> directories = new HashMap<String, FileMonitor>();

	private HashMap<File, BundleHolder> bundleRegistry = new HashMap<File, BundleHolder>();

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;

	public DeployServiceImpl(BundleContext context) {
		ServiceRegistry.getInstance().service(this);
		this.context = context;

		try {
			jaxbContext = JAXBContext.newInstance(CommandExtension.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			// Well this should not happen.. so just report a stack trace
			e.printStackTrace();
		}

	}

	@Override
	public void add(List<String> directoriesToMonitor) {
		if (directoriesToMonitor != null)
			for (String d : directoriesToMonitor) {
				FileMonitor fileMonitor = new FileMonitor("FileMonitor " + d, d);
				if (fileMonitor.start()) {
					fileMonitor.addFileMonitorListener(this);
					directories.put(d, fileMonitor);
				} else
					fileMonitor.stop();
			}
	}

	@Override
	public void remove(List<String> directoriesToRemove) {
		for (String d : directoriesToRemove) {
			FileMonitor fileMonitor = directories.remove(d);
			if (fileMonitor != null)
				fileMonitor.stop();
		}
	}

	private void uninstallBundle(File f) {
		BundleHolder bundleHolder = bundleRegistry.get(f);
		if (bundleHolder != null)
			logger.debug("Removing Bundle " + bundleHolder.fragment.getSymbolicName());
			try {
				bundleHolder.fragment.uninstall();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				bundleHolder.host.update();
			} catch (BundleException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	// TODO throw Exceptions
	@SuppressWarnings("unchecked")
	private void installBundle(File f) {
		try {
			// Install Fragment
			Bundle fragmentBundle = context.installBundle("file:"
					+ f.getAbsolutePath());
			// Get Fragment Host
			String[] hostBundleName = ((String) fragmentBundle.getHeaders()
					.get("Fragment-Host")).split(";");
			if (hostBundleName[0].isEmpty())
				return;
			// Find Host of Fragment and reload to start Fragment
			Bundle hostBundle = findBundle(hostBundleName[0]);
			hostBundle.update();

			// Save BundleFragment and File
			bundleRegistry.put(f, new BundleHolder(hostBundle, fragmentBundle));

			// open command.xml into CommandExtension
			InputStream in = fragmentBundle.getEntry(COMMANDXML).openStream();
			CommandExtension commandExtension = loadCommandExtension(in);

			// Load ReaderInfo Class
			// TODO check if class is extending ReaderInfo	
			Class<?> readerInfoClass = Class.forName(commandExtension.getExtendPlugin());
			
			// Get Plugin associated with ReaderInfo
			
			//TODO find out about Class cast
			ReaderPlugin readerPlugin = readerPluginService
					.getReaderPlugin((Class<? extends ReaderInfo>) readerInfoClass);
			
			// TODO deferred loading 
			if (readerPlugin == null) {
				logger.error("ReaderPlugin "
						+ commandExtension.getExtendPlugin() + " not found");
				return;
			} else{
				ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
				for(String commandClass : commandExtension.getCommands()){
					//TODO find out about Class cast
					commands.add((Class<? extends Command>) Class.forName(commandClass));
				}
					
				readerPlugin.addCommand(commands);
			}
		} catch (BundleException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Bundle " + f.getName() + " has no command XML extension");
		} catch (JAXBException e) {
			e.printStackTrace();
			logger.error("Bundle " + f.getName() + " command xml is invalid");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void fileAddedEvent(File f) {
		synchronized (this) {
			installBundle(f);
		}
	}

	@Override
	public void fileRemovedEvent(File f) {
		synchronized (this) {
			uninstallBundle(f);
		}
	}

	private CommandExtension loadCommandExtension(InputStream in)
			throws JAXBException {
		/*
		 * BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(in)); String input; while((input =
		 * reader.readLine()) != null) { System.out.println(input); }
		 */
		return (CommandExtension) unmarshaller.unmarshal(in);
	}

	private Bundle findBundle(String bundleName) {
		for (Bundle b : context.getBundles()) {
			if (b.getSymbolicName().equals(bundleName))
				return b;
		}
		return null;
	}

	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

}
