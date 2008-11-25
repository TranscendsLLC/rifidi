package org.rifidi.edge.client.commands.views.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class ErrorMessageBox extends MessageBox {

	private Exception exception;
	static int style = SWT.ICON_ERROR | SWT.OK;

	public ErrorMessageBox(Shell parent, Exception e) {
		super(parent, style);
		this.exception = e;
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	@Override
	public int open() {
		return super.open();
	}

	@Override
	public void setMessage(String string) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Error occoured while executing Operation\n\n");
		buffer
				.append("The error is as following:  \n"
						+ exception.getMessage());
		super.setMessage(buffer.toString());
	}

}
