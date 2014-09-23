package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.measuringstation.PluginParameter;
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
		this.tableViewer.getTable().setLayoutData(gridData);
		
		// create columns
		// Option column
		TableViewerColumn column = new TableViewerColumn(
				this.tableViewer, SWT.LEFT, 0);
	    column.getColumn().setText("Option");
	    column.getColumn().setWidth(140);

	    // Value column
	    column = new TableViewerColumn(
	    		this.tableViewer, SWT.LEFT, 1);
	    column.getColumn().setText("Value");
	    column.getColumn().setWidth(60);

	    column.setEditingSupport(new PluginControllerValueEditingSupport(this.tableViewer));

	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    this.tableViewer.setContentProvider(new PluginControllerContentProvider());

	    this.pluginControllerLabelProvider = new PluginControllerLabelProvider(); 
	    this.tableViewer.setLabelProvider(this.pluginControllerLabelProvider);

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

}