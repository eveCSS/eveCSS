/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;

public class PluginParameterButtonCellEditor extends DialogCellEditor {

	public PluginParameterButtonCellEditor( final Composite parent ) {
		super( parent );
		
	}
	
	@Override
	protected Button createButton( final Composite parent ) {
		final Button button = new Button( parent, SWT.PUSH ); 
		button.setText( "Edit" );
		return button;
	}
	
	@Override
	protected Object openDialogBox( final Control cellEditorWindow ) {
		PluginControllerDialog dialog = new PluginControllerDialog( null, (PluginController)this.getValue() );
		dialog.setBlockOnOpen( true );
		dialog.open();
		return null;
	}
	
}
