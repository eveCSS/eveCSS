/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
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

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class PositioningComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private Combo motorAxisCombo;
	private Button addButton;
	private ScanModul scanModul;
	
	public PositioningComposite( final Composite parent, final int style) {
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
	    column.setText( "Motor Axis" );
	    column.setWidth( 140 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Plugin" );
	    column.setWidth( 60 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
	    column.setText( "Detector Channel" );
	    column.setWidth( 140 );
	    
	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 3 );
	    column.setText( "Normalize Channel" );
	    column.setWidth( 140 );
	    
	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 4 );
	    column.setText( "Parameters" );
	    column.setWidth( 50 );
	    
	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    this.tableViewer.setContentProvider( new PositioningInputWrapper() );
	    this.tableViewer.setLabelProvider( new PositioningLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[5];
	    
	    
	    final List< PlugIn > plugIns = Activator.getDefault().getMeasuringStation().getPlugins();
	    final List< String > positionPlugInNames = new ArrayList< String >();
	    final Iterator< PlugIn > it = plugIns.iterator();
	    while( it.hasNext() ) {
	    	final PlugIn currentPlugin = it.next();
	    	if( currentPlugin.getType() == PluginTypes.POSTSCANPOSITIONING ) {
	    		positionPlugInNames.add( currentPlugin.getName() );
	    	}
	    }
	    final String[] plugins = positionPlugInNames.toArray( new String[0] );
	    // die erlaubten Detektoren werden erst später gesetzt
	    final String[] detectorChannels = {};
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), plugins, SWT.READ_ONLY );
	    editors[2] = new ComboBoxCellEditor( this.tableViewer.getTable(), detectorChannels, SWT.READ_ONLY );
	    editors[3] = new ComboBoxCellEditor( this.tableViewer.getTable(), detectorChannels, SWT.NONE );
	    editors[4] = new PluginParameterButtonCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellModifier( new PositioningCellModifyer( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "axis", "plugin", "channel", "normalize", "parameter" };
	    
	    this.tableViewer.setColumnProperties( props );
	    
	    this.motorAxisCombo = new Combo(this, SWT.NONE);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.motorAxisCombo.setLayoutData( gridData );
		
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
				
				if( !motorAxisCombo.getText().equals( "" ) ) {
					final MotorAxis motorAxis = (MotorAxis)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( motorAxisCombo.getText() );
					
					if( scanModul != null ) {
						Positioning[] positionings = scanModul.getPositionings();
						for( int i = 0; i < positionings.length; ++i ) {
							if( positionings[i].getMotorAxis() == motorAxis ) {
								return;
							}
						}
						Positioning positioning = new Positioning( motorAxis );
						scanModul.add( positioning );

						// Table Eintrag wird aus der Combo-Box entfernt
						motorAxisCombo.remove(motorAxisCombo.getText());
					}
					tableViewer.refresh();
				}
				
			}
			
		});
		
		   Action deleteAction = new Action(){
		    	public void run() {
		    		
		    		Positioning weg = (Positioning)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
		    		scanModul.remove( weg );
		    		// Eintrag wird in die ComboBox wieder hinzugefügt
		    		motorAxisCombo.add(weg.getAbstractDevice().getFullIdentifyer());
		    		// TODO das add in die Liste ist erstmal nicht alphabetisch sortiert
		    		// Lösung: Liste der vorhandenen Einträge durchsuchen und dabei 
		    		// entscheiden an welcher Stelle der Eintrag hinzugefügt werden muß.

		    		tableViewer.refresh();
		    	}
		    };
		    
		    deleteAction.setEnabled( true );
		    deleteAction.setText( "Delete Positioning" );
		    deleteAction.setToolTipText( "Deletes Positioning" );
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

			// Es werden nur die Achsen erlaubt die auch in diesem ScanModul verwendet werden.
			Axis[] cur_axis = scanModul.getAxis();
			String[] cur_feld = new String[cur_axis.length];
			
			for (int i=0; i<cur_axis.length; ++i) {
				cur_feld[i] = cur_axis[i].getMotorAxis().getFullIdentifyer();
			}
			this.motorAxisCombo.setItems(cur_feld);
		
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
