package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.MessageSource;
import de.ptb.epics.eve.viewer.messages.MessageTypes;
import de.ptb.epics.eve.viewer.messages.ViewerMessage;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>DevicesView</code> visualizes available 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}s of a 
 * {@link de.ptb.epics.eve.data.measuringstation.MeasuringStation} in a 
 * {@link org.eclipse.jface.viewers.TreeViewer}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public final class DevicesView extends ViewPart {

	/** the unique identifier of this view */
	public static final String ID = "DevicesView";

	private static Logger logger = Logger.getLogger(DevicesView.class.getName());
	
	private IMeasuringStation measuringStation;
	
	private TreeViewer treeViewer;
	private TreeViewerFocusListener treeViewerFocusListener;
	
	private DragSource source;

	private boolean dragInProgress;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		
		measuringStation = Activator.getDefault().getMeasuringStation();
		if(measuringStation == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No device description has been loaded. " +
					"Please check Preferences!");
			return;
		}
				
		final FillLayout fillLayout = new FillLayout();
		parent.setLayout(fillLayout);
		
		treeViewer = new TreeViewer(parent);
		treeViewer.setContentProvider(new TreeViewerContentProvider());
		treeViewer.setLabelProvider(new TreeViewerLabelProvider());
		treeViewer.getTree().setEnabled(false);
		treeViewer.setAutoExpandLevel(1);
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=177669
		
		// listen to double clicks (inserts the clicked element to the inspector)
		treeViewer.addDoubleClickListener(new TreeViewerDoubleClickListener());
		
		treeViewerFocusListener = new TreeViewerFocusListener();
		treeViewer.getTree().addFocusListener(treeViewerFocusListener);
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		treeViewer.getTree().setMenu(
				menuManager.createContextMenu(treeViewer.getTree()));
		// register menu
		getSite().registerContextMenu(
				"de.ptb.epics.eve.viewer.views.devicesview.treepopup", 
				menuManager, treeViewer);
		
		// set this tree viewer as a source for drag n drop (drop in inspector)
		this.source = new DragSource(
				this.treeViewer.getTree(), DND.DROP_COPY | DND.DROP_MOVE);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceDragListener());
		
		// Filtering
		
		
		// Filter test
		//ViewerFilter[] filters = new ViewerFilter[] {new TreeViewerFilter()};
		//treeViewer.setFilters(filters);
		
		setMeasuringStation(measuringStation);
		
		getSite().setSelectionProvider(treeViewer);
		
		dragInProgress = false;
	}

	/**
	 * 
	 * @param measuringStation
	 */
	public void setMeasuringStation(final IMeasuringStation measuringStation) {
		Activator.getDefault().getMessagesContainer().addMessage(
				new ViewerMessage(MessageSource.VIEWER, MessageTypes.INFO, 
						"Got new measuring station description."));
		this.measuringStation = measuringStation;
		this.treeViewer.setInput(this.measuringStation);
		this.treeViewer.getTree().setEnabled(this.measuringStation != null);
	}
	
	// ***********************************************************************
	// **************************** Listener *********************************
	// ***********************************************************************
	
	/**
	 * 
	 */
	class TreeViewerDoubleClickListener implements IDoubleClickListener {
	
		/**
		 * {@inheritDoc}
	 	 */
		@SuppressWarnings("unchecked")
		@Override
		public void doubleClick(DoubleClickEvent event) {
			
			if(logger.isDebugEnabled()) {
				logger.debug("Double Click: " + event.getSelection());
			}
			
			// get all views
			IViewReference[] ref = getSite().getPage().getViewReferences();
			
			DeviceInspectorView deviceInspectorView = null;
			for(IViewReference ivr : ref) {
				if(ivr.getId().equals(DeviceInspectorView.ID)) {
					if(DeviceInspectorView.activeDeviceInspectorView.equals(ivr.getSecondaryId())) {
						deviceInspectorView = (DeviceInspectorView)ivr.getPart(false);
					}
				}
			}
			
			if(deviceInspectorView != null) {
				Object selection = 
					treeViewer.getTree().getSelection()[0].getData();
				
				if(selection instanceof AbstractDevice) {
					deviceInspectorView.addAbstractDevice((AbstractDevice)
							treeViewer.getTree().getSelection()[0].getData());
				} else if(selection instanceof List<?>) {
					for(Object o : (List<Object>)selection) {
						deviceInspectorView.addAbstractDevice((AbstractDevice)o);
					}
				} else if(selection instanceof String) {
					IMeasuringStation measuringstation = 
						Activator.getDefault().getMeasuringStation();
					List<AbstractDevice> devices = 
						measuringstation.getDeviceList((String)selection);
					for(AbstractDevice d : devices) {
						deviceInspectorView.addAbstractDevice(d);
					}
				}
			}
		}
	}
	
	// ************************* DnD *****************************************
	
	/**
	 * 
	 */
	class DragSourceDragListener implements DragSourceListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dragStart(final DragSourceEvent event) {
			event.doit = true;
			
			for(TreeItem item : treeViewer.getTree().getSelection()) {
				if(logger.isDebugEnabled()) {
					logger.debug(item.getData() + " selected");
				}
				// if the selection contains a class (a collection of different
				// devices) do not allow dragNdrop
				if(item.getData() instanceof String) {
					event.doit = false;
				}
			}
			dragInProgress = true;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void dragSetData(final DragSourceEvent event) {
			
			// provide the data of the requested type
			
			if(TextTransfer.getInstance().isSupportedType(event.dataType)) {
				
				TreeItem[] items = treeViewer.getTree().getSelection();
				StringBuffer data = new StringBuffer();
				int count = 0;
				
				// build the string that is transfered to the drop target
				
				// add prefixes defining the type of the device
				for(TreeItem item : items) {
					if(item.getData() instanceof Motor) {
						data.append("M" + ((AbstractDevice)item.getData()).
								getFullIdentifyer());
					} else if(item.getData() instanceof MotorAxis) {
						data.append("A" + ((AbstractDevice)item.getData()).
								getFullIdentifyer());
					} else if(item.getData() instanceof Detector) {
						data.append("D" + ((AbstractDevice)item.getData()).
								getFullIdentifyer());
					} else if(item.getData() instanceof DetectorChannel) {
						data.append("C" + ((AbstractDevice)item.getData()).
								getFullIdentifyer());
					} else if(item.getData() instanceof Device) {
						data.append("d" + ((AbstractDevice)item.getData()).
								getFullIdentifyer());
					} else if(item.getData() instanceof List<?>) {
						
						int countB = 0;
						
						for(Object o : (List<Object>)item.getData()) {
							if(o instanceof Motor) {
								data.append("M");
							} else if(o instanceof Detector) {
								data.append("D");
							} else if(o instanceof Device) {
								data.append("d");
							}
							data.append(((AbstractDevice)o).
									getFullIdentifyer());
							
							countB++;
							if(countB != ((List<Object>)item.getData()).size() ||
									((List<Object>)item.getData()).size() == 1) {
								data.append(",");
							}
						}
					}
					
					count++;
					if(count != items.length && 
					   !(item.getData() instanceof List<?>)) {
						data.append(",");
					}
				}
				
				if(logger.isDebugEnabled()) {
					logger.debug("DragSource: " + data.toString());
				}
				
				event.data = data.toString();
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dragFinished(DragSourceEvent event) {
			// if a move operation has been performed -> remove the data 
			// from the source
			// ! we only want to copy data -> do nothing
			dragInProgress = false;
			treeViewer.getTree().deselectAll();
		}
	}
	
	// ************************************************************************
	// ************************* Listeners ************************************
	// ************************************************************************
	
	/**
	 * 
	 */
	class TreeViewerFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			if(!dragInProgress) {
				treeViewer.getTree().deselectAll();
			}
		}
	}
}