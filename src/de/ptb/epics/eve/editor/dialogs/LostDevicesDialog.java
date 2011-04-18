/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.dialogs;


import java.util.Iterator;

import org.eclipse.jface.dialogs.TitleAreaDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;

public class LostDevicesDialog extends TitleAreaDialog {

	private Composite top = null;
	private Label filenameLabel = null;
	private ScanDescriptionLoader scanDescriptionLoader;
	
	public LostDevicesDialog( final Shell shell, final ScanDescriptionLoader scanDescriptionLoader) {
		super( shell );
		this.scanDescriptionLoader = scanDescriptionLoader;
	}
	
	protected Control createDialogArea( final Composite parent) {
        Composite area = (Composite) super.createDialogArea( parent );

		this.top = new Composite(parent, SWT.RESIZE);
        this.setTitle( "There are Problems in the loaded scml File(s)");

        final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.horizontalSpacing = 20;
		this.top.setLayout( gridLayout );

		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( "Scan Description File");
		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( "Problem");

		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( " ");
		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( " ");

		String meldung;
		final Iterator<String> warnMeldungen = scanDescriptionLoader.getLostDevices().iterator();

		while( warnMeldungen.hasNext()) {
			meldung = warnMeldungen.next();
			this.filenameLabel = new Label( this.top, SWT.NONE );
			this.filenameLabel.setText( this.scanDescriptionLoader.getFileToLoad().toString());
			this.filenameLabel = new Label( this.top, SWT.NONE );
			this.filenameLabel.setText( meldung);
		}

		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( " ");
		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText( " ");
		this.filenameLabel = new Label( this.top, SWT.NONE );
		this.filenameLabel.setText("Attention: If you save one of these files, you lost some settings!");
        
        return area;
    }
	
}
