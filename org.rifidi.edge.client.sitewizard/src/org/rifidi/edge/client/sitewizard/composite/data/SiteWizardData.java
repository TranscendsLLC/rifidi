/* 
 * SiteWizardData.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rifidi.edge.client.sitewizard.creator.ReaderObject;
import org.rifidi.edge.client.sitewizard.reader.content.ReaderObjectList;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContent;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContentList;

/**
 * The site wizard data singleton. This class stores all of the data that the
 * site wizard needs to work.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteWizardData {
	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(SiteWizardData.class);

	/**
	 * 
	 */
	private static SiteWizardData instance = new SiteWizardData();

	/**
	 * 
	 */
	private boolean custom = false;

	/**
	 * 
	 */
	private TemplateContent template = null;

	/**
	 * 
	 */
	private TemplateContent defaultTemplate = null;

	/**
	 * 
	 */
	private Set<ReaderObject> readers = null;

	/**
	 * 
	 */
	private List<ReaderObject> defaultReaders = null;

	/**
	 * 
	 */
	private IStructuredSelection templateSelection = null;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private String desc;

	/**
	 * 
	 */
	private String IP;

	/**
	 * 
	 */
	private String port;

	/**
	 * 
	 */
	private SiteWizardData() {
		readers = new HashSet<ReaderObject>();

		defaultReaders = ReaderObjectList.getInstance()
				.getReaderObjectArrayList();

		defaultTemplate = TemplateContentList.CORE;

		name = "";

		desc = "";

		IP = "127.0.0.1";

		port = "40000";
	}

	/**
	 * @return the template
	 */
	public TemplateContent getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(TemplateContent template) {
		this.template = template;
	}

	/**
	 * @return the defaultTemplate
	 */
	public TemplateContent getDefaultTemplate() {
		return defaultTemplate;
	}

	/**
	 * @param defaultTemplate
	 *            the defaultTemplate to set
	 */
	public void setDefaultTemplate(TemplateContent defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	/**
	 * @return the defaultReaders
	 */
	public List<ReaderObject> getDefaultReaders() {
		return defaultReaders;
	}

	/**
	 * @param defaultReaders
	 *            the defaultReaders to set
	 */
	public void setDefaultReaders(ArrayList<ReaderObject> defaultReaders) {
		this.defaultReaders = defaultReaders;
	}

	/**
	 * @return the instance
	 */
	public static SiteWizardData getInstance() {
		return instance;
	}

	/**
	 * @return the custom
	 */
	public boolean isCustom() {
		return custom;
	}

	/**
	 * @param custom
	 *            the custom to set
	 */
	public void setCustom(boolean custom) {
		this.custom = custom;
	}

	/**
	 * @return the readers
	 */
	public ArrayList<ReaderObject> getReaders() {
		ArrayList<ReaderObject> retVal = new ArrayList<ReaderObject>();

		for (ReaderObject ro : readers) {
			retVal.add(ro);
		}

		return retVal;
	}

	/**
	 * 
	 * @param rc
	 */
	public void addReaderObject(ReaderObject rc) {
		logger.debug("adding a reader object" + rc.getName());
		readers.add(rc);
		logger.debug("readers.size = " + readers.size());
	}

	/**
	 * 
	 * @param rc
	 */
	public void removeReaderObject(ReaderObject rc) {
		logger.debug("removing a reader object" + rc.getName());
		readers.remove(rc);
		logger.debug("readers.size = " + readers.size());
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * @return the iP
	 */
	public String getIP() {
		return IP;
	}

	/**
	 * @param ip
	 *            the iP to set
	 */
	public void setIP(String ip) {
		IP = ip;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 */
	public void resetToDefault() {
		// TODO: implement this
	}

	/**
	 * @param templateSelection
	 *            the templateSelection to set
	 */
	public void setTemplateSelection(IStructuredSelection templateSelection) {
		this.templateSelection = templateSelection;
	}

	/**
	 * @return the templateSelection
	 */
	public IStructuredSelection getTemplateSelection() {
		return templateSelection;
	}
}
