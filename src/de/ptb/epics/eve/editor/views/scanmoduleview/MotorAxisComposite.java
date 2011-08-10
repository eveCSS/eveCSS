package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisView;

/**
 * <code>MotorAxisComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class MotorAxisComposite extends Composite {
	
	/*
	 * the table showing the selected motor axes
	 */
	private TableViewer tableViewer;
	private TableViewerSelectionListener tableViewerSelectionListener;
	
	/*
	 * the scan module the selected motor axes correspond to
	 */
	private ScanModule scanModule;
	
	/*
	 * the right-click menu to add new motors
	 */
	private MenuManager menuManager;
	
	/*
	 * the measuring station the available motors are taken from
	 */
	private final IMeasuringStation measuringStation;
	
	/**
	 * Constructs a <code>MotorAxisComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param measuringStation the measuring station the motor axis belongs to
	 */
	public MotorAxisComposite(final Composite parent, final int style, 
							  final IMeasuringStation measuringStation) {
		super(parent, style);
		this.measuringStation = measuringStation;

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
		
		TableColumn column = new TableColumn(
				this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Motor Axis");
	    column.setWidth(250);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Stepfunction");
	    column.setWidth(80);

	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    this.tableViewer.setContentProvider(new MotorAxisInputWrapper());
	    this.tableViewer.setLabelProvider(new MotorAxisLabelProvider());
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
	    
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"device", "value"};
	    
	    this.tableViewer.setColumnProperties(props);

	    tableViewerSelectionListener = new TableViewerSelectionListener();
		this.tableViewer.getTable().addSelectionListener(
				tableViewerSelectionListener);
	     
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		
		menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new MenuManagerMenuListener());
		
		final Menu contextMenu = menuManager.createContextMenu(
				this.tableViewer.getTable());
		this.tableViewer.getControl().setMenu(contextMenu);	
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis} of the 
	 * {@link de.ptb.epics.eve.editor.views.motoraxisview.MotorAxisView}.
	 *
	 * @param motorAxis the {@link de.ptb.epics.eve.data.scandescription.Axis} 
	 * 		  that should be set
	 */
	public void setMotorAxisView(Axis motorAxis) {
		
		// try to find the motor axis view
		IViewPart motorAxisView = PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow().
		  									 getActivePage().
		  									 findView(MotorAxisView.ID);
		
		if(motorAxisView != null)
		{
			// view found -> tell it about the current motor axis
			
			if(motorAxis != null)
			{
				// the step amount will be adjusted if one of the axes is defined 
				// as main axis
				Axis[] axis = scanModule.getAxes();
				double stepamount = -1.0;
				for(int j = 0; j < axis.length; ++j) {
					if(axis[j].isMainAxis()) {
						stepamount = axis[j].getStepCount();
						break;
					}
				}
			
				((MotorAxisView)motorAxisView).setCurrentAxis(
						motorAxis, stepamount, scanModule);
			} else {
				((MotorAxisView)motorAxisView).setCurrentAxis(null, -1);
			}
		}
	}
	
	/**
	 * Returns the currently set 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @return the currently set 
	 * 		   {@link de.ptb.epics.eve.data.scandescription.ScanModule}
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

		removeListeners();
		
		if(scanModule != null) {
			this.tableViewer.getTable().setEnabled(true);
		} else {
			this.tableViewer.getTable().setEnabled(false);
		}
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);
		
		// if there are motor axis present... 
		if(tableViewer.getTable().getItems().length > 0)
		{
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0)
			{
				// ... select the first one and set the motor axis view
				tableViewer.getTable().select(0);
				setMotorAxisView((Axis)tableViewer.getTable().getItem(0).getData());
			} else {
				// .. set the motor axis view
				setMotorAxisView((Axis)tableViewer.getTable().getSelection()[0].getData());
			}		
		} else {
			setMotorAxisView(null);
		}
		
		if(scanModule == null || tableViewer.getTable().getSelectionCount() == 0)
			setMotorAxisView(null);
		
		addListeners();
	}	
	
	/*
	 * 
	 */
	private void addListeners()
	{
		tableViewer.getTable().addSelectionListener(
				tableViewerSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		tableViewer.getTable().removeSelectionListener(
				tableViewerSelectionListener);
	}

	/*
	 * Sets the Plot Motor Axis if only one axis is available 
	 */
	private void setPlotMotorAxis()
	{
		final Axis[] availableMotorAxes;

		availableMotorAxes = scanModule.getAxes();
		String[] axisItems = new String[availableMotorAxes.length];
		for (int i = 0; i < availableMotorAxes.length; ++i) {
			axisItems[i] = 
				availableMotorAxes[i].getMotorAxis().getFullIdentifyer();
		}		
		
		// if only one axis available, set this axis as default in all Plot Windows
		if (availableMotorAxes.length == 1) {
			PlotWindow[] plotWindows = scanModule.getPlotWindows();
			for (int i = 0; i < plotWindows.length; ++i) {
				plotWindows[i].setXAxis(availableMotorAxes[0].getMotorAxis());
			}
		}
	}
	
	// ************************************************************************
	// **************************** Listener **********************************
	// ************************************************************************
	
	/**
	 * <code>SelectionListener</code> of <code>tableViewer</code>.
	 */
	class TableViewerSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			final String axisName = 
					tableViewer.getTable().getSelection()[0].getText(0);
			Axis[] axis = scanModule.getAxes();
			for(int i = 0; i < axis.length; ++i) {
				if(axis[i].getMotorAxis().getFullIdentifyer().equals(axisName)) {
					setMotorAxisView(axis[i]);
				}
			}
		}
	}
	
	/**
	 * <code>IMenuListener</code> of <code>menuManager</code>.
	 */
	class MenuManagerMenuListener implements IMenuListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {
			
			final ImageDescriptor classImage = ImageDescriptor.createFromImage(
					de.ptb.epics.eve.viewer.Activator.getDefault().
					getImageRegistry().get("CLASS"));
			final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
					de.ptb.epics.eve.viewer.Activator.getDefault().
					getImageRegistry().get("MOTOR"));
			final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
					de.ptb.epics.eve.viewer.Activator.getDefault().
					getImageRegistry().get("AXIS"));
			
			((ExcludeDevicesOfScanModuleFilterManualUpdate)measuringStation).update();
			
			for(final String className : measuringStation.getClassNameList()) {
				
				final MenuManager currentClassMenu = new MenuManager(
						className, classImage, className);
				
				for(final AbstractDevice device : 
					measuringStation.getDeviceList(className)) {

					if(device instanceof Motor) {
						final Motor motor = (Motor)device;
						final MenuManager currentMotorMenu = 
							new MenuManager(
								motor.getName(), motorImage, motor.getName());
						currentClassMenu.add(currentMotorMenu);
						
						// iterate for each axis of the motor
						for(final MotorAxis axis : motor.getAxes()) {
							if (axis.getClassName().isEmpty()) {
								// add only axis which have no className
								final SetAxisAction setAxisAction = new SetAxisAction(axis);
								setAxisAction.setImageDescriptor(axisImage);
								currentMotorMenu.add(setAxisAction);
							}
						}
					} else if(device instanceof MotorAxis) {
						final SetAxisAction setAxisAction = 
							new SetAxisAction((MotorAxis)device);
						setAxisAction.setImageDescriptor(axisImage);
						currentClassMenu.add(setAxisAction);
					}
					manager.add(currentClassMenu);
				}
			}
				
			for(final Motor motor : measuringStation.getMotors()) {
				if("".equals(motor.getClassName()) || motor.getClassName() == null) {
					final MenuManager currentMotorMenu = 
							new MenuManager(motor.getName(), motorImage, motor.getName());
					for(final MotorAxis axis : motor.getAxes()) {
						if("".equals(axis.getClassName()) || axis.getClassName() == null) {
							final SetAxisAction setAxisAction = new SetAxisAction(axis);
							setAxisAction.setImageDescriptor(axisImage);
							currentMotorMenu.add(setAxisAction);
						}
					}
					manager.add(currentMotorMenu);
				}
			}
				
			if (scanModule.getAxes().length > 0) {
				DeleteAction deleteAction = new DeleteAction(); 
				deleteAction.setEnabled(true);
				deleteAction.setText("Delete Axis");
				deleteAction.setToolTipText("Deletes Axis");
				deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
												getSharedImages().
												getImageDescriptor(
												ISharedImages.IMG_TOOL_DELETE));
				manager.add(deleteAction);
			}
		}
	}
	
	// *************************** Actions ***********************************
	
	/**
	 * <code>SetAxisAction</code>.
	 */
	class SetAxisAction extends Action {
		
		final MotorAxis ma;
		
		/**
		 * Constructs a <code>SetAxisAction</code>.
		 * 
		 * @param ma the motor axis that should be set
		 */
		SetAxisAction(MotorAxis ma) {
			this.ma = ma;
			this.setText("".equals(ma.getName())
						 ? ma.getID()
						 : ma.getName());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			
			for(final Axis a : scanModule.getAxes()) {
				if(a.getAbstractDevice() == ma) {
					return;
				}
			}		
			super.run();
			Axis a = new Axis(scanModule);
			a.setMotorAxis(ma);
			scanModule.add(a);
			setMotorAxisView(a);
			// if only one axis available, set this axis for the Plot
			setPlotMotorAxis();

			tableViewer.refresh();
			tableViewer.getTable().setSelection(
					tableViewer.getTable().getItemCount()-1);
		}	
	}
	
	/**
	 * <code>DeleteAction</code>.
	 */
	class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
	    		
			Axis removeAxis = (Axis)((IStructuredSelection)
					tableViewer.getSelection()).getFirstElement();
			
			// MotorAxis wird aus scanModul ausgetragen
			scanModule.remove(removeAxis);

			// if only one axis available, set this axis as for the Plot
			setPlotMotorAxis();

			tableViewer.refresh();
	   }
	}
}