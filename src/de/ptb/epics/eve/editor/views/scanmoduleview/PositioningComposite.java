package de.ptb.epics.eve.editor.views.scanmoduleview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>PositioningComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PositioningComposite extends Composite {

	private TableViewer tableViewer;
	private ScanModule scanModule;
	private MenuManager menuManager;
	
	/**
	 * Constructs a <code>PositioningComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PositioningComposite(final Composite parent, final int style) {
		super(parent, style);

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer(this, SWT.NONE);
		this.tableViewer.getControl().setLayoutData(gridData);
		
		TableColumn column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Motor Axis");
	    column.setWidth(140);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Plugin");
	    column.setWidth(60);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 2);
	    column.setText("Detector Channel");
	    column.setWidth(140);
	    
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 3);
	    column.setText("Normalize Channel");
	    column.setWidth(140);
	    
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 4);
	    column.setText("Parameters");
	    column.setWidth(50);
	    
	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new PositioningInputWrapper());
	    this.tableViewer.setLabelProvider(new PositioningLabelProvider());
	    
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
	    
	    this.tableViewer.setCellModifier(new PositioningCellModifyer(this.tableViewer));
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"axis", "plugin", "channel", "normalize", "parameter"};
	    
	    this.tableViewer.setColumnProperties(props);
	    
	    menuManager = new MenuManager("#PopupMenu");
		
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new MenuManagerMenuListener());
		
		final Menu contextMenu = 
			menuManager.createContextMenu(this.tableViewer.getTable());
		this.tableViewer.getControl().setMenu(contextMenu);
		
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

		if(scanModule != null) {
			this.tableViewer.getTable().setEnabled(true);
		} else {
			this.tableViewer.getTable().setEnabled(false);
		}
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);
	}
	
	/**
	 * 
	 */
	class MenuManagerMenuListener implements IMenuListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {
			// Liste der aktuellen Positionings erstellen
			List <MotorAxis> posList = new ArrayList<MotorAxis>();
			Positioning[] positionings = scanModule.getPositionings();
			for(int i = 0; i < positionings.length; ++i) {
				posList.add(positionings[i].getMotorAxis());
			}
			
			for(final Axis axis : scanModule.getAxes()) {
				// die Achsen für die es Positioning gibt, werden entfernt
				if (posList.contains(axis.getMotorAxis())) {
					continue;
				}
				
				final Action addPositioningAction = new Action() {

					final MotorAxis ax = axis.getMotorAxis();
					
					@Override
					public void run() {
						for(final Positioning p : scanModule.getPositionings()) {
							if(p.getMotorAxis() == ax) {
								return;
							}
						}
						super.run();
						final Positioning p = new Positioning();
						p.setMotorAxis(ax);
						scanModule.add(p);
						tableViewer.refresh();
					}
				};
				
				addPositioningAction.setText(axis.getMotorAxis().getName());
				manager.add(addPositioningAction);
			}
			
			Action deleteAction = new Action() {
				
				@Override
		    	public void run() {
		    		
		    		Positioning weg = (Positioning)((IStructuredSelection)
		    				tableViewer.getSelection()).getFirstElement();
		    		scanModule.remove(weg);
		    		tableViewer.refresh();
		    	}
		    };
		    
		    deleteAction.setEnabled(true);
		    deleteAction.setText("Delete Positioning");
		    deleteAction.setToolTipText("Deletes Positioning");
		    deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
		    							getSharedImages().getImageDescriptor(
		    							ISharedImages.IMG_TOOL_DELETE));
		    manager.add(deleteAction);
		}
	}
}