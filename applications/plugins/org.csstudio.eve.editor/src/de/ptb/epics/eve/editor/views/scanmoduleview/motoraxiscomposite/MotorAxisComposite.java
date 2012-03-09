package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>MotorAxisComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class MotorAxisComposite extends Composite {
	
	// logging
	private static Logger logger = Logger.getLogger(MotorAxisComposite.class);
	
	// the scan module the selected motor axes correspond to
	private ScanModule scanModule;
	
	// showing the selected motor axes
	private TableViewer tableViewer;
	
	// reference to the scan module view
	// (used to update the SelectionProviderWrapper)
	private ScanModuleView parentView;
	
	// context menu
	private MenuManager menuManager;
	
	/*
	 * the measuring station the available motors are taken from
	 */
	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation;
	
	/**
	 * Constructs a <code>MotorAxisComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param measuringStation the measuring station the motor axis belongs to
	 */
	public MotorAxisComposite(final ScanModuleView parentView, 
							final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		
		this.measuringStation = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				true, false, false, false, false);
		this.measuringStation.setSource(Activator.getDefault().
				getMeasuringStation());
		
		this.setLayout(new GridLayout());
		
		createViewer();
		
		this.tableViewer.getTable().addFocusListener(new 
				TableViewerFocusListener());
		
		menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new MenuManagerMenuListener());
		
		final Menu contextMenu = menuManager.createContextMenu(
				this.tableViewer.getTable());
		this.tableViewer.getControl().setMenu(contextMenu);	
	}
	
	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumHeight = 120;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getTable().setLayoutData(gridData);
		this.createColumns();
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableColumn column = new TableColumn(
				this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Motor Axis");
	    column.setWidth(250);

	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Stepfunction");
	    column.setWidth(80);
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
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one and set the motor axis view
				tableViewer.getTable().select(0);
			}
		}
		parentView.setSelectionProvider(this.tableViewer);
	}
	
	// ************************************************************************
	// **************************** Listener **********************************
	// ************************************************************************
	
	/**
	 * {@link org.eclipse.swt.events.FocusListener} of <code>tableViewer</code>.
	 */
	private class TableViewerFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focusGained");
			parentView.setSelectionProvider(tableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
		}
	}

	
	/**
	 * <code>IMenuListener</code> of <code>menuManager</code>.
	 */
	private class MenuManagerMenuListener implements IMenuListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {
			
			final ImageDescriptor classImage = ImageDescriptor.createFromImage(
					Activator.getDefault().getImageRegistry().get("CLASS"));
			final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
					Activator.getDefault().getImageRegistry().get("MOTOR"));
			final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
					Activator.getDefault().getImageRegistry().get("AXIS"));
			
			((ExcludeDevicesOfScanModuleFilterManualUpdate)measuringStation).
					update();
			
			for(final String className : measuringStation.getClassNameList()) {
				
				final MenuManager currentClassMenu = new MenuManager(
						className, classImage, className);
				
				for(final AbstractDevice device : 
					measuringStation.getDeviceList(className)) {

					if(device instanceof Motor) {
						final Motor motor = (Motor)device;
						final MenuManager currentMotorMenu = 
							new MenuManager(motor.getName(), motorImage, 
									motor.getName());
						currentClassMenu.add(currentMotorMenu);
						
						// iterate for each axis of the motor
						for(final MotorAxis axis : motor.getAxes()) {
							if (axis.getClassName().isEmpty()) {
								// add only axis which have no className
								final SetAxisAction setAxisAction = new 
										SetAxisAction(axis);
								setAxisAction.setImageDescriptor(axisImage);
								currentMotorMenu.add(setAxisAction);
							}
						}
						// if only one axis in MotorMenu, switch axis from 
						// MotorMenu into ClassMenu
						if (currentMotorMenu.getSize() == 1) {
							currentMotorMenu.removeAll();
							// Eintrag muß zur Class hinzugefügt werden.
							for(final MotorAxis axis : motor.getAxes()) {
								if (axis.getClassName().isEmpty()) {
									// add only axis which have no className
									final SetAxisAction setAxisAction = new 
											SetAxisAction(axis);
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
				if("".equals(motor.getClassName()) || motor.getClassName() == 
						null) {
					final MenuManager currentMotorMenu = 
							new MenuManager(motor.getName(), motorImage, 
									motor.getName());
					for(final MotorAxis axis : motor.getAxes()) {
						if("".equals(axis.getClassName()) || 
								axis.getClassName() == null) {
							final SetAxisAction setAxisAction = new 
									SetAxisAction(axis);
							setAxisAction.setImageDescriptor(axisImage);
							currentMotorMenu.add(setAxisAction);
						}
					}
					// if only one axis in MotorMenu, switch axis from 
					// MotorMenu into ClassMenu
					if (currentMotorMenu.getSize() == 1) {
						currentMotorMenu.removeAll();
						// Eintrag muß zur Class hinzugefügt werden.
						for(final MotorAxis axis : motor.getAxes()) {
							if (axis.getClassName().isEmpty()) {
								// add only axis which have no className
								final SetAxisAction setAxisAction = new 
										SetAxisAction(axis);
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
	private class SetAxisAction extends Action {
		
		final MotorAxis ma;
		
		/**
		 * Constructs a <code>SetAxisAction</code>.
		 * 
		 * @param ma the motor axis that should be set
		 */
		SetAxisAction(MotorAxis ma) {
			this.ma = ma;
			this.setText(ma.getName().isEmpty()
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
			// TODO: Wenn der MotorName TimerInt ist, wird der PositionMode auf 
			// relativ voreingestellt. Vorschlag: Den Wunsch nach relativer 
			// Schrittweite in das xsubst File eintragen und von dort 
			// übernehmen! Dann ist die Einstellung unabhängig vom Gerätenamen! 
			// (Hartmut 23.8.11)
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

			// the new axis (the last itemCount) will be selected in the table 
			// and displayed in the motorAxisView
			tableViewer.getTable().select(tableViewer.getTable().
										  getItemCount()-1);
			tableViewer.getControl().setFocus();

			tableViewer.refresh();
		}
	}
	
	/**
	 * <code>DeleteAction</code>.
	 */
	private class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Axis removeAxis = (Axis)((IStructuredSelection)
					tableViewer.getSelection()).getFirstElement();			
			scanModule.remove(removeAxis);
			
			// if another axis is available, select the first axis
			if(tableViewer.getTable().getItems().length != 0) {
				tableViewer.getTable().select(0);
			} 
			tableViewer.getControl().setFocus();
			
			tableViewer.refresh();
		}
	}
}