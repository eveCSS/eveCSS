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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.errornotification.IModelErrorListener;
import de.ptb.epics.eve.data.scandescription.errorhandling.ModelError;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class ErrorView extends ViewPart implements IModelErrorListener, IModelUpdateListener {

	public static final String ID = "de.ptb.epics.eve.editor.views.ErrorView"; // TODO Needs to be whatever is mentioned in plugin.xml  //  @jve:decl-index=0:

	private Composite top = null;

	private Table errorTable = null;
	
	private ScanDescription currentScanDescription;

	@Override
	public void createPartControl(Composite parent) {
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
			this.currentScanDescription.removeModelErrorListener( this );
		}
		this.currentScanDescription = scanDescription;
		
		TableItem[] tableItems = this.errorTable.getItems();
		for( int i = 0; i < tableItems.length; ++i ) {
			((ModelError)tableItems[i].getData()).removeModelUpdateListener( this );
			tableItems[i].dispose();
		}
		
		this.errorTable.removeAll();
		
		if( currentScanDescription != null ) {
			this.currentScanDescription.addModelErrorListener( this );
			final Iterator<ModelError> it = this.currentScanDescription.getFullErrorList().iterator();
			while( it.hasNext() ) {
				final ModelError modelError = it.next();
				modelError.addModelUpdateListener( this );
				TableItem tableItem = new TableItem( this.errorTable, 0 );
				tableItem.setData( modelError );
				tableItem.setText( 0, "" );
				tableItem.setText( 1, modelError.getLocation().getLocationText() );
				tableItem.setText( 2, modelError.getReason().getReasonText() );
			}
		}
	}

	public void errorOccured( final ModelError modelError ) {
		TableItem tableItem = new TableItem( this.errorTable, 0 );
		tableItem.setData( modelError );
		tableItem.setText( 0, "" );
		tableItem.setText( 1, modelError.getLocation().getLocationText() );
		tableItem.setText( 2, modelError.getReason().getReasonText() );
		modelError.addModelUpdateListener( this );
	}

	public void errorSolved( final ModelError modelError ) {
		TableItem[] tableItems = this.errorTable.getItems();
		for( int i = 0; i < tableItems.length; ++i ) {
			if( tableItems[i].getData() == modelError ) {
				this.errorTable.remove( i );
				tableItems[i].dispose();
				break;
			}
		}
		modelError.removeModelUpdateListener( this );
	}

	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		
		if( modelUpdateEvent.getSender() instanceof ModelError ) {
			TableItem[] tableItems = this.errorTable.getItems();
			for( int i = 0; i < tableItems.length; ++i ) {
				if( tableItems[i].getData() == modelUpdateEvent.getSender() ) {
					tableItems[i].setText( 1, ((ModelError)tableItems[i].getData()).getLocation().getLocationText() );
					tableItems[i].setText( 2, ((ModelError)tableItems[i].getData()).getReason().getReasonText() );
				}
			}
		}
		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10,533,201"
