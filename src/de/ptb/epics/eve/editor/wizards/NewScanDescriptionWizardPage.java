package de.ptb.epics.eve.editor.wizards;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FileSelectionDialog;

import de.ptb.epics.eve.editor.Activator;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (scml).
 */

public class NewScanDescriptionWizardPage extends WizardPage {
	
	private Text fileText;

	//private ISelection selection;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewScanDescriptionWizardPage( final ISelection selection ) {
		super("wizardPage");
		setTitle("New Scan Description");
		setDescription("This wizard creates a new Scan Description.");
		//this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl( final Composite parent ) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text( container, SWT.BORDER | SWT.SINGLE );
		GridData gd = new GridData( GridData.FILL_HORIZONTAL );
		fileText.setLayoutData( gd );
		fileText.addModifyListener( new ModifyListener() {
			public void modifyText( final ModifyEvent e ) {
				dialogChanged();
			}
		});
		
		Button button = new Button( container, SWT.PUSH );
		button.setText( "Browse..." );
		button.addSelectionListener( new SelectionAdapter() {
			public void widgetSelected( final SelectionEvent e ) {
				handleBrowse();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		fileText.setText( "" );
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		//ContainerSelectionDialog dialog = new ContainerSelectionDialog(
		//		getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
		//		"Select new file container");

		// als filePath wird das Messplatzverzeichnis gesetzt
		int lastSeperatorIndex = Activator.getDefault().getMeasuringStation().getLoadedFileName().lastIndexOf( File.separatorChar );
		final String filePath = Activator.getDefault().getMeasuringStation().getLoadedFileName().substring( 0, lastSeperatorIndex + 1 ) + "scan/";

		final FileDialog dialog = new FileDialog( getShell(), SWT.SAVE );
		dialog.setFilterPath(filePath);
		final String fileName = dialog.open();
		
		if( fileName != null ) {
			this.fileText.setText( fileName );
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		String fileName = getFileName();

		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		//if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
		//	updateStatus("File name must be valid");
		//	return;
		//}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase("scml") == false) {
				updateStatus("File extension must be \"scml\"");
				return;
			}
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getFileName() {
		return fileText.getText();
	}
}