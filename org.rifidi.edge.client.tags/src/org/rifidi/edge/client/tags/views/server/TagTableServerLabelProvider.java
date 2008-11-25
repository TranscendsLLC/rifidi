package org.rifidi.edge.client.tags.views.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.client.tags.utils.TagContainer;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.readerplugin.messages.impl.EnhancedTagMessage;


public class TagTableServerLabelProvider implements ITableLabelProvider , ITableColorProvider {
	static private Log logger = LogFactory.getLog(TagTableServerLabelProvider.class);
	

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		//logger.debug(element.getClass().getSimpleName() + ":" + columnIndex);
		
		TagContainer tm = (TagContainer) element;
		
		switch (columnIndex){
			case 0:
				return ByteAndHexConvertingUtility.toHexString(tm.getTag().getId()).replace(" ", "");
			case 1:
				return Long.toString(tm.getTag().getLastSeenTime());
			case 2:
				if (tm.getTag() instanceof EnhancedTagMessage) {
					int antenna = ((EnhancedTagMessage) tm.getTag()).getAntennaId();
					if (antenna >= 0) { 
						return Integer.toString(antenna);
					} else {
						return "(No Data)";
					}
				} else {
					return "(No Data)";
				}
			case 3:
				if (tm.getTag() instanceof EnhancedTagMessage) {
					float signalStrength = ((EnhancedTagMessage) tm.getTag()).getSignalStrength();
					if (!Float.isNaN(signalStrength) ) {
						return Float.toString(Math.abs(signalStrength));
					} else {
						return "(No Data)";
					}
				} else {
					return "(No Data)";
				}
			case 4:
				if (tm.getTag() instanceof EnhancedTagMessage) {
					float velocity = ((EnhancedTagMessage) tm.getTag()).getVelocity();
					if (!Float.isNaN(velocity) ) {
						return Float.toString(Math.abs(velocity));
					} else {
						return "(No Data)";
					}
				} else {
					return "(No Data)";
				}
		}
		return "";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		TagContainer tm = (TagContainer) element;
		if (tm.getTag() instanceof EnhancedTagMessage) {
			Float velocity = ((EnhancedTagMessage) tm.getTag()).getVelocity();
			//TODO add epsilon
			if ( velocity < -0.5){
				return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			} else if (velocity > 0.5){
				return Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
			} else {
				return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
			}
			
		} else {
			return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		}
		
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return Display.getCurrent().getSystemColor(0);
	}

}
