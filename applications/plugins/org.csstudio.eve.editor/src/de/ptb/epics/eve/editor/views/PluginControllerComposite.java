package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PluginControllerComposite extends Composite {

	private TableViewer tableViewer;
	private PluginController pluginController;
	private ScanModule scanModule;
	private PluginControllerLabelProvider pluginControllerLabelProvider;
	
	/**
	 * Constructs a <code>PluginControllerComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public PluginControllerComposite(final Composite parent, final int style) {
		super(parent, style);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		
		this.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer(this, SWT.NONE);
		this.tableViewer.getControl().setLayoutData(gridData);
		
		// Option column
		TableColumn column = new TableColumn(
				this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Option");
	    column.setWidth(140);

	    // Value column
	    column = new TableColumn(
	    		this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Value");
	    column.setWidth(60);
	    
	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new PluginControllerContentProvider());
	    this.pluginControllerLabelProvider = new PluginControllerLabelProvider(); 
	    this.tableViewer.setLabelProvider(this.pluginControllerLabelProvider);
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    final String[] props = {"option", "value"};
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
	    
	    this.tableViewer.setCellEditors(editors);
	    this.tableViewer.setCellModifier(
	    		new PluginControllerCellModifyer(this.tableViewer));
	    this.tableViewer.setColumnProperties(props);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.PluginController}.
	 * 
	 * @param pluginController the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.PluginController}
	 * 		  that should be set
	 */
	public void setPluginController(final PluginController pluginController) {
		this.pluginController = pluginController;
		this.pluginControllerLabelProvider.setPluginController(pluginController);
		this.tableViewer.setInput(pluginController);
	}
	
	/**
	 * Returns the 
	 * {@link de.ptb.epics.eve.data.scandescription.PluginController}.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.PluginController}
	 */
	public PluginController getPluginController() {
		return this.pluginController;
	}

	/**
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * @return the {@link de.ptb.epics.eve.data.scandescription.ScanModule}
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		this.scanModule = scanModule;
		if(pluginController != null)
			pluginController.setScanModule(scanModule);
	}
}