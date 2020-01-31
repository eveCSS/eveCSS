package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.plugin.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.PluginControllerContentProvider;
import de.ptb.epics.eve.editor.views.PluginControllerLabelProvider;
import de.ptb.epics.eve.editor.views.PluginControllerValueEditingSupport;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.DialogCellEditorDialog;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PluginDialog extends DialogCellEditorDialog {
	private final Axis axis;
	private TableViewer tableViewer;
	private PluginControllerLabelProvider labelProvider;
	private Combo pluginCombo;
	private List<String> pluginNames;
	
	public PluginDialog(Shell parentShell, Control control, Axis axis) {
		super(parentShell, control);
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite =new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		
		Label pluginLabel = new Label(composite, SWT.NONE);
		pluginLabel.setText("Plugin:");
		
		/*
		 * no Java 8 Lambdas, Filter, Map, etc.
		 * -> doing it "Old-School"
		 */
		pluginNames = new ArrayList<>();
		for (PlugIn plugin : Activator.getDefault().getMeasuringStation().
				getPlugins().toArray(new PlugIn[0])) {
			if (PluginTypes.POSITION.equals(plugin.getType())) {
				pluginNames.add(plugin.getName());
			}
		}
		pluginCombo = new Combo(composite, SWT.READ_ONLY);
		pluginCombo.setItems(pluginNames.toArray(new String[0]));
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		pluginCombo.setLayoutData(gridData);
		pluginCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PlugIn plugin = Activator.getDefault().getMeasuringStation().
						getPluginByName(pluginCombo.getText());
				axis.setPluginController(new PluginController(plugin));
				// legacy code requires setting the plugin in the label provider ?!?
				labelProvider.setPluginController(axis.getPluginController());
				tableViewer.setInput(axis.getPluginController());
			}
		});
		
		this.createTable(composite);
		
		return super.createDialogArea(parent);
	}
	
	private void createTable(Composite parent) {
		tableViewer = new TableViewer(parent, SWT.BORDER);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.minimumHeight = 150;
		gridData.minimumWidth = 300;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tableViewer.getTable().setLayoutData(gridData);
		
		TableViewerColumn optionColumn = new TableViewerColumn(
				tableViewer, SWT.LEFT);
		optionColumn.getColumn().setText("Option");
		optionColumn.getColumn().setWidth(140);
		
		TableViewerColumn valueColumn = new TableViewerColumn(
				tableViewer, SWT.LEFT);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setWidth(60);
		valueColumn.setEditingSupport(new PluginControllerValueEditingSupport(
				tableViewer));
		
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		tableViewer.setContentProvider(new PluginControllerContentProvider());
		this.labelProvider = new PluginControllerLabelProvider();
		tableViewer.setLabelProvider(this.labelProvider);
		
		// set initial values
		
		pluginCombo.select(pluginNames.indexOf(
				axis.getPluginController().getPlugin().getName()));
		// legacy code requires setting the plugin in the label provider ?!?
		labelProvider.setPluginController(axis.getPluginController());
		tableViewer.setInput(axis.getPluginController());
	}
}
