/**
 * 
 */
package org.rifidi.edge.build.views;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class MainView extends ViewPart {

	public static final String ID = "org.rifidi.edge.build.views.MainView";
	private String path;
	private Text selectedDirectory;
	private Button saveConfigButton;
	private Button endButton;
	private Text configFileText;
	private Composite bottomComposite;
	private Button checkDefaultRunlevel;

	/**
	 * 
	 */
	public MainView() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		FillLayout layout = new FillLayout();
		parent.setLayout(layout);

		Composite mainComposite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = SWT.FILL;
		gridLayout.verticalSpacing = SWT.FILL;
		gridLayout.numColumns = 2;
		mainComposite.setLayout(gridLayout);

		configFileText = new Text(mainComposite, SWT.MULTI | SWT.BORDER
				| SWT.WRAP);
		GridData configFileTextGridData = new GridData(GridData.FILL_BOTH);
		configFileTextGridData.horizontalSpan = 2;
		configFileText.setLayoutData(configFileTextGridData);

		selectedDirectory = new Text(mainComposite, SWT.BORDER);
		selectedDirectory.setEditable(false);
		GridData selectedDirectoryGridData = new GridData(
				GridData.FILL_HORIZONTAL);
		selectedDirectory.setLayoutData(selectedDirectoryGridData);

		Button openDirectoryButton = new Button(mainComposite, SWT.PUSH);
		openDirectoryButton.setText("select Directory");

		openDirectoryButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectDirectoryName();
				generateConfig();
			}
		});

		
		bottomComposite = new Composite(mainComposite, SWT.NONE);
		GridData bcgd = new GridData();
		bcgd.horizontalSpan = 2;
		bcgd.horizontalAlignment = SWT.FILL;
		bcgd.grabExcessHorizontalSpace = true;
		bottomComposite.setLayoutData(bcgd);
		GridLayout bcgl = new GridLayout(3,false);
		
		bottomComposite.setLayout(bcgl);
		
		checkDefaultRunlevel = new Button(bottomComposite,SWT.CHECK);
		checkDefaultRunlevel.setText("start");
		
		saveConfigButton = new Button(bottomComposite, SWT.PUSH);
		saveConfigButton.setText("save config");
		saveConfigButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				saveToFile();
			}
		});

		endButton = new Button(bottomComposite, SWT.PUSH);
		endButton.setText("end");
		endButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);

			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	protected void selectDirectoryName() {
		DirectoryDialog directoryDialog = new DirectoryDialog(getSite()
				.getShell(), SWT.OPEN);
		if (path != null && !path.isEmpty())
			directoryDialog.setFilterPath(path);
		directoryDialog
				.setMessage("Select the directory your plugins are located");
		directoryDialog.setText("Select Plugins Directory");
		path = directoryDialog.open();
		selectedDirectory.setText(path);
	}

	protected void generateConfig() {
		if (path != null && path.isEmpty()) {
			MessageBox messageBox = new MessageBox(getSite().getShell(),
					SWT.ICON_ERROR);
			messageBox.setText("Error");
			messageBox.setMessage("No Directory selected");
			return;
		}
		File directory = new File(path);
		if (directory.isDirectory()) {
			configFileText.setText("");
			boolean fistrun = true;
			configFileText.append("osgi.bundles.defaultStartLevel=1\n");
			configFileText.append("eclipse.ignoreApp=true\n");
			configFileText.append("osgi.bundles=");
			for (File f : directory.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return pathname.isFile()
							&& pathname.getName().endsWith(".jar");
				}
			})) {

				if (fistrun) {
					fistrun = false;
				} else {
					configFileText.append(",");
				}
				configFileText.append("plugins/" + f.getName());
				if(checkDefaultRunlevel.getSelection())
				{
					configFileText.append("@start");
				}
			}
		}
	}

	protected void saveToFile() {
		FileDialog fileDialog = new FileDialog(getSite().getShell(), SWT.SAVE);
		if (path != null && !path.isEmpty())
			fileDialog.setFilterPath(path);
		fileDialog.setFilterExtensions(new String[] { "ini" });
		fileDialog.setFileName("config.ini");
		String savepath = fileDialog.open();
		if (savepath != null && !savepath.isEmpty()) {
			File saveFile = new File(savepath);
			if (saveFile.exists()) {
				MessageBox messageBox = new MessageBox(getSite().getShell(),
						SWT.ICON_WARNING | SWT.YES | SWT.CANCEL);
				messageBox.setText("File already exists");
				messageBox.setMessage("File " + saveFile.getName()
						+ " already exists. \n Do you want to override?");
				if (messageBox.open() != SWT.OK) {
					return;
				}
			}
			FileWriter writer;
			try {
				writer = new FileWriter(saveFile);
				writer.write(configFileText.getText());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
