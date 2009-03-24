/**
 * 
 */
package org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Attribute;

import org.eclipse.swt.widgets.Text;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;

/**
 * This is a convenience abstract class to use for widgets who want to display a
 * string widget as a Text control. Implementors will still need to build the
 * control
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractStringWidget<T extends StringWidgetData> extends
		AbstractWidget<T> {

	/** The Text control */
	protected Text text;
	/** The regular expression */
	private Pattern regexPattern;
	/** The regular expression matcher object used for validation */
	private Matcher matcher;

	/**
	 * @param data
	 */
	public AbstractStringWidget(T data) {
		super(data);
		String regexString = data.getRegex();
		if (regexString == null || regexString.equals("")) {
			regexPattern = Pattern.compile("(.)*");
		} else {
			regexPattern = Pattern.compile(regexString);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#disable()
	 */
	@Override
	public void disable() {
		this.text.setEnabled(false);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#enable()
	 */
	@Override
	public void enable() {
		if (data.isEditable()) {
			this.text.setEnabled(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#getValue()
	 */
	@Override
	public Attribute getAttribute() {
		return new Attribute(getElementName(), text.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#setValue(java
	 * .lang.String)
	 */
	@Override
	public String setValue(Object value) {
		text.setText((String)value);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget#validate()
	 */
	@Override
	public String validate() {
		if (regexPattern != null) {
			if (matcher == null) {
				matcher = regexPattern.matcher(text.getText());
			} else {
				matcher.reset(text.getText());
			}
			if (!matcher.matches()) {
				return data.getDisplayName() + " is invalid";
			}
		}
		return null;
	}

}
