/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.model;

import java.util.ArrayList;

/**
 * This is a singleton that creates a single model to use for views and
 * controllers to use to access the tags that have been seen.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagModelProviderSingleton {

	/** A static reference to this class */
	private static TagModelProviderSingleton model;
	/** The tags */
	private TagList tags;

	/**
	 * A private constructor since this is a singleton.
	 */
	private TagModelProviderSingleton() {
		tags = new TagList(new ArrayList<TagModel>(), TagModel.class);
	}

	/**
	 * A public access to use to get a hold of this singleton
	 * 
	 * @return
	 */
	public static synchronized TagModelProviderSingleton getInstance() {
		if (model == null) {
			model = new TagModelProviderSingleton();
		}
		return model;
	}

	/**
	 * Get the tags that have been seen
	 * 
	 * @return
	 */
	public TagList getTags() {
		return tags;
	}

}
