/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.RifidiApp;
import org.rifidi.edge.core.services.notification.data.ReadCycle;

import com.espertech.esper.client.EPRuntime;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagGeneratorApp extends RifidiApp {

	private ScheduledExecutorService executorService;
	private ConcurrentHashMap<Integer, TagGenerator> idToGenerator;
	private int generatorID = 0;

	public TagGeneratorApp(String name) {
		super(name);
		executorService = new ScheduledThreadPoolExecutor(10);
		idToGenerator = new ConcurrentHashMap<Integer, TagGenerator>();

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
	
}
