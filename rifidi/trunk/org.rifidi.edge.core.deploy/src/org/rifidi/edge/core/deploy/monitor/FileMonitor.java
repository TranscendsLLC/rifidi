package org.rifidi.edge.core.deploy.monitor;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;

/**
 * File system monitor for directory changes
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class FileMonitor extends AbstractThread {
	private Log logger = LogFactory.getLog(FileMonitor.class);

	private Set<FileMonitorListener> listeners = new HashSet<FileMonitorListener>();

	private String pathToMonitor;
	private File directory;

	private HashSet<File> monitoredFiles = new HashSet<File>();

	public FileMonitor(String threadName, String pathToMonitor) {
		super(threadName);
		this.pathToMonitor = pathToMonitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.common.utilities.thread.AbstractThread#start()
	 */
	@Override
	public boolean start() {
		// TODO think about throwing exceptions

		// Check if all the Arguments are valid
		if (pathToMonitor.isEmpty())
			return false;
		directory = new File(pathToMonitor);
		if (directory.isDirectory())
			return super.start();
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO make this more flexible
		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".jar"))
					return true;
				else
					return false;
			}
		};
		try {
			while (running) {
				for (File f : directory.listFiles(filter)) {
					if (!monitoredFiles.contains(f)) {
						// TODO only monitor JAR files
						logger.debug("New File found : " + f.getName());
						monitoredFiles.add(f);
						fireFileAddEvent(f);
					}
				}
				for (File f : monitoredFiles) {
					if (!f.exists()) {
						logger.debug("File removed : " + f.getName());
						monitoredFiles.remove(f);
						fireFileRemoveEvent(f);
					}
				}
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			// ignore this. It's break condition
		}

	}

	/**
	 * Register new listener for changes in the file system
	 * 
	 * @param listener
	 *            listener for changes
	 */
	public void addFileMonitorListener(FileMonitorListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove previous registered listener
	 * 
	 * @param listener
	 *            listener for changes
	 */
	public void removeFileMonitorListener(FileMonitorListener listener) {
		listeners.remove(listener);
	}

	/**
	 * New file found
	 * 
	 * @param f
	 *            file found
	 */
	private void fireFileAddEvent(File f) {
		for (FileMonitorListener listener : listeners) {
			listener.fileAddedEvent(f);
		}
	}

	/**
	 * File was removed
	 * 
	 * @param f
	 *            file removed
	 */
	private void fireFileRemoveEvent(File f) {
		for (FileMonitorListener listener : listeners) {
			listener.fileRemovedEvent(f);
		}
	}
}
