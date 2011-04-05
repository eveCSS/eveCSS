/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public class PluginControllerComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private PluginController pluginController;
	private ScanModule scanModul;
	private PluginControllerLabelProvider pluginControllerLabelProvider;
	
	public PluginControllerComposite( final Composite parent, final int style) {
		super( parent, style );
		initialize();
	}
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
	

	}

	private void initialize() {
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		
		this.setLayout( gridLayout );
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer( this, SWT.NONE );
		this.tableViewer.getControl().setLayoutData( gridData );
		
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "Option" );
	    column.setWidth( 140 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Value" );
	    column.setWidth( 60 );
	    
	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    this.tableViewer.setContentProvider( new PluginControllerInputWrapper() );
	    this.pluginControllerLabelProvider = new PluginControllerLabelProvider(); 
	    this.tableViewer.setLabelProvider( this.pluginControllerLabelProvider );
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    final String[] props = { "option", "value" };
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellEditors( editors );
	    this.tableViewer.setCellModifier( new PluginControllerCellModifyer( this.tableViewer ) );
	    this.tableViewer.setColumnProperties( props );
		
	}
	
	public void setPluginController( final PluginController pluginController ) {
		this.pluginController = pluginController;
		this.pluginControllerLabelProvider.setPluginController( pluginController );
		this.tableViewer.setInput( pluginController );
	}
	
	public PluginController getPluginController() {
		return this.pluginController;
	}

	public ScanModule getScanModul() {
		return this.scanModul;
	}
	
	public void setScanModul( final ScanModule scanModul ) {
		this.scanModul = scanModul;
		pluginController.setScanModul(scanModul);
	}
	
}
