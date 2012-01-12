package de.ptb.epics.eve.editor.wizards;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.editor.Activator;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (scml).
 */
public class NewScanDescriptionWizardPage extends WizardPage {
	
	// the filename / path for the new file
	private Text fileText;
	
	/**
	 * Constructor for SampleNewWizardPage.
	
	 */
	public NewScanDescriptionWizardPage() {
		super("wizardPage");
		setTitle("New Scan Description");
		setDescription("This wizard creates a new Scan Description.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("File name:");
		
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new FileTextModifiedListener());
		
		Button button = new Button(container, SWT.PUSH);
		button.setText("Search...");
		button.addSelectionListener(new ButtonSelectionListener());
		
		String rootdir = Activator.getDefault().getRootDirectory();
		File file = new File(rootdir + "scml/");
		if(file.exists()) {
			fileText.insert(rootdir + "scml/");
		} else {
			fileText.insert(rootdir);
		}
		file = null;
		
		setErrorMessage("File name must be \"/path/to/file/filename.scml\"");
		setPageComplete(false);
		setControl(container);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible) {
			fileText.setSelection(fileText.getText().length());
			fileText.setFocus();
		}
	};
	
	/**
	 * Returns the file name set by the user
	 * 
	 * @return the user selected file name
	 */
	public String getFileName() {
		return fileText.getText();
	}
	
	// ********************** Listeners *******************************
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of <code>fileText</code>.
	 */
	class FileTextModifiedListener implements ModifyListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			
			// read filename from text field
			String fileName = fileText.getText();
			
			// show error message if no file name is given
			if (fileName.length() == 0) {
				setErrorMessage("File name must be specified!");
				setPageComplete(false);
				return;
			}
			
			// show error message if file extension is not scml
			int dotLoc = fileName.lastIndexOf('.');
			if (dotLoc != -1) {
				// suffix is not scml
				String ext = fileName.substring(dotLoc + 1);
				if (!ext.equalsIgnoreCase("scml")) {
					setErrorMessage("Extension must be \"scml\"");
					setPageComplete(false);
					return;
				}
			} else {
				// no suffix set
				setErrorMessage("Extension must be \"scml\"");
				setPageComplete(false);
				return;
			}
			// if none of the above events occurred everything is ok:
			setErrorMessage(null);
			setPageComplete(true);
		}
	}
	
	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of <code>button</code>.
	 */
	class ButtonSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			String filePath;
			String rootdir = Activator.getDefault().getRootDirectory();
			File file = new File(rootdir + "scml/");
			if(file.exists()) {
				filePath = rootdir + "scml/";
			} else {
				filePath = rootdir;
			}
			file = null;
			
			// show a file dialog
			final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
			dialog.setFilterPath(filePath);
			String[] extensions = {"*.scml"};
			dialog.setFilterExtensions(extensions);
			final String fileName = dialog.open();
			
			// set selected file (if one was selected)
			if(fileName != null) {
				int dotLoc = fileName.lastIndexOf('.');
				if(dotLoc != -1) {
					String ext = fileName.substring(dotLoc + 1);
					if (!ext.equalsIgnoreCase("scml")) {
						// file name with dot but not scml
						fileText.setText(fileName + ".scml");
					} else {
						// file name with .scml
						fileText.setText(fileName);
					}
				} else {
					// file name without a dot
					fileText.setText(fileName + ".scml");
				}
			}
		}
	}
}