package org.rifidi.edge.server.epcglobal.ale.services;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchSubscriberExceptionResponse;

//TODO ALE interface
public interface ApplicationLevelEventService {

	/**
	 * This method indicates if the ALE is ready or not.
	 * 
	 * @return true if the ALE is ready and false otherwise
	 */
	boolean isReady();
	
	/**
	 * With this method an ec specification can be defined.
	 * 
	 * @param specName of the ec specification
	 * @param spec to define
	 * @throws DuplicateNameExceptionResponse if a ec specification with the same name is already defined
	 * @throws ECSpecValidationExceptionResponse if the ec specification is not valid
	 * @throws ImplementationExceptionResponse if an implementation ExceptionResponse occurs
	 */
	void define(String specName, ECSpec spec) throws DuplicateNameExceptionResponse, ECSpecValidationExceptionResponse, ImplementationExceptionResponse;
	
	/**
	 * With this method an ec specification can be undefined.
	 * 
	 * @param specName of the ec specification to undefine
	 * @throws NoSuchNameExceptionResponse if there is no ec specification with this name defined
	 */
	void undefine(String specName) throws NoSuchNameExceptionResponse;
	
	/**
	 * This method returns an ec specification depending on a given name.
	 * 
	 * @param specName of the ec specification to return
	 * @return ec specification with the specified name
	 * @throws NoSuchNameExceptionResponse if no such ec specification exists
	 */
	ECSpec getECSpec(String specName) throws NoSuchNameExceptionResponse;
	
	/**
	 * This method returns the names of all defined ec specifications.
	 * 
	 * @return string array with names
	 */
	String[] getECSpecNames();
	
	/**
	 * With this method a notification uri can be subscribed to a defined ec specification.
	 * 
	 * @param specName of the ec specification
	 * @param notificationURI to subscribe
	 * @throws NoSuchNameExceptionResponse if there is no ec specification with the given name defined
	 * @throws InvalidURIExceptionResponse if the specified notification uri is invalid
	 * @throws DuplicateSubscriptionExceptionResponse if the same subscription is already done
	 * @throws ValidationExceptionResponse 
	 */
	void subscribe(String specName, String notificationURI) throws NoSuchNameExceptionResponse, InvalidURIExceptionResponse, DuplicateSubscriptionExceptionResponse;

	/**
	 * With this method a notification uri can be unsubscribed from a defined ec specification.
	 * 
	 * @param specName of the ec specification
	 * @param notificationURI to unsubscribe
	 * @throws NoSuchNameExceptionResponse if there is no ec specification with the given name defined
	 * @throws NoSuchSubscriberExceptionResponse if the specified notification uri is not subscribed to the ec specification.
	 * @throws InvalidURIExceptionResponse if the specified notification uri is invalid
	 */
	void unsubscribe(String specName, String notificationURI) throws NoSuchNameExceptionResponse, NoSuchSubscriberExceptionResponse, InvalidURIExceptionResponse;

	/**
	 * With this method a defined ec specification can be polled.
	 * Polling is the same as subscribe to a ec specification, waiting for one event cycle and then unsubscribe
	 * with the difference that the report is the result of the method instead of sending it to an uri.
	 * 
	 * @param specName of the ec specification which schould be polled
	 * @return ec report of the next event cycle
	 * @throws NoSuchNameExceptionResponse if there is no ec specification with the given name defined
	 */
	ECReports poll(String specName) throws NoSuchNameExceptionResponse, ImplementationExceptionResponse;
	
	/**
	 * With this method a undefined ec specifcation can be executed.
	 * It's the same as defining the ec specification, polling and undefining it afterwards.
	 * 
	 * @param spec ec specification to execute
	 * @return ec report of the next event cycle
	 * @throws ECSpecValidationExceptionResponse if the ec specification is not valid
	 * @throws ImplementationExceptionResponse if an implementation ExceptionResponse occures
	 */
	ECReports immediate(ECSpec spec) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse;
	
	/**
	 * This method returns all subscribers to a given ec specification name.
	 * 
	 * @param specName of which the subscribers should be returned
	 * @return array of string with notification uris
	 * @throws NoSuchNameExceptionResponse if there is no ec specification with the given name is defined
	 */
	Set<String> getSubscribers(String specName) throws NoSuchNameExceptionResponse;
		
	/**
	 * This method returns the standard version to which this implementation is compatible.
	 * 
	 * @return standard version
	 */
	String getStandardVersion();
	
	/**
	 * This method returns the vendor version of this implementation.
	 * 
	 * @return vendor version
	 */
	String getVendorVersion();
	
	

	
}
