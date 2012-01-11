package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>MotorAxisComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class MotorAxisComposite extends Composite {
	
	private static Logger logger = Logger.getLogger(MotorAxisComposite.class);
	/*
	 * the table showing the selected motor axes
	 */
	private TableViewer tableViewer;
	private ViewPart parentView;
	
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
	public MotorAxisComposite(final ViewPart parentView, final Composite parent, final int style, 
							  final IMeasuringStation measuringStation) {
		super(parent, style);
		this.parentView = parentView;
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
	    
	    this.tableViewer.setContentProvider(new MotorAxisContentProvider());
	    this.tableViewer.setLabelProvider(new MotorAxisLabelProvider());
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
	    
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"device", "value"};
	    
	    this.tableViewer.setColumnProperties(props);

		this.tableViewer.getTable().addFocusListener(new TableViewerFocusListener());
	     
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

		logger.debug("setScanModule");
		
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);

		if(scanModule == null) {
			return;
		}

		// if there are motor axis present... 
		if(tableViewer.getTable().getItems().length > 0)
		{
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0)
			{
//				((ScanModuleView)parentView).selectionProviderWrapper.setSelectionProvider(tableViewer);

				// ... select the first one and set the motor axis view

				tableViewer.getTable().select(0);

				boolean focusStat;
				focusStat = tableViewer.getControl().setFocus();
//				focusStat = tableViewer.getControl().forceFocus();
//				focusStat = tableViewer.getTable().setFocus();
//				focusStat = tableViewer.getTable().forceFocus();
				System.out.println("\t\tFocus bekommen?: " + focusStat);

				//				tableViewer.getTable().forceFocus();
				((ScanModuleView)parentView).selectionProviderWrapper.setSelectionProvider(tableViewer);
//				tableViewer.getTable().setSelection(0);
				System.out.println("\tSelection: " + tableViewer.getTable().getSelectionIndex());
			}	
		}
	}	
	
	/*
	 * Sets the Plot Motor Axis if only one axis is available 
	 */
	private void setPlotMotorAxis()
	{
		logger.debug("setPlotMotorAxis");

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
	 * 
	 */
	class TableViewerFocusListener implements FocusListener {

		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focusGained");
			((ScanModuleView)parentView).selectionProviderWrapper.setSelectionProvider(tableViewer);
		}

		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			
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
					Activator.getDefault().
					getImageRegistry().get("CLASS"));
			final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("MOTOR"));
			final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
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
							new MenuManager(motor.getName(), motorImage, motor.getName());
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
						// if only one axis in MotorMenu, switch axis from MotorMenu into ClassMenu
						if (currentMotorMenu.getSize() == 1) {
							currentMotorMenu.removeAll();
							// Eintrag muß zur Class hinzugefügt werden.
							for(final MotorAxis axis : motor.getAxes()) {
								if (axis.getClassName().isEmpty()) {
									// add only axis which have no className
									final SetAxisAction setAxisAction = new SetAxisAction(axis);
									setAxisAction.setImageDescriptor(axisImage);
									currentClassMenu.add(setAxisAction);
								}
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
					// if only one axis in MotorMenu, switch axis from MotorMenu into ClassMenu
					if (currentMotorMenu.getSize() == 1) {
						currentMotorMenu.removeAll();
						// Eintrag muß zur Class hinzugefügt werden.
						for(final MotorAxis axis : motor.getAxes()) {
							if (axis.getClassName().isEmpty()) {
								// add only axis which have no className
								final SetAxisAction setAxisAction = new SetAxisAction(axis);
								setAxisAction.setImageDescriptor(axisImage);
								manager.add(setAxisAction);
							}
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
			// TODO: Wenn der MotorName TimerInt ist, wird der PositionMode auf relativ
			// voreingestellt. Vorschlag: Den Wunsch nach relativer Schrittweite in das
			// xsubst File eintragen und von dort übernehmen! Dann ist die Einstellung
			// unabhängig vom Gerätenamen! (Hartmut 23.8.11)
			if (ma.getName().equals("TimerInt")) {
				a.setPositionMode(PositionMode.RELATIVE);
			}
			if(a.getMotorAxis().getGoto().isDiscrete()) {
				a.setStepfunction(Stepfunctions.stepfunctionToString(
						Stepfunctions.POSITIONLIST));
				StringBuffer sb = new StringBuffer();
				for(String s : a.getMotorAxis().getGoto().getDiscreteValues()) {
					sb.append(s + ",");
				}
				a.setPositionlist(sb.substring(0, sb.length() - 1));
			}

			// the new axis (the last itemCount) will be selected in the table and 
			// displayed in the motorAxisView
//			((ScanModuleView)parentView).selectionProviderWrapper.setSelectionProvider(null);
//			tableViewer.getTable().setFocus();
			tableViewer.getTable().select(tableViewer.getTable().getItemCount()-1);
//			((ScanModuleView)parentView).selectionProviderWrapper.setSelectionProvider(tableViewer);

			// if only one axis available, set this axis for the Plot
			setPlotMotorAxis();

			tableViewer.refresh();
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