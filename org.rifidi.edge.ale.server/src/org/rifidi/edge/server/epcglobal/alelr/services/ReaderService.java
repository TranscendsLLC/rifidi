package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.epcglobal.alelr.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.NonCompositeReaderExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ReaderLoopExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.SecurityExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.epcglobal.ale.EventCycle;
import org.rifidi.edge.server.epcglobal.alelr.Reader;

import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscription;

public interface ReaderService{
	
	Iterable<org.rifidi.edge.epcglobal.alelr.LogicalReader> getAleLogicalReaders();
	org.rifidi.edge.epcglobal.alelr.LogicalReader getAleLogicalReader(String logicalReaderName);
    void loadLogicalReaders();
    org.rifidi.edge.server.epcglobal.alelr.Reader addAleLogicalReaderToCompositeReader(String compositeReaderName, org.rifidi.edge.epcglobal.alelr.LogicalReader logicalReader) throws ImplementationExceptionResponse, ValidationExceptionResponse;
    org.rifidi.edge.server.epcglobal.alelr.Reader createAleLogicalReader(org.rifidi.edge.epcglobal.alelr.LogicalReader logicalReader) throws ImplementationExceptionResponse, ValidationExceptionResponse, IOException, JAXBException, CommandSubmissionException;
    
    /**
	 * returns the requested logicalReader.
	 * @param readerName name of the requested reader
	 * @return a logicalReder
	 */
    org.rifidi.edge.server.epcglobal.alelr.Reader getLogicalReader(String logicalReaderName);
    Subscription subscribe(String logicalReaderName, Observer<TagReadEvent> observer);
    
    /**
	 * returns all available logicalReaders.
	 * @return Set of LogicalReader
	 */
    Collection<org.rifidi.edge.server.epcglobal.alelr.Reader> getLogicalReaders();
    Iterable<TagReadEvent> getEvents(int lastNSeconds);
    Iterable<TagReadEvent> getEvents(String logicalReaderName, int lastNSeconds);
    boolean start(String compositeReaderName);
    boolean stop(String compositeReaderName);
    /**
	 * returns the vendor version of the ale (see 10.3 API).
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @return vendor version of the ale
	 */
	String getVendorVersion()  throws ImplementationExceptionResponse;

	/**
	 * returns the standard version of the ale (see 10.3 API).
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @return standard version of the ale
	 */
	String getStandardVersion() throws ImplementationExceptionResponse;

	/**
	 * returns the current value of the specified property for reader name (see 10.3 API).
	 * @param name the reader the property value is requested
	 * @param propertyName the property that for the value is requested
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @return returns a value for a requested property
	 */
	String getPropertyValue(String name,  String propertyName) throws NoSuchNameExceptionResponse, SecurityException, ImplementationExceptionResponse;

	/**
	 * changes properties for the reader name (see 10.3 API). Null value for the properties are not allowed.
	 * @param name name of the reader to change
	 * @param properties new properties for the reader
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 */
	void setProperties(String name, List<LRProperty> properties) throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse, ImmutableReaderExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;

	/**
	 * removes the specified logical readers from the components of the composite reader (see 10.3 API).
	 * @param name name of the composite reader
	 * @param readers list of readers to be removed
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @throws NonCompositeReaderExceptionResponse addReader or setReader or removeReader was called on a non compositeReader
	 */
	void removeReaders(String name, java.util.List<String> readers) throws NoSuchNameExceptionResponse, InUseExceptionResponse, ImmutableReaderExceptionResponse, NonCompositeReaderExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;

	/**
	 * changes the list of readers in a composite reader (see 10.3 API).
	 * @param name name of the composite reader
	 * @param readers list of readers to be changed
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws ReaderLoopExceptionResponse the reader would include itself which would result in a loop
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @throws NonCompositeReaderExceptionResponse addReader or setReader was called on a non compositeReader
	 */
	void setReaders(String name, java.util.List<String> readers)  throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse, ImmutableReaderExceptionResponse, NonCompositeReaderExceptionResponse, ReaderLoopExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;

