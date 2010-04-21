/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class PrescanComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private Combo prescanCombo;
	private Button addButton;
	private ScanModul scanModul;
	
	public PrescanComposite( final Composite parent, final int style) {
		super( parent, style );
		initialize();
	}
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		
	}

	private void initialize() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.setLayout( gridLayout );
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer( this, SWT.NONE );
		this.tableViewer.getControl().setLayoutData( gridData );
		
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "Device" );
	    column.setWidth( 300 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Value" );
	    column.setWidth( 80 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen Prescans des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new PrescanInputWrapper() );
	    this.tableViewer.setLabelProvider( new PrescanLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellModifier( new PrescanCellModifyer( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "device", "value"};
	    
	    this.tableViewer.setColumnProperties( props );
	    
	    this.prescanCombo = new Combo(this, SWT.READ_ONLY);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.prescanCombo.setLayoutData( gridData );
		
		this.addButton = new Button( this, SWT.NONE );
		this.addButton.setText( "Add" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.CENTER;
		this.addButton.setLayoutData( gridData );
		this.addButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {

				if( !prescanCombo.getText().equals( "" ) ) {

					AbstractPrePostscanDevice device = (AbstractPrePostscanDevice) Activator
							.getDefault().getMeasuringStation()
							.getAbstractDeviceByFullIdentifyer(
										prescanCombo.getText());

					Prescan prescan = new Prescan ();
					prescan.setAbstractPrePostscanDevice(device);
					scanModul.add(prescan);

					// Table Eintrag wird aus der Combo-Box entfernt
					prescanCombo.remove(prescanCombo.getText());
					tableViewer.refresh();
				}
			}
		});
	
		   Action deleteAction = new Action(){
		    	public void run() {
	    		
					// Prescan wird aus scanModul ausgetragen
		    		scanModul.remove( (Prescan)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );

		    		// ComboBos muß aktualisiert werden
		    		// alle Prescans werden in die ComboBox eingetragen und die
		    		// gesetzten prescans wieder ausgetragen
		    		prescanCombo.setItems( Activator.getDefault().getMeasuringStation().getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ) );
					Prescan[] prescans = scanModul.getPrescans();
					for (int i = 0; i < prescans.length; ++i) {
						// Prescan Eintrag wird aus der Combo-Box entfernt
						prescanCombo.remove(prescans[i].getAbstractPrePostscanDevice().getFullIdentifyer());
					}
		    		
		    		tableViewer.refresh();
		    	}
		    };
		    
		    deleteAction.setEnabled( true );
		    deleteAction.setText( "Delete Prescan" );
		    deleteAction.setToolTipText( "Deletes Prescan" );
		    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
		    
		    MenuManager manager = new MenuManager();
		    Menu menu = manager.createContextMenu( this.tableViewer.getControl() );
		    this.tableViewer.getControl().setMenu( menu );
		    manager.add( deleteAction );
		
	}
	
	public ScanModul getScanModul() {
		return this.scanModul;
	}
	
	public void setScanModul( final ScanModul scanModul ) {
		if( this.scanModul != null ) {
			this.scanModul.removeModelUpdateListener( this );
		}
		if( scanModul != null ) {
			scanModul.addModelUpdateListener( this );

			this.prescanCombo.setItems( Activator.getDefault().getMeasuringStation().getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ) );
			Prescan[] prescans = scanModul.getPrescans();
			for (int i = 0; i < prescans.length; ++i) {
				// Prescan Eintrag wird aus der Combo-Box entfernt
				this.prescanCombo.remove(prescans[i].getAbstractPrePostscanDevice().getFullIdentifyer());
			}
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
