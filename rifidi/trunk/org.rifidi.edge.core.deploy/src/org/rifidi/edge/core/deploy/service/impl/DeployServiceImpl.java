package org.rifidi.edge.core.deploy.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
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
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This is the implementation of the DeployService. It monitors a list of
 * Directories for changes in the file system. Once it finds a new File it tries
 * to load the File as a Fragment to a Plugin already running in the OSGI
 * Context. It will lookup for a XML File inside of the Fragment to find the
 * commands this Fragment provides to its host. After it was sucessfully loaded
 * it will add the described commands to the ReaderPlugin these commands belong
 * to.
 * 
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

	/**
	 * Constructor
	 * 
	 * @param context
	 *            Context of the OSGi Framework
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.deploy.service.DeployService#add(java.util.List)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.deploy.service.DeployService#remove(java.util.List)
	 */
	@Override
	public void remove(List<String> directoriesToRemove) {
		for (String d : directoriesToRemove) {
			FileMonitor fileMonitor = directories.remove(d);
			if (fileMonitor != null)
				fileMonitor.stop();
		}
	}

	/**
	 * Uninstall Fragment
	 * 
	 * @param f
	 *            the file for the fragment to uninstall
	 */
	private void uninstallBundle(File f) {
		BundleHolder bundleHolder = bundleRegistry.get(f);
		if (bundleHolder != null) {
			logger.debug("Removing Bundle "
					+ bundleHolder.fragment.getSymbolicName());

			// TODO Move to new CommandDescription
			bundleHolder.plugin.removeCommand(bundleHolder.commands);

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

	}

	/**
	 * Install fragment
	 * 
	 * @param f
	 *            the file for the fragment to install
	 */
	@SuppressWarnings("unchecked")
	private void installBundle(File f) {

		// TODO throw Exceptions

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
			BundleHolder bundleHolder = new BundleHolder(hostBundle,
					fragmentBundle);
			bundleRegistry.put(f, bundleHolder);

			// open command.xml into CommandExtension
			InputStream in = fragmentBundle.getEntry(COMMANDXML).openStream();
			CommandExtension commandExtension = loadCommandExtension(in);

			// Get Plugin associated with ReaderInfo
			ReaderPlugin readerPlugin = readerPluginService
					.getReaderPlugin(commandExtension.getExtendPlugin());

			// TODO deferred loading
			if (readerPlugin == null) {
				logger.error("ReaderPlugin "
						+ commandExtension.getExtendPlugin() + " not found");
				return;
			} else {
				ArrayList<Class<? extends Command>> commands = new ArrayList<Class<? extends Command>>();
				for (String commandClass : commandExtension.getCommands()) {
					// TODO find out about Class cast
					Class<? extends Command> command = (Class<? extends Command>) Class
							.forName(commandClass);
					commands.add(command);
					logger.debug("found extension " + commandClass);
					for (Annotation a : command.getAnnotations()) {
						System.out.println("Annotation " + a.toString());
					}
				}
				logger.debug("adding " + commands.size()
						+ " command(s) to readerplugin "
						+ readerPlugin.getClass().getSimpleName());
				bundleHolder.commands = commands;
				bundleHolder.plugin = readerPlugin;
				// TODO Move to new CommandDescription
				readerPlugin.addCommand(commands);
			}
		} catch (BundleException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Bundle " + f.getName()
					+ " has no command XML extension");
		} catch (JAXBException e) {
			e.printStackTrace();
			logger.error("Bundle " + f.getName() + " command xml is invalid");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.deploy.monitor.FileMonitorListener#fileAddedEvent
	 * (java.io.File)
	 */
	@Override
	public void fileAddedEvent(File f) {
		synchronized (this) {
			installBundle(f);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.deploy.monitor.FileMonitorListener#fileRemovedEvent
	 * (java.io.File)
	 */
	@Override
	public void fileRemovedEvent(File f) {
		synchronized (this) {
			uninstallBundle(f);
		}
	}

	/**
	 * Parse Extension XML into a Java CommandExtension Object
	 * 
	 * @param in
	 *            InputStream of the XML File
	 * @return CommandExtenstion for the given XML File
	 * @throws JAXBException
	 *             if the XML File is invalid
	 */
	private CommandExtension loadCommandExtension(InputStream in)
			throws JAXBException {
		/*
		 * BufferedReader reader = new BufferedReader(new
		 * InputStreamReader(in)); String input; while((input =
		 * reader.readLine()) != null) { System.out.println(input); }
		 */
		return (CommandExtension) unmarshaller.unmarshal(in);
	}

	/**
	 * Find a Bundle inside of the OSGI Registry
	 * 
	 * @param bundleName
	 *            name of the Bundle
	 * @return the OSGi Bundle with the given name
	 */
	private Bundle findBundle(String bundleName) {
		for (Bundle b : context.getBundles()) {
			if (b.getSymbolicName().equals(bundleName))
				return b;
		}
		return null;
	}

	/**
	 * Inject method to obtain a instance of the ReaderPluginService from the
	 * ServiceRegistry Framework
	 * 
	 * @param readerPluginService
	 *            instance of the ReaderPluginService
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

}
