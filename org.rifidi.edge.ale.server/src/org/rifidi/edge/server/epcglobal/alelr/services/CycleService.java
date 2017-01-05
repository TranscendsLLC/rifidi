/**
 * 
 */
package org.rifidi.edge.server.epcglobal.alelr.services;

import java.util.List;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.server.epcglobal.ale.Cycle;

import rx.Observer;
import rx.Subscription;

/**
 * @author Daniel Gómez
 *
 */
public interface CycleService {

	boolean containsKey(String specName);
	Set<String> getSubscribers(String specName);
	
    Cycle define(String specName, ECSpec spec) throws ImplementationExceptionResponse, ECSpecValidationExceptionResponse;
    Iterable<ECSpec> getECSpecs();
    ECSpec getECSpec(String specName);
    List<String> getECSpecNames();
    Cycle getCycle(String specName);
    void loadEventCycles();
//    void poll();
    void subscribe(String specName, String notificationURI) throws InvalidURIExceptionResponse;
    void undefine(String specName);
    void unsubscribe(String specName, String notificationURI);
    Subscription subscribe(String specName, Observer<ECReports> observer);
    Cycle get(String specName);

}
