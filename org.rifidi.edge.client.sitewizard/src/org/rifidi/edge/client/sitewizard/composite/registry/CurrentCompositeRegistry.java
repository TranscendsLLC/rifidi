/* 
 * CurrentCompositeRegistry.java
 *  Created:	Aug 6, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite.registry;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.rifidi.edge.client.sitewizard.composite.DataComposite;
import org.rifidi.edge.client.sitewizard.composite.DisplayDataComposite;
import org.rifidi.edge.client.sitewizard.composite.MasterComposite;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;
import org.rifidi.edge.client.sitewizard.creator.SiteCreatorInterface;

/**
 * This is a registry for all the composites.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class CurrentCompositeRegistry {

	/**
	 * 
	 */
	private static Log logger = LogFactory
			.getLog(CurrentCompositeRegistry.class);

	/**
	 * 
	 */
	private static CurrentCompositeRegistry instance = new CurrentCompositeRegistry();

	/**
	 * 
	 */
	private ArrayList<DataComposite> compositeList = null;

	/**
	 * 
	 */
	private int currentIndex = -1;

	/**
	 * 
	 */
	private CurrentCompositeRegistry() {
		compositeList = new ArrayList<DataComposite>();
	}

	/**
	 * @return the instance
	 */
	public static CurrentCompositeRegistry getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return
	 */
	public DataComposite getCurrentComposite() {
		return compositeList.get(currentIndex);
	}

	/**
	 * @param comp
	 */
	public void setFirstComposite(DataComposite comp) {
		logger.debug("Setting the first DataComposite");

		compositeList.add(comp);

		currentIndex = 0;
	}

	/**
	 * Clears the composite list after the given index. If there is nothing
	 * after the given index, or if the given index does not exist, this method
	 * does nothing.
	 * 
	 * @param index
	 */
	public void clearCompositeListAfterIndex(int index) {
		int temp = index + 1;
		while (compositeList.get(temp) != null) {
			compositeList.remove(temp);
		}
	}

	/**
	 * 
	 * 
	 * @param comp
	 * @param selection
	 * @return
	 */
	public void showNextComposite() {
		// Do this if the current composite is the display composite, meaning
		// that pressing next means we want to create the site
		if (this.compositeList.get(currentIndex) instanceof DisplayDataComposite) {
			SiteCreatorInterface.getInstance().createBatchFile(
					SiteWizardData.getInstance().getName());
			if (SiteWizardData.getInstance().isCustom()) {
				logger.debug("creating a custom site: "
						+ SiteWizardData.getInstance().getName());
				SiteCreatorInterface.getInstance().createConfigFile(
						SiteWizardData.getInstance().getName(),
						SiteWizardData.getInstance().getReaders(),
						SiteWizardData.getInstance().getTemplate()
								.getIncludeList());
				logger.debug("done creating a custom site");
			} else {
				logger.debug("creating a default site: "
						+ SiteWizardData.getInstance().getName());
				SiteCreatorInterface.getInstance().createConfigFile(
						SiteWizardData.getInstance().getName(),
						SiteWizardData.getInstance().getDefaultReaders(),
						SiteWizardData.getInstance().getDefaultTemplate()
								.getIncludeList());
				logger.debug("done creating a default site");
			}
		}

		logger.debug("getting the next datacomposite");

		DataComposite curComp = compositeList.get(currentIndex);

		logger.debug("Ending the current composite");
		MasterComposite.getInstance().resetMasterComposite();
		MasterComposite.getInstance().layout();

		DataComposite possibleComposite = curComp.getNextDataComposite();

		DataComposite nextComposite = null;
		try {
			nextComposite = compositeList
					.get(compositeList.indexOf(curComp) + 1);
		} catch (IndexOutOfBoundsException e) {
			// Do nothing
		}

		boolean exists = false;

		logger.debug("right before the if/else block");
		if (nextComposite != null) {
			if (!possibleComposite.getClass().equals(nextComposite.getClass())) {
				logger.debug("Clearing the list after this index");
				this.clearCompositeListAfterIndex(compositeList
						.indexOf(curComp));
				logger.debug("adding the possible composite: "
						+ possibleComposite.toString());
				compositeList.add(possibleComposite);

				this.currentIndex = compositeList.indexOf(possibleComposite);
			} else {
				logger.debug("Got into the else, simply"
						+ " incrementing the current index");

				// TODO: Who knows what is going on here? I can't be bothered to
				// figure this out, but when I changed this it fixed an error I
				// was getting.

				MasterComposite.getInstance().resetMasterComposite();
				MasterComposite.getInstance().layout();

				exists = true;
				this.currentIndex++;
				compositeList.set(currentIndex, compositeList.get(currentIndex)
						.remakeDataComposite(
								MasterComposite.getInstance()
										.getMasterComposite(), SWT.NONE));
			}
		} else {
			logger.debug("adding the possible composite: "
					+ possibleComposite.toString());
			compositeList.add(possibleComposite);
			this.currentIndex = compositeList.indexOf(possibleComposite);
		}

		this.compositeList.get(currentIndex).updateButtons();

		logger.debug("after the if/else block");

		this.compositeList.get(currentIndex).setParent(
				MasterComposite.getInstance().getMasterComposite());
		MasterComposite.getInstance().layout();

		if (exists) {
			logger.debug("filling out info, name is: "
					+ SiteWizardData.getInstance().getName());
			this.compositeList.get(currentIndex).fillOutInfo();
			this.compositeList.get(currentIndex).updateButtons();
		}
	}

	/**
	 * 
	 */
	public void showPrevComposite() {
		MasterComposite.getInstance().resetMasterComposite();
		if (currentIndex == 0) {
			logger.error("Can't go previous when you are at the first screen");
		} else {
			currentIndex--;
			compositeList.set(currentIndex, compositeList.get(currentIndex)
					.remakeDataComposite(
							MasterComposite.getInstance().getMasterComposite(),
							SWT.NONE));
			compositeList.get(currentIndex).fillOutInfo();
			compositeList.get(currentIndex).updateButtons();
		}
		MasterComposite.getInstance().layout();
	}
}
