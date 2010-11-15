package org.rifidi.edge.app.tracking;

/**
 * A Data Access Object that looks up a product name
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ProductsDAO {

	/**
	 * Returns the product name for the given ID
	 * 
	 * @param ID
	 * @return
	 */
	public String getProductName(String ID);
}
