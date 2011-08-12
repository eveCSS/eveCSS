package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;

/**
 * <code>DeviceOptionsView</code> displays 
 * {@link de.ptb.epics.eve.data.measuringstation.Option}s of an 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}.
 * <p>
 * Multiple instances of this view are allowed. Only the currently active 
 * instance will react to selection change events of the selection service.
 * The view instance is the active one if its secondary id equals the static 
 * property {@link #activeDeviceOptionsView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class DeviceOptionsView extends ViewPart implements ISelectionListener {
	
	/** the unique identifier of this view */
	public static final String ID = "DeviceOptionsView";
	
	/** 
	 * The secondary id of the <code>DeviceOptionsView</code> that is shown. 
	 * (Changes in the selection service will be addressed only by this one) 
	 */
	public static String activeDeviceOptionsView;
	
	private static Logger logger = 
			Logger.getLogger(DeviceOptionsView.class.getName());
	
	private TableViewer tableViewer;
	
	// underlying model of this view
	private AbstractDevice device;
	
	// ensures that the right Device Options View is active after a perspective 
	// switch.
	private DeviceOptionsViewPartListener deviceOptionsViewPartListener;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IViewSite site, final IMemento memento) 
													throws PartInitException {
		super.init(site, memento);
		if(memento != null) {
			final String identifier = memento.getString("device");
			if(identifier != null && !identifier.isEmpty()) {
				final AbstractDevice device = 
					Activator.getDefault().getMeasuringStation().
					getAbstractDeviceByFullIdentifyer(identifier);
				if(device != null) {
					this.device = 
						Activator.getDefault().getMeasuringStation().
						getAbstractDeviceByFullIdentifyer(identifier);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		this.tableViewer = new TableViewer(parent);
		
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getControl().setLayoutData(gridData);
		
		TableColumn column = 
			new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 0);
		column.setText("Option");
		column.setWidth(140);
		
		column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
		column.setText("Value");
		column.setWidth(60);
		
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);

		this.tableViewer.setContentProvider(new DeviceOptionsContentProvider());
		this.tableViewer.setLabelProvider(new DeviceOptionsLabelProvider());
		final CellEditor[] editors = new CellEditor[2];
		
		editors[0] = null;
		editors[1] = new TextCellEditor(this.tableViewer.getTable()) {
			@Override protected void focusLost() {
				fireCancelEditor();
			}
		};
		
		this.tableViewer.setCellModifier(
				new DeviceOptionsCellModifier(this.tableViewer));
		this.tableViewer.setCellEditors(editors);

		final String[] props = {"option", "value"};
		
		this.tableViewer.setColumnProperties(props);
		
		if(this.device != null) {
			this.setDevice(device);
		}
		
		// get all views
		IViewReference[] ref = getSite().getPage().getViewReferences();
		DeviceOptionsView deviceOptionsView = null;
		for(IViewReference ivr : ref) {
			if(ivr.getId().equals(DeviceOptionsView.ID)) {
					deviceOptionsView = (DeviceOptionsView)ivr.getPart(false);
			}
		}
		if(deviceOptionsView == null) {
			activeDeviceOptionsView = this.getViewSite().getSecondaryId();
		}
		
		deviceOptionsViewPartListener = new DeviceOptionsViewPartListener();
		IPartService service = 
				(IPartService) getSite().getService(IPartService.class);
		service.addPartListener(deviceOptionsViewPartListener);
		
		// listen to selection changes
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
	} // end of: createPartControl

	/**
	 * {@inheritDoc}
	 * <p>
	 * The view becomes the active one (of all 
	 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView} 
	 * instances). Only the active instance responds to selection changes in the 
	 * selection service.
	 */
	@Override
	public void setFocus() {
		activeDeviceOptionsView = this.getViewSite().getSecondaryId();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		IPartService service = 
			(IPartService) getSite().getService(IPartService.class);
		service.removePartListener(deviceOptionsViewPartListener);
	
		getSite().getWorkbenchWindow().getSelectionService().
			removeSelectionListener(this);
		
		if(this.getViewSite().getSecondaryId().equals(activeDeviceOptionsView)) {
			activeDeviceOptionsView = null;
		}
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the view is the currently active instance and the Selection is a 
	 * {@link org.eclipse.jface.viewers.IStructuredSelection} and the contained 
	 * data is either a 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * (a row in one of the device inspector tables) 
	 * or an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} 
	 * (an item in the devices view tree) its options will be displayed.
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// if this view instance is not the currently active one 
		// (the one in front) -> do nothing
		if (activeDeviceOptionsView == null ||
			!(activeDeviceOptionsView.equals(this.getViewSite().getSecondaryId()))) {
			return;
		}
		if(logger.isDebugEnabled() && selection != null) {
			logger.debug(selection.toString());
		}
		if(selection instanceof IStructuredSelection) {
			if(((IStructuredSelection) selection).size() == 0) {
				this.tableViewer.setInput(null);
				this.device = null;
				this.setPartName("nothing selected");
				return;
			}
			// since at any given time this view can only display options of 
			// one device we take the first element of the selection
			Object o = ((IStructuredSelection) selection).toList().get(0);
			if(o instanceof CommonTableElement) {
				if (this.device != null && this.device.equals(
					((CommonTableElement)o).getAbstractDevice())) {
						// the same element in the inspector was selected again
						return;
				}
				// the selection is a tableViewer selection (from the inspector)
				this.device = ((CommonTableElement)o).getAbstractDevice();
				this.tableViewer.setInput(device);
				this.setPartName(device.getFullIdentifyer());
			} else if(o instanceof AbstractDevice) {
				if (this.device != null && this.device.equals(o)) {
						// the same element in the options was selected again
						return;
				}
				
				// the selection is from a treeViewer (DevicesView)
				this.device = (AbstractDevice)o;
				this.tableViewer.setInput(device);
				this.setPartName(device.getFullIdentifyer());
			} else {
				this.tableViewer.setInput(null);
				this.device = null;
				this.setPartName("nothing selected");
			}
		} else {
			this.tableViewer.setInput(null);
			this.device = null;
			this.setPartName("nothing selected");
		}
	}

	/**
	 * Sets the device of which options should be shown.
	 * 
	 * @param device the device that should be set
	 * @deprecated To ensure a loose coupling, the eclipse built-in selection 
	 * service should be used instead.The <code>DeviceOptionsView</code> 
	 * implements {@link org.eclipse.ui.ISelectionListener} and registers 
	 * itself to the selection service. Parties interested in showing options 
	 * of a device should register a selection provider. Consult the 
	 * <a href="http://www.eclipse.org/articles/Article-WorkbenchSelections/article.html">
	 * Eclipse Article</a> for further details.<br>
	 * If a new view is created it should grab the active selection and set 
	 * the device itself...
	 */
	public void setDevice(final AbstractDevice device) {
		this.device = device;
		this.tableViewer.setInput(device);
		this.setPartName(device.getFullIdentifyer());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(final IMemento memento) {
		if(this.device != null) {
			memento.putString("device", this.device.getFullIdentifyer());
		}
	}
}