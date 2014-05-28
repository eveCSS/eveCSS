package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModule;

//import de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.ContentProvider;
//import de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.LabelProvider;

//import de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.ValueEditingSupport;

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
		
		this.tableViewer = new TableViewer(this, SWT.BORDER);
// getControl kann weg, getTable macht das gleiche!
//		this.tableViewer.getControl().setLayoutData(gridData);
		this.tableViewer.getTable().setLayoutData(gridData);
		
		// create columns
		// Option column
		TableViewerColumn optionColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT, 0);
	    optionColumn.getColumn().setText("Option");
	    optionColumn.getColumn().setWidth(140);

	    // Value column
	    TableViewerColumn valueColumn = new TableViewerColumn(
	    		this.tableViewer, SWT.LEFT, 1);
	    valueColumn.getColumn().setText("Value");
	    valueColumn.getColumn().setWidth(60);
	    
	    // ANFANG NEU
		valueColumn.setEditingSupport(new PluginControllerValueEditingSupport(this.tableViewer));
		// ENDE NEU
		
	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);

		this.tableViewer.setContentProvider(new PluginControllerContentProvider());

		this.pluginControllerLabelProvider = new PluginControllerLabelProvider(); 
		this.tableViewer.setLabelProvider(this.pluginControllerLabelProvider);

// ANFANG ALT

// Anfang alt bis zum Punkt Ende Alt kann weg und soll durch den
// ValueEditingSupport ersetzt werden
// Hierfür wird dann unbedingt der MyComboBoxCellEditor verwendet
// In die ComboBox soll dann der Name des Gerätes geschrieben
// In der Struktur soll aber die ID als String geschrieben werden
	    
	    // Der CellEditor kommt weg,
	    // Hier wird jetzt der ValueEditingSupport verwendet
//	    final CellEditor[] editors = new CellEditor[2];
//	    final String[] props = {"option", "value"};
	    
//	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
//	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
   
//	    this.tableViewer.setCellEditors(editors);
//	    this.tableViewer.setCellModifier(
//	    		new PluginControllerCellModifyer(this.tableViewer));
//	    this.tableViewer.setColumnProperties(props);
	    // ENDE ALT
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
		if(pluginController != null) {
			pluginController.setScanModule(scanModule);
		}
	}
}