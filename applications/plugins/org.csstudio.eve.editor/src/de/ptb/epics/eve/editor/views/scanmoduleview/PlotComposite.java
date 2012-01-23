package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.csstudio.swt.xygraph.figures.Trace.PointStyle;
import org.csstudio.swt.xygraph.figures.Trace.TraceType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>PlotComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author Hartmut Scherr
 */
public class PlotComposite extends Composite {

	// logging
	private static Logger logger = Logger.getLogger(PlotComposite.class);

	private TableViewer tableViewer;
	private ScanModule scanModule;
	private MenuManager menuManager;
	private ViewPart parentView;

	/**
	 * Constructs a <code>PlotComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public PlotComposite(final ViewPart parentView, final Composite parent, 
						 final int style) {
		super(parent, style);
		this.parentView = parentView;

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
	    column.setText("Id");
	    column.setWidth(50);

//		column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
//	    column.setText("Name");
//	    column.setWidth(80);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("x Axis");
	    column.setWidth(100);
		
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 2);
	    column.setText("y Axis1");
	    column.setWidth(100);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 3);
	    column.setText("y Axis2");
	    column.setWidth(100);

	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new PlotContentProvider());
	    this.tableViewer.setLabelProvider(new PlotLabelProvider());
	    
	    final CellEditor[] editors = new CellEditor[1];
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());

	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"plot" };
	    
	    this.tableViewer.setColumnProperties(props);
	    
		this.tableViewer.getTable().addFocusListener(new TableViewerFocusListener());

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

		logger.debug("setScanModule");

		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);

		if(scanModule == null) {
			return;
		}
		// if there are plots present... 
		if(tableViewer.getTable().getItems().length > 0)
		{
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0)
			{	// ... select the first one
				// der richtige Plot selektiert und gezeigt!
				tableViewer.getTable().select(0);
//				tableViewer.getTable().setFocus();
			}
		}
		((ScanModuleView)parentView).selectionProviderWrapper.
		setSelectionProvider(this.tableViewer);
	}
	
	/**
	 * 
	 */
	class MenuManagerMenuListener implements IMenuListener {
		
		final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
				Activator.getDefault().
				getImageRegistry().get("AXIS"));
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {

			Action addAction = new AddAction();
			addAction.setEnabled(true);
			addAction.setText("Add Plot Window");
			addAction.setToolTipText("Adds Plot Window");
			addAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("PLOT"));
			manager.add(addAction);

			if(scanModule.getPlotWindows().length > 0) {
				Action changeAction = new ChangeAction();
				changeAction.setEnabled(true);
				changeAction.setText("Change Id");
				changeAction.setToolTipText("Changes Id of Plot");
				changeAction.setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("RENAME"));
				manager.add(changeAction);
				
				Action deleteAction = new DeleteAction();
				deleteAction.setEnabled(true);
				deleteAction.setText("Delete Plot");
				deleteAction.setToolTipText("Deletes Plot");
				deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
									getSharedImages().getImageDescriptor(
									ISharedImages.IMG_TOOL_DELETE));
				manager.add(deleteAction);
			}
		}
	}

	/**
	 * 
	 */
	class AddAction extends Action {
		
		private Axis[] availableMotorAxes;
		private Channel[] availableDetectorChannels;

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {

			int newID = 1;
			PlotWindow[] plotWindows = scanModule.getPlotWindows();
			do {
				boolean repeat = false;
				for (int i = 0; i < plotWindows.length; ++i) {
					if (plotWindows[i].getId() == newID) {
						newID++;
						repeat = true;
						break;
					}
				}
				if (!repeat) break;
			} while(true);
			PlotWindow plotWindow = new PlotWindow(scanModule);
			plotWindow.setId(newID);

			// if only one axis available, set this axis as default
			availableMotorAxes = scanModule.getAxes();
			if (availableMotorAxes.length == 1) {
				plotWindow.setXAxis(availableMotorAxes[0].getMotorAxis());
			}

			// if only one channel available, create a yAxis and set 
			// this channel as default
			availableDetectorChannels = scanModule.getChannels();
			if (availableDetectorChannels.length == 1) {

				YAxis yAxis1 = new YAxis();
				// default values for color, line style and mark style
				yAxis1.setColor(new RGB(0,0,255));
				yAxis1.setLinestyle(TraceType.SOLID_LINE);
				yAxis1.setMarkstyle(PointStyle.NONE);

				yAxis1.setDetectorChannel(availableDetectorChannels[0].getDetectorChannel());
				plotWindow.addYAxis(yAxis1);
			}

			scanModule.add(plotWindow);

			// the new plot (the last itemCount) will be selected in the table and 
			// displayed in the plotWindow
			tableViewer.getTable().select(tableViewer.getTable().getItemCount()-1);
			tableViewer.getControl().setFocus();

			tableViewer.refresh();
    	}
	}

	/**
	 * 
	 */
	class ChangeAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {

			Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
			PlotWindow plotWindow = (PlotWindow)((IStructuredSelection)
							tableViewer.getSelection()).getFirstElement();

			InputDialog dialog = new InputDialog(shell,
					"Change Id for Plot Window",
					"Please enter the new Id", "" + plotWindow.getId(), null);
			if (InputDialog.OK == dialog.open()) {
				// ID wird nur gesetzt, wenn es sie noch nicht gibt
				int newId = Integer.parseInt(dialog.getValue());

				PlotWindow[] plotWindows = scanModule.getPlotWindows();
				boolean setId = true;
				for (int j = 0; j < plotWindows.length; ++j) {
					if (newId == plotWindows[j].getId()) {
						// ID wird nicht gesetzt, da schon vorhanden
						setId = false;
					}
				}
				if (setId) {
					plotWindow.setId(newId);
				}
			}
    	}
	}

	/**
	 * 
	 */
	class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
    		
			scanModule.remove((PlotWindow)((IStructuredSelection)
					tableViewer.getSelection()).getFirstElement());

			// if another plot is available, select the first plot
			if(tableViewer.getTable().getItems().length != 0) {
				tableViewer.getTable().select(0);
			} 
			tableViewer.getControl().setFocus();
			
			tableViewer.refresh();
    	}
	}

	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************
	
	/**
	 * 
	 */
	class TableViewerFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focusGained");
			((ScanModuleView)parentView).selectionProviderWrapper.
								setSelectionProvider(tableViewer);
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	
}