	/**
	 * adds the specified logical readers to a composite reader (see 10.3 API).
	 * @param name name of the composite reader
	 * @param readers list of readers to be added to the composite reader
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws ReaderLoopExceptionResponse the reader would include itself which would result in a loop
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @throws NonCompositeReaderExceptionResponse the reader is not composite.
	 */
	void addReaders(String name, java.util.List<String> readers) throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse, ImmutableReaderExceptionResponse, ReaderLoopExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse, NonCompositeReaderExceptionResponse;
	
	/**
	 * returns an LRSpec that describes a logical reader called name (see 10.3 API).
	 * @param name name of the logical reader
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 * @return LRSpec for the logical reader name
	 */
	LRSpec getLRSpec(String name) throws NoSuchNameExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;

	/**
	 * returns a list of the logical readers in the reader (see 10.3 API).
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation 
	 * @return list of String containing the logicalReaders
	 */
	java.util.List<String> getLogicalReaderNames() throws SecurityExceptionResponse, ImplementationExceptionResponse;

	/**.
	 * removes the logicalReader name (see 10.3 API).
	 * @param name name for the logical reader to be undefined
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws ImplementationExceptionResponse whenever an internal error occurs
	 */
	void undefine(String name) throws NoSuchNameExceptionResponse, InUseExceptionResponse, SecurityExceptionResponse, ImmutableReaderExceptionResponse, ImplementationExceptionResponse;

	/** 
	 * Changes the definition of the logical reader named name to
	 *  match the specification in the spec parameter. This is
	 *  different than calling undefine followed by define, because
	 *  update may be called even if there are defined ECSpecs, 
	 *  CCSpecs, or other logical readers that refer to this 
	 *  logical reader. 
	 * @param name a valid name for the reader to be changed.
	 * @param spec an LRSpec describing the changes to the reader  
	 * @throws ImmutableReaderExceptionResponse whenever you want to change a immutable reader
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 * @throws InUseExceptionResponse Is thrown when you try to undefine a Reader that is still referenced by EC or CC
	 * @throws ReaderLoopExceptionResponse the reader would include itself which would result in a loop
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation 
	 * @throws NoSuchNameExceptionResponse whenever the specified name is not defined in the logicalReader API
	 */
	void update(String name, LRSpec spec)  throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse,  ImmutableReaderExceptionResponse, ReaderLoopExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;

	/**
	 * creates a new logical Reader according to spec (see 10.3 API). this variant works on jaxb LRSpec
	 * @param name name of the new logicalReader
	 * @param spec LRSpec how to build the reader
	 * @throws DuplicateNameExceptionResponse when a reader name is already defined
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation 
	 */
	//void define(String name, LRSpec spec) throws DuplicateNameExceptionResponse, ValidationExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;
	
	/**
	 * creates a new logical Reader according to spec (see 10.3 API). this variant works directly on LRSpec
	 * @param name name of the new logicalReader
	 * @param spec LRSpec how to build the reader
	 * @throws DuplicateNameExceptionResponse when a reader name is already defined
	 * @throws ValidationExceptionResponse the provided LRSpec is invalid
	 * @throws SecurityExceptionResponse the operation was not permitted due to access restrictions
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation 
	 */
	void define(String name, LRSpec spec) throws DuplicateNameExceptionResponse, ValidationExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse;
			
	
	/**
	 * 
	 * @param reader a logicalReader to be stored in the manager
	 * @throws ImplementationExceptionResponse whenever something goes wrong inside the implementation
	 */
	void setLogicalReader(Reader reader) throws ImplementationExceptionResponse;
	
	/**
	 * This method indicates if the manager contains a logical reader with specified name.
	 * 
	 * @param logicalReaderName to search
	 * @return true if the logical reader exists and false otherwise
	 */
	boolean contains(String logicalReaderName);

}
