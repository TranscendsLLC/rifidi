package org.rifidi.dynamicswtforms.ui.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.widgets.data.AbstractWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.listeners.DynamicSWTWidgetListener;

/**
 * An abstract implementation of a Widget. Most widgets should extend this class
 * 
 * @author kyle
 * 
 */
public abstract class AbstractWidget implements DynamicSWTFormWidget {

	/**
	 * The listeners to this widget
	 */
	private ArrayList<DynamicSWTWidgetListener> listners;

	/**
	 * The data used to construct this widget
	 */
	protected AbstractWidgetData data;

	/**
	 * The constructor
	 * 
	 * @param data
	 *            The data that is used to construct this widget
	 */
	public AbstractWidget(AbstractWidgetData data) {
		this.data = data;
		listners = new ArrayList<DynamicSWTWidgetListener>();
	}

	@Override
	public String getElementName() {
		return data.getName();
	}

	@Override
	public void createLabel(Composite parent) {
		Label label = new Label(parent, SWT.None);
		label.setText(data.getDisplayName());

	}

	@Override
	public void addListener(DynamicSWTWidgetListener listener) {
		this.listners.add(listener);

	}

	@Override
	public void removeListener(DynamicSWTWidgetListener listener) {
		this.listners.remove(listener);

	}

	@Override
	public Element getXML() {
		Element e = new Element(data.getName());
		e.setText(this.getValue());
		return e;
	}

	/**
	 * Helper method for subclasses to use. Notify listeners of data changed
	 * 
	 * @param data
	 */
	protected void notifyListenersDataChanged(String data) {
		for (DynamicSWTWidgetListener l : listners) {
			l.dataChanged(data);
		}

	}

	/**
	 * Helper method for subclasses to use. Notify listeners of key released
	 */
	protected void notifyListenersKeyReleased() {
		for (DynamicSWTWidgetListener l : listners) {
			l.keyReleased();
		}
	}

}
