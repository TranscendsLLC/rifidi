 /**
     * This interface represents an event cycle. It collects the tags and manages the 
     * reports.
     */
package org.rifidi.edge.server.epcglobal.ale;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.rifidi.edge.epcglobal.ale.ECReport;
import org.rifidi.edge.epcglobal.ale.ECReportSpec;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
//import org.rifidi.edge.server.ale.infrastructure.TagReadEvent;
import org.rifidi.edge.notification.TagReadEvent;

import rx.Observer;

/**
 * @author DanielGomez
 *
 */
public interface Cycle extends Observer<TagReadEvent>{	
   
	/**
	 * This method adds a tag to this event cycle.
	 * 
	 * @param tag to add
	 * @throws ImplementationException if an implementation exception occurs
	 * @throws ECSpecValidationException if the tag is not valid
	 */
	void addTag(Tag tag);
	
	/**
	 * implementation of the observer interface for tags.
	 * @param o an observable object that triggered the update
	 * @param arg the arguments passed by the observable
	 */
	//@Override
	//void update(Observable o, Object arg);
	
	
	
	/**
	 * This method stops the thread.
	 */
	void stop();
	
	/**
	 * This method returns the name of this event cycle.
	 * 
	 * @return name of event cycle
	 */
	String getName();
	
	/**
	 * This method indicates if this event cycle is terminated or not.
	 * 
	 * @return true if this event cycle is terminated and false otherwise
	 */
	boolean isTerminated();
	
	/**
	 * starts this EventCycle.
	 */
	void launch();
	
	/**
	 * returns the set of tags from the previous EventCycle run.
	 * @return a set of tags from the previous EventCycle run
	 */
	Set<Tag> getLastEventCycleTags();

	/**
	 * This method return all tags of this event cycle.
	 * 
	 * @return set of tags
	 */
	Set<Tag> getTags();
	
	/**
	 * @return the number of rounds this event cycle has already run through.
	 */
	int getRounds();
	
	/**
	 * thread synchronizer for the end of this event cycle. if the event cycle 
	 * has already finished, then the method returns immediately. otherwise the 
	 * thread waits for the finish.
	 * @throws InterruptedException
	 */
	void join() throws InterruptedException;

	/**
	 * get the report spec identified by the given name.
	 * @param name the name of the spec to obtain.
	 * @return the ECReportSpec.
	 */
	ECReportSpec getReportSpecByName(String name);

	/**
	 * @return the lastReports
	 */
	Map<String, ECReport> getLastReports();
	
	ECSpec getECSpec();
	
	void setECSpec(ECSpec ecSpec);
	
	void poll();
	
	ECReports getPollReports();
	
	void limpiarTags();
    
}
