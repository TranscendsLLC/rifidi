/**
 * 
 */
package org.rifidi.edge.client.sal.properties.mbeanwidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractStringWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;

/**
 * @author kyle
 * 
 */
public class FlatFormStringMbeanWidget<T extends StringWidgetData> extends
		AbstractStringWidget<T> {

	private TabbedPropertySheetWidgetFactory factory;
	private boolean dirty;

	public FlatFormStringMbeanWidget(T data,
			TabbedPropertySheetWidgetFactory factory) {
		super(data);
		this.factory = factory;
		this.dirty = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget
	 * #createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		this.text = factory.createText(parent, "");
		this.text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		// notify listeners of a user typing a key
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.character != SWT.CR) {
					dirty = true;
					notifyListenersKeyReleased();
				} else {
					if (dirty == true) {
						dirty = false;
						notifyListenersDataChanged(text.getText());
					}
				}

			}
		});
	}

}
