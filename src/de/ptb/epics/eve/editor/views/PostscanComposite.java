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
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class PostscanComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private Combo postscanCombo;
	private Button addButton;
	private ScanModul scanModul;
	
	public PostscanComposite( final Composite parent, final int style) {
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
	    column.setWidth( 100 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
	    column.setText( "Reset Original" );
	    column.setWidth( 80 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen Postscans des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new PostscanInputWrapper() );
	    this.tableViewer.setLabelProvider( new PostscanLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[3];
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[2] = new CheckboxCellEditor( );
	    
	    this.tableViewer.setCellModifier( new PostscanCellModifyer( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "device", "value", "reset"};
	    
	    this.tableViewer.setColumnProperties( props );
	    
	    this.postscanCombo = new Combo(this, SWT.NONE);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.postscanCombo.setLayoutData( gridData );
		
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
				
				if( !postscanCombo.getText().equals( "" ) ) {

					AbstractPrePostscanDevice device = (AbstractPrePostscanDevice) Activator
							.getDefault().getMeasuringStation()
							.getAbstractDeviceByFullIdentifyer(
										postscanCombo.getText());

					Postscan postscan = new Postscan ();
					postscan.setAbstractPrePostscanDevice(device);
					
					scanModul.add(postscan);
					// Table Eintrag wird aus der Combo-Box entfernt
					postscanCombo.remove(postscanCombo.getText());
					tableViewer.refresh();
				}
			}
		});
		
		   Action deleteAction = new Action(){
		    	public void run() {
		    		
		    		scanModul.remove( (Postscan)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );
		    		// ComboBos muß aktualisiert werden
		    		// alle Postscans werden in die ComboBox eingetragen und die
		    		// gesetzten postscans wieder ausgetragen
		    		postscanCombo.setItems( Activator.getDefault().getMeasuringStation().getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ) );
					Postscan[] postscans = scanModul.getPostscans();
					for (int i = 0; i < postscans.length; ++i) {
						// Postscan Eintrag wird aus der Combo-Box entfernt
						postscanCombo.remove(postscans[i].getAbstractPrePostscanDevice().getFullIdentifyer());
					}
		    		tableViewer.refresh();
		    	}
		    };
		    
		    deleteAction.setEnabled( true );
		    deleteAction.setText( "Delete Postscan" );
		    deleteAction.setToolTipText( "Deletes Postscan" );
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

			this.postscanCombo.setItems( Activator.getDefault().getMeasuringStation().getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ) );
			Postscan[] postscans = scanModul.getPostscans();
			for (int i = 0; i < postscans.length; ++i) {
				// Postscan Eintrag wird aus der Combo-Box entfernt
				this.postscanCombo.remove(postscans[i].getAbstractPrePostscanDevice().getFullIdentifyer());
			}
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
