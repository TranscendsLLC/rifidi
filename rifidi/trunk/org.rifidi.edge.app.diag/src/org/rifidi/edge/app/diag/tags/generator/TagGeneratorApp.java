/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.services.notification.data.ReadCycle;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagGeneratorApp extends AbstractRifidiApp {

	private ScheduledExecutorService executorService;
	private ConcurrentHashMap<Integer, TagGenerator> idToGenerator;
	private int generatorID = 0;

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public TagGeneratorApp(String group, String name) {
		super(group, name);
		executorService = new ScheduledThreadPoolExecutor(10);
		idToGenerator = new ConcurrentHashMap<Integer, TagGenerator>();

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart= getProperty(LAZY_START, "true");
		return Boolean.parseBoolean(lazyStart);
	}

	public void generate(String ID, String readerID, int interval) {
		ReadCycle readCycle = ReadCycleFactory.createReadCycle(ID, readerID);
		TagGenerator tagGenerator;
		synchronized (this) {
			tagGenerator = new TagGenerator(readCycle, getEPRuntime());
			idToGenerator.put(generatorID++, tagGenerator);
		}
		if (interval <= 0) {
			executorService.execute(tagGenerator);
		} else {
			executorService.scheduleAtFixedRate(tagGenerator, 0, interval,
					TimeUnit.MILLISECONDS);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#getCommandProider()
	 */
	@Override
	protected CommandProvider getCommandProider() {
		TagGeneratorCommandProvider command = new TagGeneratorCommandProvider();
		command.setGeneratorApp(this);
		return command;
	}

}
