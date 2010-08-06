package org.rifidi.edge.core.app.api.resources.db;

import java.io.File;
import java.util.HashMap;

import javax.sql.DataSource;

import org.rifidi.edge.core.app.api.resources.CannotCreateResourceException;
import org.rifidi.edge.core.app.api.resources.ResourceService;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * By default the DBResource service will configure Derby at
 * ${org.rifidi.home}/applications/Resources/DB
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DBResourceService extends
		ResourceService<DBResourceDescription, SimpleJdbcTemplate> {

	/** This keeps up with datasources that have been created */
	private HashMap<DBResourceDescription, MetadataUtils> descriptionToMetadataUtils;

	/**
	 * Constructor.
	 */
	public DBResourceService() {
		super();
		this.descriptionToMetadataUtils = new HashMap<DBResourceDescription, MetadataUtils>();
		String derbyHome = System.getProperty("org.rifidi.home") + File.separator
				+ System.getProperty("org.rifidi.edge.applications")
				+ File.separator + "Resources" + File.separator + "DB";

		System.setProperty("derby.system.home", derbyHome);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.resources.ResourceService#createDAO(org.
	 * rifidi.edge.core.app.api.resources.ResourceDescription)
	 */
	@Override
	protected SimpleJdbcTemplate createResource(
			DBResourceDescription resourceDescription)
			throws CannotCreateResourceException {
		DataSource dataSource = resourceDescription.getDataSource();
		MetadataUtils metadata = new MetadataUtils();
		metadata.setDataSource(dataSource);
		descriptionToMetadataUtils.put(resourceDescription, metadata);
		return new SimpleJdbcTemplate(dataSource);

	}

	/**
	 * The MetadataUtils allows access to DB Metadata information. This method
	 * must be called after the resource has been created.
	 * 
	 * @param resourceDescription
	 * @return
	 */
	public MetadataUtils getMetadataUtils(
			DBResourceDescription resourceDescription) {
		if (descriptionToMetadataUtils.containsKey(resourceDescription)) {
			return descriptionToMetadataUtils.get(resourceDescription);
		}

		throw new IllegalStateException("Resource has not been created: "
				+ resourceDescription);
	}

}
