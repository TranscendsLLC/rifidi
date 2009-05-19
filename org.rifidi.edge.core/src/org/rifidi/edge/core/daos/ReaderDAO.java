package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;

/**
 * Interface for Data Access Objects used to access reader objects
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ReaderDAO {
	/**
	 * Get all readers currently available.
	 * 
	 * @return
	 */
	Set<AbstractReader<?>> getReaders();

	/**
	 * Get a certain reader by its id.
	 * 
	 * @param id
	 * @return
	 */
	AbstractReader<?> getReaderByID(String id);

	/**
	 * Get all factories currently available.
	 * 
	 * @return
	 */
	Set<AbstractReaderFactory<?>> getReaderFactories();

	/**
	 * Get a factory by its id.
	 * 
	 * @param id
	 * @return
	 */
	AbstractReaderFactory<?> getReaderFactoryByID(String id);
}
