/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;
import java.util.Iterator;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class ErrorView extends ViewPart implements IModelUpdateListener {

	public static final String ID = "de.ptb.epics.eve.editor.views.ErrorView"; // TODO Needs to be whatever is mentioned in plugin.xml  //  @jve:decl-index=0:

	private Composite top = null;

	private Table errorTable = null;
	
	private ScanDescription currentScanDescription;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
			return;
		}
		// TODO Auto-generated method stub
		top = new Composite(parent, SWT.NONE);
		top.setLayout(new FillLayout());
		errorTable = new Table(top, SWT.NONE);
		errorTable.setHeaderVisible(true);
		errorTable.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(errorTable, SWT.NONE);
		tableColumn.setWidth(60);
		tableColumn.setText("Level");
		TableColumn tableColumn1 = new TableColumn(errorTable, SWT.NONE);
		tableColumn1.setWidth(60);
		tableColumn1.setText("Location");
		TableColumn tableColumn2 = new TableColumn(errorTable, SWT.NONE);
		tableColumn2.setWidth(60);
		tableColumn2.setText("Description");
	}

	@Override
	public void setFocus() {
		

	}
	
	public ScanDescription getScanDescription() {
		return this.currentScanDescription;
	}
	
	public void setScanDescription( final ScanDescription scanDescription ) {
		if( currentScanDescription != null ) {
			this.currentScanDescription.removeModelUpdateListener( this );
			
		}
		this.currentScanDescription = scanDescription;
		
		TableItem[] tableItems = this.errorTable.getItems();
		for( int i = 0; i < tableItems.length; ++i ) {
			tableItems[i].dispose();
		}
		
		this.errorTable.removeAll();
		
		if( currentScanDescription != null ) {
			this.currentScanDescription.addModelUpdateListener( this );
			final Iterator< IModelError > it = this.currentScanDescription.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				TableItem tableItem = new TableItem( this.errorTable, 0 );
				tableItem.setData( modelError );
				tableItem.setText( 0, "" );
				tableItem.setText( 1, modelError.toString() );
				tableItem.setText( 2, "" );
			}
		}
	}

	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		
		this.errorTable.removeAll();
		
		if( currentScanDescription != null ) {
			final Iterator< IModelError > it = this.currentScanDescription.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				TableItem tableItem = new TableItem( this.errorTable, 0 );
				tableItem.setData( modelError );
				tableItem.setText( 0, "" );
				tableItem.setText( 1, modelError.toString() );
				tableItem.setText( 2, "" );
			}
		}
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10,533,201"
