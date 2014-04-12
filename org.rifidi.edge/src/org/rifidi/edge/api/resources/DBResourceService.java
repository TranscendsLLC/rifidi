/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.api.resources;

import java.io.File;
import java.util.HashMap;

import javax.sql.DataSource;

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
	 * org.rifidi.edge.api.resources.ResourceService#createDAO(org.
	 * rifidi.edge.api.resources.ResourceDescription)
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
