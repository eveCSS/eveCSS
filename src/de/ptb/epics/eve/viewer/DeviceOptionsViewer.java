package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

public class DeviceOptionsViewer extends ViewPart {

	private AbstractDevice device;
	private TableViewer tableViewer;
	private boolean fixed = false;
	
	@Override
	public void createPartControl( final Composite parent ) {
		
		
	    
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		this.tableViewer = new TableViewer( parent );
		
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getControl().setLayoutData( gridData );
		
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "Option" );
	    column.setWidth( 140 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Value" );
	    column.setWidth( 60 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    this.tableViewer.setContentProvider( new DeviceOptionsContentProvider() );
	    this.tableViewer.setLabelProvider( new DeviceOptionsLabelProvider() );
	    //tableViewer.new ColorAndFontCollectorWithProviders(new OptionColorProvider());
	    //tableViewer.getColorAndFontCollector();
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = null;
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellModifier( new DeviceOptionsModifier( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "option", "value" };
	    
	    this.tableViewer.setColumnProperties( props );
	    
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public AbstractDevice getDevice() {
		return this.device;
	}
	
	public void setDevice( final AbstractDevice device ) {
		this.device = device;
		this.tableViewer.setInput( device );
		this.setPartName( device.getFullIdentifyer() );
	}

	public boolean isFixed() {
		return this.fixed;
	}

	public void setFixed( final boolean fixed ) {
		this.fixed = fixed;
		
	}
}
