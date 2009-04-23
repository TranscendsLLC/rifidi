/**
 * 
 */
package org.rifidi.edge.client.alelr;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.InUseExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.alelr.decorators.LRSpecDecorator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ALELRService extends IPropertyChangeListener {

	/**
	 * Register a listener for changes to the ALE LR stub.
	 * 
	 * @param listener
	 */
	void registerALELRListener(ALELRListener listener);

	/**
	 * Unregister a listener for changes to the ALE LR stub.
	 * 
	 * @param listener
	 */
	void unregisterALELRListener(ALELRListener listener);

	/**
	 * Register a viewer to the ALELR model.
	 * 
	 * @param viewer
	 */
	void registerALELRViewer(Viewer viewer);

	/**
	 * Unregister a viewer to the ALELR model.
	 * 
	 * @param viewer
	 */
	void unregisterALELRViewer(Viewer viewer);

	/**
	 * Get a list containing the names of currently available logical readers.
	 */
	List<String> getAvailableReaderNames();

	/**
	 * Delete the given reader.
	 * 
	 * @param reader
	 * @throws InUseExceptionResponse
	 * @throws NoSuchNameExceptionResponse
	 */
	void deleteReader(LRSpecDecorator reader) throws InUseExceptionResponse,
			NoSuchNameExceptionResponse;

	/**
	 * Create a ew logical reader.
	 * 
	 * @param name
	 * @param readerNames
	 * @throws DuplicateNameExceptionResponse
	 * @throws ValidationExceptionResponse
	 */
	void createReader(String name, List<String> readerNames)
			throws DuplicateNameExceptionResponse, ValidationExceptionResponse;
}
