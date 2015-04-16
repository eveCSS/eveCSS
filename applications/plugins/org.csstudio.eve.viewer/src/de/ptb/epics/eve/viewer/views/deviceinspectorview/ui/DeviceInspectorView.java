package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.util.jface.SelectionProviderWrapper;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.DragNDropPrefix;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.TableViewerComparator;

/**
 * <code>DeviceInspectorView</code> presents a selection of devices separated in 
 * three categories (motor axes, detector channels and devices) and allows 
 * modifying them (e.g. move a motor axes).
 * 
 * @author Stephan Rehfeld <stephan.rehfeld@ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class DeviceInspectorView extends ViewPart {

	/** 
	 * the public identifier of this view 
	 */
	public static final String ID = "DeviceInspectorView";
	
	/** 
	 * the secondary id of the <code>DeviceInspectorView</code> that is shown 
	 */
	public static String activeDeviceInspectorView = "";
	
	private static Logger LOGGER = 
			Logger.getLogger(DeviceInspectorView.class.getName());

	private SashForm sashForm;
	
	private Composite motorAxesComposite;
	private boolean motorAxesCompositeMaximized;
	private Label motorMaxIcon;
	private MotorIconMouseListener motorIconMouseListener;
	private Label motorIcon;
	private Label motorLabel;
	private TableViewer axisTableViewer;
	private CommonTableContentProvider axisTableContentProvider;
	private CommonTableDragSourceListener axisTableDragSourceListener;
	private AxisTableDropTargetListener axisTableDropTargetListener;
	private AxisTableFocusListener axisTableFocusListener;
	
	// sorting
	private AxisTableNameColumnSelectionListener axisTableNameColumnSelectionListener;
	private TableViewerComparator axisTableViewerComparator;
	private int axisTableSortState; // 0 no sort, 1 asc, 2 desc
	
	private Composite detectorChannelsComposite;
	private boolean detectorChannelsCompositeMaximized;
	private Label channelMaxIcon;
	private DetectorIconMouseListener detectorIconMouseListener;
	private Label channelIcon;
	private Label channelLabel;
	private TableViewer channelTableViewer;
	private CommonTableContentProvider channelTableContentProvider;
	private CommonTableDragSourceListener channelTableDragSourceListener;
	private ChannelTableDropTargetListener channelTableDropTargetListener;
	private ChannelTableFocusListener channelTableFocusListener;
	
	// sorting
	private ChannelTableNameColumnSelectionListener channelTableNameColumnSelectionListener;
	private TableViewerComparator channelTableViewerComparator;
	private int channelTableSortState;
	
	private Composite devicesComposite;
	private boolean devicesCompositeMaximized;
	private Label deviceMaxIcon;
	private DeviceIconMouseListener deviceIconMouseListener;
	private Label deviceIcon;
	private Label deviceLabel;
	private TableViewer deviceTableViewer;
	private CommonTableContentProvider deviceTableContentProvider;
	private CommonTableDragSourceListener deviceTableDragSourceListener;
	private DeviceTableDropTargetListener deviceTableDropTargetListener;
	private DeviceTableFocusListener deviceTableFocusListener;
	
	// sorting
	private DeviceTableNameColumnSelectionListener deviceTableNameColumnSelectionListener;
	private TableViewerComparator deviceTableViewerComparator;
	private int deviceTableSortState;
	
	private List<AbstractDevice> devices;
	
	// the selection service only accepts one selection provider per view,
	// since we have three tables capable of providing selections a wrapper 
	// handles them and registers the active one with the global selection 
	// service
	private SelectionProviderWrapper selectionProviderWrapper;
	
	// listens to the event that the part becomes visible in order to update 
	// the static property activeDeviceInspector
	private DeviceInspectorViewPartListener deviceInspectorViewPartListener;
	
	// listens to events of the cell editor of the motor axis table
	private AxisTableColumnEditorActivationListener 
			axisTableColumnEditorActivationListener;
	
	// saves/restores user defined settings
	private IMemento memento;
	
	private Image deleteIcon;
	private Image leftArrowIcon;
	private Image leftArrowIconDisabled;
	private Image rightArrowIcon;
	private Image rightArrowIconDisabled;
	private Image playIcon;
	private Image playIconDisabled;
	private Image stopIcon;
	private Image stopIconDisabled;
	private Image restoreIcon;
	private Image maximizeIcon;
	private Image axisImage;
	private Image channelImage;
	private Image deviceImage;
	private Image ascending;
	private Image descending;
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Restores devices present in the axes, channels and devices table during 
	 * shutdown of the application.
	 */
	@Override
	public void init(final IViewSite site, final IMemento memento) 
													throws PartInitException {
		init(site);
		this.memento = memento;
		
		devices = new ArrayList<AbstractDevice>();
		
		if(memento == null) {
			return;
		}
		
		// restore table items
		String devicesString = memento.getString("devices");
		if(devicesString != null) {
			for(String s : devicesString.split(",")) {
				if(!s.isEmpty()) {
					AbstractDevice dev = Activator.getDefault().
						getMeasuringStation().getAbstractDeviceByFullIdentifyer(s);
					if(dev != null) {
						devices.add(dev);
					}
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		// icons
		deleteIcon = PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_DELETE).createImage();
		leftArrowIcon = PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_BACK).createImage();
		leftArrowIconDisabled = PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED).createImage();
		rightArrowIcon = PlatformUI.getWorkbench().
				getSharedImages().getImageDescriptor(
				ISharedImages.IMG_TOOL_FORWARD).createImage();
		rightArrowIconDisabled = PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED).createImage();
		playIcon = Activator.getDefault().getImageRegistry().get("PLAY16");
		playIconDisabled = 
			Activator.getDefault().getImageRegistry().get("PLAY16_DISABLED");
		stopIcon = Activator.getDefault().getImageRegistry().get("STOP16");
		stopIconDisabled = 
			Activator.getDefault().getImageRegistry().get("STOP16_DISABLED");
		restoreIcon = 
				Activator.getDefault().getImageRegistry().get("RESTOREVIEW");
		maximizeIcon = 
				Activator.getDefault().getImageRegistry().get("MAXIMIZE");
		axisImage = 
				Activator.getDefault().getImageRegistry().get("AXIS");
		channelImage = 
				Activator.getDefault().getImageRegistry().get("CHANNEL");
		deviceImage = PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_ELCL_SYNCED).createImage();
		ascending =	 Activator.getDefault().getImageRegistry().get("ASCENDING");
		descending = Activator.getDefault().getImageRegistry().get("DESCENDING");
		
		parent.setLayout(new FillLayout());
		
		sashForm = new SashForm(parent, SWT.VERTICAL);
		sashForm.SASH_WIDTH = 4;
		
		// Motor Axes Composite
		motorAxesComposite = new Composite(sashForm, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		motorAxesComposite.setLayout(gridLayout);
		
		motorMaxIcon = new Label(motorAxesComposite, SWT.NONE);
		motorMaxIcon.setImage(maximizeIcon);
		motorIconMouseListener = new MotorIconMouseListener();
		motorMaxIcon.addMouseListener(motorIconMouseListener);
		
		motorIcon = new Label(motorAxesComposite, SWT.NONE);
		motorIcon.setImage(axisImage);
		
		motorLabel = new Label(motorAxesComposite, SWT.NONE);
		motorLabel.setText("Motor Axes:");
		
		axisTableViewer = new TableViewer(motorAxesComposite, SWT.BORDER
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.minimumHeight = 25;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		axisTableViewer.getTable().setLayoutData(gridData);
		axisTableViewer.getTable().setHeaderVisible(true);
		axisTableViewer.getTable().setLinesVisible(true);
		
		axisTableViewerComparator = new TableViewerComparator();
		axisTableNameColumnSelectionListener = 
			new AxisTableNameColumnSelectionListener();
		
		createAxisTableColumns();
		axisTableContentProvider = 
				new CommonTableContentProvider(axisTableViewer, devices);
		axisTableViewer.setContentProvider(axisTableContentProvider);
		axisTableViewer.setInput(axisTableContentProvider);
		axisTableFocusListener = new AxisTableFocusListener();
		axisTableViewer.getTable().addFocusListener(axisTableFocusListener);
		
		// drag source for item reordering
		DragSource axisTableDragSource = 
				new DragSource(axisTableViewer.getTable(), DND.DROP_MOVE);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		axisTableDragSource.setTransfer(types);
		axisTableDragSourceListener = new CommonTableDragSourceListener(
				axisTableViewer);
		axisTableDragSource.addDragListener(axisTableDragSourceListener);
		
		// a drop target receives data in a Drag and Drop operation
		DropTarget axisTableDropTarget = new DropTarget(
				axisTableViewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE);
		types = new Transfer[] {TextTransfer.getInstance()};
		axisTableDropTarget.setTransfer(types);
		axisTableDropTargetListener = new AxisTableDropTargetListener();
		axisTableDropTarget.addDropListener(axisTableDropTargetListener);
		axisTableDropTarget.setDropTargetEffect(
				new TableDropTargetEffect(axisTableViewer.getTable()));
		
		// create context menu
		MenuManager axisTableMenuManager = new MenuManager();
		axisTableMenuManager.add(
				new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		axisTableViewer.getTable().setMenu(
			axisTableMenuManager.createContextMenu(axisTableViewer.getTable()));
		// register menu
		getSite().registerContextMenu(
			"de.ptb.epics.eve.viewer.views.deviceinspectorview.axistablepopup", 
			axisTableMenuManager, axisTableViewer);
		// end of: Motor Axes Composite
		
		// Detector Channels Composite
		detectorChannelsComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		detectorChannelsComposite.setLayout(gridLayout);
		
		channelMaxIcon = new Label(detectorChannelsComposite, SWT.NONE);
		channelMaxIcon.setImage(maximizeIcon);
		detectorIconMouseListener = new DetectorIconMouseListener();
		channelMaxIcon.addMouseListener(detectorIconMouseListener);
		
		channelIcon = new Label(detectorChannelsComposite, SWT.NONE);
		channelIcon.setImage(channelImage);
		
		channelLabel = new Label(detectorChannelsComposite, SWT.NONE);
		channelLabel.setText("Detector Channels:");
		
		channelTableViewer = new TableViewer(detectorChannelsComposite,
				SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		channelTableViewer.getTable().setLayoutData(gridData);
		channelTableViewer.getTable().setHeaderVisible(true);
		channelTableViewer.getTable().setLinesVisible(true);
		
		channelTableViewerComparator = new TableViewerComparator();
		channelTableNameColumnSelectionListener = 
			new ChannelTableNameColumnSelectionListener();
		
		createChannelTableColumns();
		channelTableContentProvider = 
				new CommonTableContentProvider(channelTableViewer, devices);
		channelTableViewer.setContentProvider(channelTableContentProvider);
		channelTableViewer.setInput(channelTableContentProvider);
		channelTableFocusListener = new ChannelTableFocusListener();
		channelTableViewer.getTable().addFocusListener(channelTableFocusListener);
		
		// drag source for item reordering
		DragSource channelTableDragSource = new DragSource(
				channelTableViewer.getTable(), DND.DROP_MOVE);
		types = new Transfer[] { TextTransfer.getInstance() };
		channelTableDragSource.setTransfer(types);
		channelTableDragSourceListener = new CommonTableDragSourceListener(
				channelTableViewer);
		channelTableDragSource.addDragListener(channelTableDragSourceListener);
		
		DropTarget channelTableDropTarget = new DropTarget(
				channelTableViewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE);
		channelTableDropTarget.setTransfer(types);
		channelTableDropTargetListener = new ChannelTableDropTargetListener();
		channelTableDropTarget.addDropListener(channelTableDropTargetListener);
		channelTableDropTarget.setDropTargetEffect(
			new TableDropTargetEffect(channelTableViewer.getTable()));
		
		// create context menu
		MenuManager channelTableMenuManager = new MenuManager();
		channelTableMenuManager.add(
				new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		channelTableViewer.getTable().setMenu(
			channelTableMenuManager.createContextMenu(channelTableViewer.getTable()));
		// register menu
		getSite().registerContextMenu(
			"de.ptb.epics.eve.viewer.views.deviceinspectorview.channeltablepopup", 
			channelTableMenuManager, channelTableViewer);
		// end of: Detector Channels Composite
		
		
		// Devices Composite
		devicesComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		devicesComposite.setLayout(gridLayout);
		
		deviceMaxIcon = new Label(devicesComposite, SWT.NONE);
		deviceMaxIcon.setImage(maximizeIcon);
		deviceIconMouseListener = new DeviceIconMouseListener();
		deviceMaxIcon.addMouseListener(deviceIconMouseListener);
		
		deviceIcon = new Label(devicesComposite, SWT.NONE);
		deviceIcon.setImage(deviceImage);
		
		deviceLabel = new Label(devicesComposite, SWT.NONE);
		deviceLabel.setText("Devices:");
		
		deviceTableViewer = new TableViewer(devicesComposite, SWT.BORDER
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		deviceTableViewer.getTable().setLayoutData(gridData);
		deviceTableViewer.getTable().setHeaderVisible(true);
		deviceTableViewer.getTable().setLinesVisible(true);
		
		deviceTableViewerComparator = new TableViewerComparator();
		deviceTableNameColumnSelectionListener = 
			new DeviceTableNameColumnSelectionListener();
		
		createDeviceTableColumns();
		deviceTableContentProvider = 
				new CommonTableContentProvider(deviceTableViewer, devices);
		deviceTableViewer.setContentProvider(deviceTableContentProvider);
		deviceTableViewer.setInput(deviceTableContentProvider);
		deviceTableFocusListener = new DeviceTableFocusListener();
		deviceTableViewer.getTable().addFocusListener(deviceTableFocusListener);
		
		// drag source for item reordering
		DragSource deviceTableDragSource = new DragSource(
				deviceTableViewer.getTable(), DND.DROP_MOVE);
		types = new Transfer[] { TextTransfer.getInstance() };
		deviceTableDragSource.setTransfer(types);
		deviceTableDragSourceListener = new CommonTableDragSourceListener(
				deviceTableViewer);
		deviceTableDragSource.addDragListener(deviceTableDragSourceListener);
		
		DropTarget deviceTableDropTarget = new DropTarget(
				deviceTableViewer.getTable(), DND.DROP_COPY | DND.DROP_MOVE);
		deviceTableDropTarget.setTransfer(types);
		deviceTableDropTargetListener = new DeviceTableDropTargetListener();
		deviceTableDropTarget.addDropListener(deviceTableDropTargetListener);
		deviceTableDropTarget.setDropTargetEffect(
			new TableDropTargetEffect(deviceTableViewer.getTable()));
		
		// create context menu
		MenuManager deviceTableMenuManager = new MenuManager();
		deviceTableMenuManager.add(
				new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		deviceTableViewer.getTable().setMenu(
			deviceTableMenuManager.createContextMenu(deviceTableViewer.getTable()));
		// register menu
		getSite().registerContextMenu(
			"de.ptb.epics.eve.viewer.views.deviceinspectorview.devicetablepopup", 
			deviceTableMenuManager, deviceTableViewer);
		// end of: Devices Composite
		
		// TODO the sashes allow resizing beyond dimensions that make sense 
		// (e.g. moving them to the top such that the table isn't visible 
		// anymore) 
		// if it is resized beyond this point it should be reset to a min height
		// below is an incomplete snippet
		/*
		// Set a minimum width on the sash so that the
		// tables are always visible.

		// First, find the sash child on the sashform...
		Control[] comps = sashForm.getChildren();
		for (Control comp : comps) {
			if (comp instanceof Sash) {
				final Sash sash = (Sash)comp;
				sash.addSelectionListener (new SelectionAdapter () {
					@Override public void widgetSelected (SelectionEvent event) {
						if (event.detail != SWT.DRAG) {
							// resize here
							sashForm.layout();
							logger.debug("min size");
						}
					}
				});
			}
		}
		*/
		
		axisTableColumnEditorActivationListener = 
				new AxisTableColumnEditorActivationListener();
		if(LOGGER.isDebugEnabled()) {
			axisTableViewer.getColumnViewerEditor().addEditorActivationListener(
				axisTableColumnEditorActivationListener);
		}
		
		restoreState();
		
		if(getViewSite().getSecondaryId() != null) {
			activeDeviceInspectorView = this.getViewSite().getSecondaryId();
		} else {
			activeDeviceInspectorView = "";
		}
		
		deviceInspectorViewPartListener = new DeviceInspectorViewPartListener();
		IPartService service = 
			(IPartService) getSite().getService(IPartService.class);
		service.addPartListener(deviceInspectorViewPartListener);
		
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);
	} // end of: createPartControl()
	
	/*
	 * 
	 */
	private void createAxisTableColumns() {
		ColumnViewerToolTipSupport.enableFor(
				axisTableViewer, ToolTip.NO_RECREATE);
		
		TableViewerColumn delColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		delColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "remove"));
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {return deleteIcon;}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				String name = ((CommonTableElement)element).getValue("name");
				return "Remove \"" + name + "\"";
			}
		});
		delColumn.getColumn().setWidth(22);
		
		TableViewerColumn nameColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("value");
			}
		});
		nameColumn.getColumn().setWidth(200);
		nameColumn.getColumn().addSelectionListener(
				axisTableNameColumnSelectionListener);
		
		TableViewerColumn valueColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Position");
		valueColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getPosition() != null && 
				   axis.getPosition().getAccess() != null) {
					return axis.getPosition().getAccess().getVariableID();
				}
				return null;
			}
		});
		valueColumn.getColumn().setWidth(100);
		
		// Engine Column should only be visible in the Engine Perspective
		if(getSite().getPage().getPerspective().getId().equals("EveEnginePerspective")) {
			TableViewerColumn engineColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
			engineColumn.getColumn().setText("Engine");
			engineColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "engine"));
			engineColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override public String getText(Object element) {
					return ((CommonTableElement) element).getValue("engine");
				}
			});
			engineColumn.getColumn().setWidth(100);
		}
		
		TableViewerColumn unitColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getUnit() != null && axis.getUnit().getAccess() != null) {
					return axis.getUnit().getAccess().getVariableID();
				}
				return null;
			}
		});
		unitColumn.getColumn().setWidth(40);
		
		TableViewerColumn gotoColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		gotoColumn.getColumn().setText("Go to");
		gotoColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "goto"));
		gotoColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("goto");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("goto");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getGoto() != null && axis.getGoto().getAccess() != null) {
					return axis.getGoto().getAccess().getVariableID();
				}
				return null;
			}
		});
		gotoColumn.getColumn().setWidth(100);
		
		TableViewerColumn stopColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		stopColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "stop"));
		stopColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {
				CommonTableElement cte = (CommonTableElement) element;
				if(cte.isConnected("stop")) {
					return stopIcon;
				}
				return stopIconDisabled;
			}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis)
					((CommonTableElement)element).getAbstractDevice();
				if(axis.getStop() != null && axis.getStop().getAccess() != null) {
					return axis.getStop().getAccess().getVariableID();
				}
				return null;
			}
		});
		stopColumn.getColumn().setWidth(22);
		
		TableViewerColumn statusColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		statusColumn.getColumn().setText("Status");
		statusColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "status"));
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("status");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("status");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getStatus() != null && axis.getStatus().getAccess() != null) {
					return axis.getStatus().getAccess().getVariableID();
				}
				return null;
			}
		});
		statusColumn.getColumn().setWidth(75);
		
		TableViewerColumn tweakRColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakRColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "tweakreverse"));
		tweakRColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {
				if(((CommonTableElement)element).isConnected("tweakvalue")) {
					return leftArrowIcon;
				}
				return leftArrowIconDisabled;
			}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getTweakReverse() != null && axis.getTweakReverse().getAccess() != null) {
					return axis.getTweakReverse().getAccess().getVariableID();
				}
				return null;
			}
		});
		tweakRColumn.getColumn().setWidth(22);
		
		TableViewerColumn tweakValueColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakValueColumn.getColumn().setText("Tweak");
		tweakValueColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "tweakvalue"));
		tweakValueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("tweakvalue");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("tweakvalue");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getTweakValue() != null && 
				   axis.getTweakValue().getAccess() != null) {
					return axis.getTweakValue().getAccess().getVariableID();
				}
				return null;
			}
		});
		tweakValueColumn.getColumn().setWidth(90);
		
		TableViewerColumn tweakFColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakFColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "tweakforward"));
		tweakFColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {
				if(((CommonTableElement)element).isConnected("tweakvalue")) {
					return rightArrowIcon;
				}
				return rightArrowIconDisabled;
			}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getTweakForward() !=  null && axis.getTweakForward().getAccess() != null) {
					return axis.getTweakForward().getAccess().getVariableID();
				}
				return null;
			}
		});
		tweakFColumn.getColumn().setWidth(22);

		TableViewerColumn defineValueColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		defineValueColumn.getColumn().setText("Define");
		defineValueColumn.setEditingSupport(
				new CommonTableEditingSupport(axisTableViewer, "define"));
		defineValueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("define");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("define");
			}
			@Override public String getToolTipText(Object element) {
				MotorAxis axis = (MotorAxis) 
						((CommonTableElement)element).getAbstractDevice();
				if(axis.getOffset() != null && 
				   axis.getOffset().getAccess() != null) {
					return axis.getOffset().getAccess().getVariableID();
				}
				return null;
			}
		});
		defineValueColumn.getColumn().setWidth(90);
		
		TableViewerColumn emptyColumn = 
				new TableViewerColumn(axisTableViewer, SWT.NONE);
		emptyColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return null;
			}
		});
		emptyColumn.getColumn().setWidth(1);
	} // end of: createMotorTableColumns
	
	/*
	 * Channel Table Columns
	 */
	private void createChannelTableColumns() {
		ColumnViewerToolTipSupport.enableFor(
				channelTableViewer, ToolTip.NO_RECREATE);
		
		TableViewerColumn delColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		delColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "remove"));
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {return deleteIcon;}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				String name = ((CommonTableElement)element).getValue("name");
				return "Remove \"" + name + "\"";
			}
		});
		delColumn.getColumn().setWidth(22);

		TableViewerColumn  nameColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("name");
			}
		});
		nameColumn.getColumn().setWidth(200);
		nameColumn.getColumn().addSelectionListener(
				channelTableNameColumnSelectionListener);
		
		TableViewerColumn valueColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Value");
		valueColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
			@Override public String getToolTipText(Object element) {
				DetectorChannel channel = (DetectorChannel) 
						((CommonTableElement)element).getAbstractDevice(); 
				if(channel.getRead() != null && channel.getRead().getAccess() != null) {
					return channel.getRead().getAccess().getVariableID();
				}
				return null;
			}
		});
		valueColumn.getColumn().setWidth(120);
		
		// Engine Column should only be visible in the Engine Perspective
		if(getSite().getPage().getPerspective().getId().equals("EveEnginePerspective")) {
			TableViewerColumn engineColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
			engineColumn.getColumn().setText("Engine");
			engineColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "engine"));
			engineColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override public String getText(Object element) {
					return ((CommonTableElement) element).getValue("engine");
				}
				@Override public Color getForeground(Object element) {
					return ((CommonTableElement) element).getSeverityColor("value");
				}
			});
			engineColumn.getColumn().setWidth(100);
		}
		
		TableViewerColumn unitColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
			@Override public String getToolTipText(Object element) {
				DetectorChannel channel = (DetectorChannel) 
						((CommonTableElement)element).getAbstractDevice();
				if(channel.getUnit() != null && channel.getUnit().getAccess() != null) {
					return channel.getUnit().getAccess().getVariableID();
				}
				return null;
			}
		});
		unitColumn.getColumn().setWidth(50);

		TableViewerColumn triggerColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		triggerColumn.getColumn().setText("");
		triggerColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "trigger"));
		triggerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {
				if (((CommonTableElement)element).isConnected("trigger")) {
					return playIcon;
				}
				return playIconDisabled;
			}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				DetectorChannel channel = (DetectorChannel) 
						((CommonTableElement)element).getAbstractDevice();
				if(channel.getTrigger() != null && 
						channel.getTrigger().getAccess() != null) {
					return channel.getTrigger().getAccess().getVariableID();
				}
				// triggerPv is the detector of the channel
				Detector detector = channel.getDetector();
				if(detector.getTrigger() != null && 
						detector.getTrigger().getAccess() != null) {
					return detector.getTrigger().getAccess().getVariableID();
				}
				return null;
			}
		});
		triggerColumn.getColumn().setWidth(22);
		
		TableViewerColumn stopColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		stopColumn.getColumn().setText("");
		stopColumn.setEditingSupport(
				new CommonTableEditingSupport(channelTableViewer, "stop"));
		stopColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {
				if (((CommonTableElement)element).isConnected("stop")) {
					return stopIcon;
				}
				return stopIconDisabled;
			}
			@Override public String getText(Object element) { return null; }
			@Override public String getToolTipText(Object element) {
				DetectorChannel channel = (DetectorChannel) 
						((CommonTableElement)element).getAbstractDevice();
				if (channel.getStop() != null &&
						channel.getStop().getAccess() != null &&
						!channel.getStop().getAccess().getVariableID().equals("")) {
					return channel.getStop().getAccess().getVariableID();
				}
				// stopPV is the detector of the channel
				Detector detector = channel.getDetector();
				if (detector.getStop() != null &&
						detector.getStop().getAccess() != null && 
						!detector.getStop().getAccess().getVariableID().equals("")) {
					return detector.getStop().getAccess().getVariableID();
				}
				return null;
			}
		});
		stopColumn.getColumn().setWidth(22);
		
		TableViewerColumn statusColumn = 
				new TableViewerColumn(channelTableViewer, SWT.NONE);
		statusColumn.getColumn().setText("Status");
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("status");
			}
			@Override public String getToolTipText(Object element) {
				DetectorChannel channel = (DetectorChannel) 
						((CommonTableElement)element).getAbstractDevice();
				if (channel.getStatus() != null &&
						channel.getStatus().getAccess() != null &&
						!channel.getStatus().getAccess().getVariableID().equals("")) {
					return channel.getStatus().getAccess().getVariableID();
				}
				Detector detector = channel.getDetector();
				if (detector.getStatus() != null &&
						detector.getStatus().getAccess() != null && 
						!detector.getStatus().getAccess().getVariableID().equals("")) {
					return detector.getStatus().getAccess().getVariableID();
				}
				return null;
			}
		});
		statusColumn.getColumn().setWidth(70);
		
		TableViewerColumn emptyColumn = 
			new TableViewerColumn(channelTableViewer, SWT.NONE);
		emptyColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return null;
			}
		});
		emptyColumn.getColumn().setWidth(1);
	} // end of: createChannelTableColumns
	
	/*
	 * Device Table Columns
	 */
	private void createDeviceTableColumns() {
		ColumnViewerToolTipSupport.enableFor(
				deviceTableViewer, ToolTip.NO_RECREATE);
		
		TableViewerColumn delColumn = 
				new TableViewerColumn(deviceTableViewer, SWT.NONE);
		delColumn.setEditingSupport(
				new CommonTableEditingSupport(deviceTableViewer, "remove"));
		delColumn.getColumn().setMoveable(true);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public Image getImage(Object element) {return deleteIcon;}
			@Override public String getText(Object element) {return null;}
			@Override public String getToolTipText(Object element) {
				String name = ((CommonTableElement)element).getValue("name");
				return "Remove \"" + name + "\"";
			}
		});
		delColumn.getColumn().setWidth(22);
		
		TableViewerColumn nameColumn = 
				new TableViewerColumn(deviceTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setMoveable(true);
		nameColumn.setEditingSupport(
				new CommonTableEditingSupport(deviceTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("name");
			}
		});
		nameColumn.getColumn().setWidth(200);
		nameColumn.getColumn().addSelectionListener(
				deviceTableNameColumnSelectionListener);
		
		TableViewerColumn valueColumn = 
				new TableViewerColumn(deviceTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setMoveable(true);
		valueColumn.setEditingSupport(
				new CommonTableEditingSupport(deviceTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			@Override public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
			@Override public String getToolTipText(Object element) {
				Device device = (Device) 
						((CommonTableElement)element).getAbstractDevice();
				if(device.getValue() != null && device.getValue().getAccess() != null) {
					return device.getValue().getAccess().getVariableID();
				}
				return null;
			}
		});
		valueColumn.getColumn().setWidth(100);

		TableViewerColumn unitColumn = 
				new TableViewerColumn(deviceTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.getColumn().setMoveable(true);
		unitColumn.setEditingSupport(
				new CommonTableEditingSupport(deviceTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
			@Override public String getToolTipText(Object element) {
				Device device = (Device) 
						((CommonTableElement)element).getAbstractDevice();
				if(device.getUnit() != null && device.getUnit().getAccess() != null) {
					return device.getUnit().getAccess().getVariableID();
				}
				return null;
			}
		});
		unitColumn.getColumn().setWidth(35);
		
		TableViewerColumn emptyColumn = 
			new TableViewerColumn(deviceTableViewer, SWT.NONE);
		emptyColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override public String getText(Object element) {
				return null;
			}
		});
		emptyColumn.getColumn().setWidth(1);
	} // end of: createDeviceTableColumns
	
	/**
	 * Renames the <code>DeviceInspectorView</code>.
	 * 
	 * @param newName the name the view should get
	 */
	public void rename(String newName) {
		this.setPartName(newName);
	}
	
	/**
	 * Resets all layout changes made:
	 * <ul>
	 *   <li>resets maximized window (if one)</li>
	 *   <li>makes all table heights equal</li>
	 * </ul>
	 */
	public void resetLayout() {
		motorAxesCompositeMaximized = false;
		detectorChannelsCompositeMaximized = false;
		devicesCompositeMaximized = false;
		motorMaxIcon.setImage(maximizeIcon);
		channelMaxIcon.setImage(maximizeIcon);
		deviceMaxIcon.setImage(maximizeIcon);
		sashForm.setMaximizedControl(null);
		
		int[] weights = {1,1,1};
		sashForm.setWeights(weights);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		activeDeviceInspectorView = this.getViewSite().getSecondaryId();
	}
	
	/**
	 * Removes all elements in the axes table.
	 */
	public void clearMotorAxesTable() {
		CommonTableContentProvider ctcp = (CommonTableContentProvider)
				this.axisTableViewer.getContentProvider();
		for(Object o : ctcp.getElements(null)) {
			ctcp.removeElement(o);
		}
	}
	
	/**
	 * Removes all elements in the channels table.
	 */
	public void clearDetectorChannelsTable() {
		CommonTableContentProvider ctcp = (CommonTableContentProvider)
				this.channelTableViewer.getContentProvider();
		for(Object o : ctcp.getElements(null)) {
			ctcp.removeElement(o);
		}
	}
	
	/**
	 * Removes all elements in the devices table
	 */
	public void clearDevicesTable() {
		CommonTableContentProvider ctcp = (CommonTableContentProvider)
				this.deviceTableViewer.getContentProvider();
		for(Object o : ctcp.getElements(null)) {
			ctcp.removeElement(o);
		}
	}
	
	/**
	 * Adds an {@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} as 
	 * follows:
	 * <ul>
	 *  <li>a {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} is added 
	 *  	if it isn't already present in the motor axes table.</li>
	 *  <li>a {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} is
	 *  	added if it isn't already present in the detector channels table.</li>
	 *  <li>for a {@link de.ptb.epics.eve.data.measuringstation.Motor} all of 
	 *  	its axes that aren't already present are added to the motor axes 
	 *  	table</li>
	 *  <li>for a {@link de.ptb.epics.eve.data.measuringstation.Detector} all of 
	 *  	its channels that aren't already present are added to the detector 
	 *  	channels table</li>
	 *  <li>a {@link de.ptb.epics.eve.data.measuringstation.Device} is added if 
	 *  	it isn't already present in the devices table</li>
	 * </ul>
	 * 
	 * @precondition <code>device</code> is not <code>null</code>
	 * @param device the 
	 * 		{@link de.ptb.epics.eve.data.measuringstation.AbstractDevice} that 
	 * 		should be added
	 */
	public void addAbstractDevice(final AbstractDevice device) {
		if(device != null) {
			if(device instanceof MotorAxis) {
				if(!devices.contains(device)) {
					addMotorAxisEntry(device);
				}
			} else if(device instanceof DetectorChannel) {
				if(!devices.contains(device)) {
					addDetectorChannelEntry(device);
				}
			} else if(device instanceof Device) {
				if(!devices.contains(device)) {
					addDeviceEntry(device);
				}
			} else if(device instanceof Motor) {
				final Motor motor = (Motor)device;
				for(MotorAxis axis : motor.getAxes()) {
					if (axis.getClassName().isEmpty() && 
						!devices.contains(axis)) {
							addMotorAxisEntry(axis);
					}
				}
			} else if(device instanceof Detector) {
				final Detector detector = (Detector)device;
				for(DetectorChannel channel : detector.getChannels()) {
					if (channel.getClassName().isEmpty() && 
						!devices.contains(channel)) {
							addDetectorChannelEntry(channel);
					}
				}
			}
		}
	}
	
	/*
	 * called by addAbstractDevice
	 */
	private void addMotorAxisEntry(final AbstractDevice device) {
		CommonTableElement cte = 
				new CommonTableElement(device, axisTableViewer);
		if(axisTableContentProvider.addElement(cte)) {
			cte.init();
			this.devices.add(device);
		}

	}
	
	/*
	 * called by addAbstractDevice
	 */
	private void addDetectorChannelEntry(final AbstractDevice device) {
		CommonTableElement cte = 
				new CommonTableElement(device, channelTableViewer);
		if(channelTableContentProvider.addElement(cte)) {
			cte.init();
			this.devices.add(device);
		}
	}

	/*
	 * called by addAbstractDevice
	 */
	private void addDeviceEntry(final AbstractDevice device) {
		CommonTableElement cte = 
				new CommonTableElement(device, deviceTableViewer);
		if(deviceTableContentProvider.addElement(cte)) {
			cte.init();
			this.devices.add(device);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(final IMemento memento) {
		// save maximized states
		memento.putBoolean(
				"motorAxesCompositeMaximized", motorAxesCompositeMaximized);
		memento.putBoolean("detectorChannelsCompositeMaximized", 
				detectorChannelsCompositeMaximized);
		memento.putBoolean("devicesCompositeMaximized", 
				devicesCompositeMaximized);
		
		// save composite heights
		memento.putInteger("motorAxesWeight", sashForm.getWeights()[0]);
		memento.putInteger("detectorChannelsWeight", sashForm.getWeights()[1]);
		memento.putInteger("devicesWeight", sashForm.getWeights()[2]);
		
		// save table elements
		StringBuffer devicesString = new StringBuffer();
		for(AbstractDevice dev : devices) {
			devicesString.append(dev.getFullIdentifyer());
			devicesString.append(",");
		}
		memento.putString("devices", devicesString.toString());
		
		// save sorting
		memento.putInteger("axisTableSortState", axisTableSortState);
		memento.putInteger("channelTableSortState", channelTableSortState);
		memento.putInteger("deviceTableSortState", deviceTableSortState);
		
		// save part name (view title)
		memento.putString("partName", this.getPartName());
		
		if(LOGGER.isDebugEnabled()) {
			LOGGER.debug("saved Maximized States: " + 
					"MotorComp " + motorAxesCompositeMaximized + ", " + 
					"DetComp " + detectorChannelsCompositeMaximized + ", " + 
					"DevComp " + devicesCompositeMaximized);
			
			LOGGER.debug("saved Weights: " + 
						"MotorComp " + sashForm.getWeights()[0] + ", " +
						"DetComp " + sashForm.getWeights()[1] + ", " + 
						"DevComp " + sashForm.getWeights()[2]);
			LOGGER.debug("saved devices: " + devicesString.toString());
		}
	}
	
	/*
	 * gets called at the end of createPartControl() to restore the state 
	 * saved in the memento.
	 */
	private void restoreState() {
		if(memento == null) {
			// nothing saved
			return;
		}
		
		// restore part name (view title)
		if(memento.getString("partName") != null) {
			this.setPartName(memento.getString("partName"));
		}
		
		// restore sorting
		axisTableSortState = (memento.getInteger("axisTableSortState") == null) 
								? 0 
								: memento.getInteger("axisTableSortState");
		if(axisTableSortState != 0) {
			axisTableViewerComparator.setDirection(
					axisTableSortState == 1
					? TableViewerComparator.ASCENDING
					: TableViewerComparator.DESCENDING);
			axisTableViewer.getTable().getColumn(1).setImage(
					axisTableSortState == 1 ? ascending : descending);
			axisTableViewer.setComparator(axisTableViewerComparator);
			axisTableViewer.refresh();
		}
		channelTableSortState = (memento.getInteger("channelTableSortState") == null)
								? 0 
								: memento.getInteger("channelTableSortState");
		if(channelTableSortState != 0) {
			channelTableViewerComparator.setDirection(
					channelTableSortState == 1
					? TableViewerComparator.ASCENDING
					: TableViewerComparator.DESCENDING);
			channelTableViewer.getTable().getColumn(1).setImage(
					channelTableSortState == 1 ? ascending : descending);
			channelTableViewer.setComparator(channelTableViewerComparator);
			channelTableViewer.refresh();
		}
		deviceTableSortState = (memento.getInteger("deviceTableSortState") == null) 
								? 0
								: memento.getInteger("deviceTableSortState");
		if(deviceTableSortState != 0) {
			deviceTableViewerComparator.setDirection(
					axisTableSortState == 1
					? TableViewerComparator.ASCENDING
					: TableViewerComparator.DESCENDING);
			deviceTableViewer.getTable().getColumn(1).setImage(
					deviceTableSortState == 1 ? ascending : descending);
			deviceTableViewer.setComparator(deviceTableViewerComparator);
			deviceTableViewer.refresh();
		}
		
		// restore heights of the composites
		int[] weights = new int[3];
		weights[0] = (memento.getInteger("motorAxesWeight") == null) 
						? 1 
						: memento.getInteger("motorAxesWeight");
		weights[1] = (memento.getInteger("detectorChannelsWeight") == null) 
						? 1 : memento.getInteger("detectorChannelsWeight");
		weights[2] = (memento.getInteger("devicesWeight") == null) 
						? 1 : memento.getInteger("devicesWeight");
		sashForm.setWeights(weights);
		
		// restore maximized status
		motorAxesCompositeMaximized = 
			(memento.getBoolean("motorAxesCompositeMaximized") == null) 
				? false : memento.getBoolean("motorAxesCompositeMaximized");
		detectorChannelsCompositeMaximized = 
			(memento.getBoolean("detectorChannelsCompositeMaximized") == null)
				? false : memento.getBoolean("detectorChannelsCompositeMaximized");
		devicesCompositeMaximized = 
			(memento.getBoolean("devicesCompositeMaximized") == null) 
				? false : memento.getBoolean("devicesCompositeMaximized");
		
		if(motorAxesCompositeMaximized) {
			motorMaxIcon.setImage(restoreIcon);
			sashForm.setMaximizedControl(motorAxesComposite);
		}
		if(detectorChannelsCompositeMaximized) {
			channelMaxIcon.setImage(restoreIcon);
			sashForm.setMaximizedControl(detectorChannelsComposite);
		}
		if(devicesCompositeMaximized) {
			deviceMaxIcon.setImage(restoreIcon);
			sashForm.setMaximizedControl(devicesComposite);
		}
		
		final List<AbstractDevice> devs = 
				new ArrayList<AbstractDevice>(devices);
		this.devices.clear();
		for(final AbstractDevice d : devs) {
			this.addAbstractDevice(d);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		IPartService service = 
			(IPartService) getSite().getService(IPartService.class);
		service.removePartListener(deviceInspectorViewPartListener);
		
		axisTableViewer.getColumnViewerEditor().removeEditorActivationListener(
				axisTableColumnEditorActivationListener);
		
		axisTableColumnEditorActivationListener = null;
		getSite().setSelectionProvider(null);
		selectionProviderWrapper = null;
		
		super.dispose();
	}

	/**
	 * @author Marcus Michalsky
	 */
	private class MotorIconMouseListener implements MouseListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDown(MouseEvent e) {
			if(motorAxesCompositeMaximized) {
				motorMaxIcon.setImage(maximizeIcon);
				motorMaxIcon.getParent().layout();
				sashForm.setMaximizedControl(null);
				motorAxesCompositeMaximized = false;
			} else {
				motorMaxIcon.setImage(restoreIcon);
				sashForm.setMaximizedControl(motorAxesComposite);
				motorAxesCompositeMaximized = true;
			}
		}
		
		/** {@inheritDoc} */
		@Override
		public void mouseUp(MouseEvent e) {	
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 */
	private class DetectorIconMouseListener implements MouseListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDown(MouseEvent e) {
			if(detectorChannelsCompositeMaximized) {
				channelMaxIcon.setImage(maximizeIcon);
				channelMaxIcon.getParent().layout();
				sashForm.setMaximizedControl(null);
				detectorChannelsCompositeMaximized = false;
			} else {
				channelMaxIcon.setImage(restoreIcon);
				sashForm.setMaximizedControl(detectorChannelsComposite);
				detectorChannelsCompositeMaximized = true;
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseUp(MouseEvent e) {
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 */
	private class DeviceIconMouseListener implements MouseListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDown(MouseEvent e) {
			if(devicesCompositeMaximized) {
				deviceMaxIcon.setImage(maximizeIcon);
				deviceMaxIcon.getParent().layout();
				sashForm.setMaximizedControl(null);
				devicesCompositeMaximized = false;
			} else {
				deviceMaxIcon.setImage(restoreIcon);
				sashForm.setMaximizedControl(devicesComposite);
				devicesCompositeMaximized = true;
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseUp(MouseEvent e) {	
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.18
	 */
	private class AxisTableDropTargetListener extends CommonTableDropTargetListener {
		
		/**
		 * Constructor.
		 */
		public AxisTableDropTargetListener() {
			super(axisTableViewer, axisTableSortState);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void drop(DropTargetEvent event) {
			if((event.operations & DND.DROP_COPY) != 0) {
				if(LOGGER.isDebugEnabled()) {
					LOGGER.debug("drop data: " + event.data);
				}
				
				IMeasuringStation measuringstation = getSite().getPage().
						getPerspective().getId().equals("EveDevicePerspective")
						? Activator.getDefault().getMeasuringStation()
						: Activator.getDefault().getCurrentScanDescription().
								getMeasuringStation();
				
				boolean refuse = true;
				
				for(String s : ((String)event.data).split(",")) {
					if(s.startsWith(DragNDropPrefix.MOTOR.toString())) {
						// String contains a motor -> add each axis
						Motor m = (Motor) measuringstation.
								getAbstractDeviceByFullIdentifyer(
										s.substring(1, s.length()));
						if(m != null) {
							for(MotorAxis ma : m.getAxes()) {
								if(ma.getClassName().isEmpty()) {
									addMotorAxisEntry(ma);
								}
							}
						}
						refuse = false;
					} else if(s.startsWith(DragNDropPrefix.MOTOR_AXIS.toString())) {
						// String contains a motor axis -> add it
						MotorAxis ma = (MotorAxis) measuringstation.
								getAbstractDeviceByFullIdentifyer(
										s.substring(1, s.length()));
						addMotorAxisEntry(ma);
						refuse = false;
					}
				}
				
				if(refuse) {
					LOGGER.debug("Drop refused");
					event.detail = DND.DROP_NONE;
				}
			} else if ((event.operations & DND.DROP_MOVE) != 0) {
				if (axisTableSortState != 0) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				if (event.item instanceof TableItem) {
					super.move(event);
				}
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.18
	 */
	private class ChannelTableDropTargetListener extends
			CommonTableDropTargetListener {
		
		public ChannelTableDropTargetListener() {
			super(channelTableViewer, channelTableSortState);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void drop(DropTargetEvent event) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("drop data: " + event.data);
				}

				IMeasuringStation measuringstation = getSite().getPage()
						.getPerspective().getId()
						.equals("EveDevicePerspective") ? Activator
						.getDefault().getMeasuringStation() : Activator
						.getDefault().getCurrentScanDescription()
						.getMeasuringStation();

				boolean refuse = true;

				for (String s : ((String) event.data).split(",")) {
					if (s.startsWith(DragNDropPrefix.DETECTOR.toString())) {
						// String contains a detector -> add its channels
						Detector d = (Detector) measuringstation
								.getAbstractDeviceByFullIdentifyer(s.substring(
										1, s.length()));
						if (d != null) {
							for (DetectorChannel ch : d.getChannels()) {
								if (ch.getClassName().isEmpty()) {
									addDetectorChannelEntry(ch);
								}
							}
						}
						refuse = false;
					} else if (s.startsWith(DragNDropPrefix.DETECTOR_CHANNEL
							.toString())) {
						// String contains a detector channel -> add it
						DetectorChannel ch = (DetectorChannel) measuringstation
								.getAbstractDeviceByFullIdentifyer(s.substring(
										1, s.length()));
						addDetectorChannelEntry(ch);
						refuse = false;
					}
				}

				if (refuse) {
					LOGGER.debug("Drop refused");
					event.detail = DND.DROP_NONE;
				}
			} else if ((event.operations & DND.DROP_MOVE) != 0) {
				if (tableSortState != 0) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				if (event.item instanceof TableItem) {
					super.move(event);
				}
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.18
	 */
	private class DeviceTableDropTargetListener extends
			CommonTableDropTargetListener {

		public DeviceTableDropTargetListener() {
			super(deviceTableViewer, deviceTableSortState);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void drop(DropTargetEvent event) {
			if ((event.operations & DND.DROP_COPY) != 0) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("drop data: " + event.data);
				}

				IMeasuringStation measuringstation = getSite().getPage()
						.getPerspective().getId()
						.equals("EveDevicePerspective") ? Activator
						.getDefault().getMeasuringStation() : Activator
						.getDefault().getCurrentScanDescription()
						.getMeasuringStation();

				boolean refuse = true;

				for (String s : ((String) event.data).split(",")) {
					if (s.startsWith(DragNDropPrefix.DEVICE.toString())) {
						// String contains a device -> add it
						Device d = (Device) measuringstation
								.getAbstractDeviceByFullIdentifyer(s.substring(
										1, s.length()));
						if (d != null) {
							addDeviceEntry(d);
						}
						refuse = false;
					}
				}

				if (refuse) {
					LOGGER.debug("Drop refused");
					event.detail = DND.DROP_NONE;
				}
			} else if ((event.operations & DND.DROP_MOVE) != 0) {
				if (axisTableSortState != 0) {
					event.detail = DND.DROP_NONE;
					event.feedback = DND.FEEDBACK_NONE;
					return;
				}
				if (event.item instanceof TableItem) {
					super.move(event);
				}
			} else {
				event.detail = DND.DROP_NONE;
			}
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 */
	private class AxisTableFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent arg0) {
			selectionProviderWrapper.setSelectionProvider(axisTableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent arg0) {
			axisTableViewer.getTable().deselectAll();
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 */
	private class ChannelTableFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent arg0) {
			selectionProviderWrapper.setSelectionProvider(channelTableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent arg0) {
			channelTableViewer.getTable().deselectAll();
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 */
	private class DeviceTableFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent arg0) {
			selectionProviderWrapper.setSelectionProvider(deviceTableViewer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent arg0) {
			deviceTableViewer.getTable().deselectAll();
		}
	}
	
	/**
	 * 
	 * 
	 * @author Marcus Michalsky
	 * @since 0.4.2
	 */
	private class AxisTableNameColumnSelectionListener implements SelectionListener {
		
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
			LOGGER.debug("name column (axes table) clicked");
			LOGGER.debug("old axis table sort state: " + axisTableSortState);
			switch(axisTableSortState) {
				case 0: // was no sorting -> now ascending
						axisTableViewerComparator.setDirection(
								TableViewerComparator.ASCENDING);
						axisTableViewer.setComparator(axisTableViewerComparator);
						axisTableViewer.getTable().getColumn(1).
								setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						axisTableViewerComparator.setDirection(
								TableViewerComparator.DESCENDING);
						axisTableViewer.setComparator(axisTableViewerComparator);
						axisTableViewer.refresh();
						axisTableViewer.getTable().getColumn(1).
								setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						axisTableViewer.setComparator(null);
						axisTableViewer.getTable().getColumn(1).setImage(null);
						break;
			}
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			axisTableSortState = ++axisTableSortState % 3;
			axisTableDropTargetListener.setSortState(axisTableSortState);
			LOGGER.debug("new axis table sort state: " + axisTableSortState);
		}
	}
	
	/**
	 *
	 *
	 * @author Marcus Michalsky
	 * @since 0.4.2
	 */
	private class ChannelTableNameColumnSelectionListener implements SelectionListener {
		
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
			LOGGER.debug("name column (channels table) clicked");
			LOGGER.debug("old channels table sort state: " + channelTableSortState);
			switch(channelTableSortState) {
				case 0: // was no sorting -> now ascending
						channelTableViewerComparator.setDirection(
								TableViewerComparator.ASCENDING);
						channelTableViewer.setComparator(channelTableViewerComparator);
						channelTableViewer.getTable().getColumn(1).
								setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						channelTableViewerComparator.setDirection(
								TableViewerComparator.DESCENDING);
						channelTableViewer.setComparator(channelTableViewerComparator);
						channelTableViewer.refresh();
						channelTableViewer.getTable().getColumn(1).
								setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						channelTableViewer.setComparator(null);
						channelTableViewer.getTable().getColumn(1).setImage(null);
						break;
			}
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			channelTableSortState = ++channelTableSortState % 3;
			channelTableDropTargetListener.setSortState(channelTableSortState);
			LOGGER.debug("new channel table sort state: " + channelTableSortState);
		}
	}
	
	/**
	 *
	 *
	 * @author Marcus Michalsky
	 * @since 0.4.2
	 */
	private class DeviceTableNameColumnSelectionListener implements SelectionListener {
		
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
			LOGGER.debug("name column (devices table) clicked");
			LOGGER.debug("old devices table sort state: " + deviceTableSortState);
			switch(deviceTableSortState) {
				case 0: // was no sorting -> now ascending
						deviceTableViewerComparator.setDirection(
								TableViewerComparator.ASCENDING);
						deviceTableViewer.setComparator(deviceTableViewerComparator);
						deviceTableViewer.getTable().getColumn(1).
								setImage(ascending);
						break;
				case 1: // was ascending -> now descending
						deviceTableViewerComparator.setDirection(
								TableViewerComparator.DESCENDING);
						deviceTableViewer.setComparator(deviceTableViewerComparator);
						deviceTableViewer.refresh();
						deviceTableViewer.getTable().getColumn(1).
								setImage(descending);
						break;
				case 2: // was descending -> now no sorting
						deviceTableViewer.setComparator(null);
						deviceTableViewer.getTable().getColumn(1).setImage(null);
						break;
			}
			// set is {0,1,2}
			// if it becomes 3 it has to be 0 again
			// but before the state has to be increased to the new state
			deviceTableSortState = ++deviceTableSortState % 3;
			deviceTableDropTargetListener.setSortState(deviceTableSortState);
			LOGGER.debug("new device table sort state: " + deviceTableSortState);
		}
	}
	
	/**
	 * 
	 */
	private class AxisTableColumnEditorActivationListener 
								extends ColumnViewerEditorActivationListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void beforeEditorActivated(
				ColumnViewerEditorActivationEvent event) {
			LOGGER.debug("before editor activated");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void afterEditorActivated(
				ColumnViewerEditorActivationEvent event) {
			LOGGER.debug("after editor activated");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void beforeEditorDeactivated(
				ColumnViewerEditorDeactivationEvent event) {
			LOGGER.debug("before editor deactivated");
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void afterEditorDeactivated(
				ColumnViewerEditorDeactivationEvent event) {
			LOGGER.debug("after editor deactivated");
			if(event.eventType == ColumnViewerEditorDeactivationEvent.EDITOR_CANCELED) {
				LOGGER.debug("(editor canceled)");
			} else if(event.eventType == ColumnViewerEditorDeactivationEvent.EDITOR_SAVED) {
				LOGGER.debug("(editor saved)");
			}
		}
	}
}