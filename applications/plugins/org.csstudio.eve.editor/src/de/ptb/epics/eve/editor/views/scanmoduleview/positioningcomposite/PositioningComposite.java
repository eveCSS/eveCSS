package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>PositioningComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PositioningComposite extends Composite {

	private TableViewer tableViewer;
	private ScanModuleView parentView;
	private ScanModule scanModule;
	
	/**
	 * Constructs a <code>PositioningComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PositioningComposite(final ScanModuleView parentView, 
								final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		
		FillLayout fillLayout = new FillLayout();
		this.setLayout(fillLayout);
		
		createViewer();
		
		
		
		/*
	   
	    
	    final CellEditor[] editors = new CellEditor[5];
	    
	    final List<PlugIn> plugIns = Activator.getDefault().getMeasuringStation().getPlugins();
	    final List<String> positionPlugInNames = new ArrayList<String>();
	    final Iterator<PlugIn> it = plugIns.iterator();
	    while(it.hasNext()) {
	    	final PlugIn currentPlugin = it.next();
	    	if(currentPlugin.getType() == PluginTypes.POSTSCANPOSITIONING) {
	    		positionPlugInNames.add(currentPlugin.getName());
	    	}
	    }
	    final String[] plugins = positionPlugInNames.toArray(new String[0]);
	    // die erlaubten Detektoren werden erst später gesetzt
	    final String[] detectorChannels = {};
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new ComboBoxCellEditor(this.tableViewer.getTable(), plugins, SWT.READ_ONLY);
	    editors[2] = new ComboBoxCellEditor(this.tableViewer.getTable(), detectorChannels, SWT.READ_ONLY);
	    editors[3] = new ComboBoxCellEditor(this.tableViewer.getTable(), detectorChannels, SWT.NONE);
	    editors[4] = new PluginParameterButtonCellEditor(this.tableViewer.getTable());
	    
	    this.tableViewer.setCellModifier(new CellModifyer(this.tableViewer));
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"axis", "plugin", "channel", "normalize", "parameter"};
	    
	    this.tableViewer.setColumnProperties(props);*/
	}
	
	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.NONE);
		createColumns();
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite.popup", 
			menuManager, this.tableViewer);
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn axisColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		axisColumn.getColumn().setText("Motor Axis");
		axisColumn.getColumn().setWidth(140);
		
		TableViewerColumn pluginColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		pluginColumn.getColumn().setText("Plugin");
		pluginColumn.getColumn().setWidth(60);
		
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(140);
		
		TableViewerColumn normColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		normColumn.getColumn().setText("Normalize Channel");
		normColumn.getColumn().setWidth(140);
		
		TableViewerColumn paramColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		paramColumn.getColumn().setText("Parameters");
		paramColumn.getColumn().setWidth(50);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule} the 
	 * composite is based on.
	 * 
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		should be set
	 */
	public void setScanModule(final ScanModule scanModule) {

		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);

		// if there are positioings present... 
		if(tableViewer.getTable().getItems().length > 0)
		{
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0)
			{	// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
	}
}