package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
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
import de.ptb.epics.eve.viewer.actions.NewDeviceInspectorAction;
import de.ptb.epics.eve.viewer.actions.RenameDeviceInspector;

/**
 * This class represents the DeviceInspector view that provides interaction with motor axes
 * 
 * @author Stephan Rehfeld <stephan.rehfeld@ptb.de>
 *
 */
public class DeviceInspectorViewer extends ViewPart {

	private ExpandBar bar1;
	private ExpandBar bar2;
	private ExpandBar bar3;
	private Sash sash1;
	private Sash sash2;
	
	private List< AbstractDevice > devices;

	private TableViewer axisTableViewer;
	private TableViewer channelTableViewer;
	private TableViewer deviceTableViewer;
	private CommonTableContentProvider deviceContentProvider;
	private CommonTableContentProvider axisContentProvider;
	private CommonTableContentProvider channelContentProvider;
	
	private NewDeviceInspectorAction newDeviceInspectorAction;
	private RenameDeviceInspector renameDeviceInspectorAction;
	
	private boolean axisExpanded = true;
	private boolean channelExpanded = true;
	private boolean deviceExpanded = true;

	@Override
	public void createPartControl( final Composite parent ) {
		
		if( this.devices == null ) {
			this.devices = new ArrayList< AbstractDevice >();
		}
		
		final Image deleteIcon = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ).createImage();
		final Image leftArrowIcon = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_BACK ).createImage();
		final Image rightArrowIcon = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_FORWARD ).createImage();
		final Image playIcon = Activator.getDefault().getImageRegistry().get("PLAY16");
		final Image stopIcon = Activator.getDefault().getImageRegistry().get("STOP16");

		parent.setLayout( new GridLayout() );
		GridData gridData;

		DropTarget target = new DropTarget( parent, DND.DROP_COPY );
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final Transfer[] types = new Transfer[] { textTransfer };
		target.setTransfer( types );
		
		DropTargetListener dropListener = new DropTargetListener() {

			public void dragEnter( final DropTargetEvent event ) {
				System.out.println( "DragEnter" );
				if( (event.operations & DND.DROP_COPY) != 0 ) {
					for( int i = 0; i < event.dataTypes.length; ++i ) {
						if( textTransfer.isSupportedType( event.dataTypes[i] ) ) {
							event.detail = DND.DROP_COPY;
							event.currentDataType = event.dataTypes[i];
							break;
						}
					}
				}
			}

			public void dragLeave( final DropTargetEvent event ) {
				
			}

			public void dragOperationChanged( final DropTargetEvent event ) {
				
			}

			public void dragOver( final DropTargetEvent event ) {
				
			}

			public void drop( final DropTargetEvent event ) {
				System.out.println( event.getSource() );
				if( textTransfer.isSupportedType( event.currentDataType ) ) {
					IViewReference[] ref = getSite().getPage().getViewReferences();
					MeasuringStationView view = null;
					final String[] comp = ((String)event.data).split( "," );
					if( comp.length == 2 ) {
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( comp[0] ) ) {
								view = (MeasuringStationView)ref[i].getPart( false );
								final IMeasuringStation measuringStation = view.getMeasuringStation();
								if (measuringStation.getClassNameList().contains( comp[1] ) ) {
									for (AbstractDevice absdevice : measuringStation.getDeviceList(comp[1])) {
										if (absdevice instanceof MotorAxis ) addMotorAxisEntry((MotorAxis) absdevice );
										else if (absdevice instanceof DetectorChannel )
											addDetectorChannelEntry((DetectorChannel) absdevice );
										else if (absdevice instanceof Device ) 
											addDeviceEntry((Device) absdevice );
										else if( absdevice instanceof Motor )
											for (MotorAxis axis : ((Motor)absdevice).getAxes()) 
												addMotorAxisEntry(axis);
										else if( absdevice instanceof Detector )
											for (DetectorChannel channel : ((Detector)absdevice).getChannels()) 
												addDetectorChannelEntry( channel );
									}
								} else {
									AbstractDevice device = measuringStation.getAbstractDeviceByFullIdentifyer( comp[1] );
									addAbstractDevice( device );
								}
								break;
							}
							
						}
					}
				}
				
			}
			
			public void dropAccept( final DropTargetEvent event ) {
				
			}
		
		};
		target.addDropListener( dropListener );

		this.bar1 = new ExpandBar( parent, SWT.V_SCROLL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar1.setLayoutData( gridData );

		target = new DropTarget( this.bar1, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
						
//		Composite tableComposite = new Composite(bar1, SWT.NONE);
//		Composite tableComposite = new Composite(bar1, SWT.NONE);
		ScrolledComposite tableCompositeScr = new ScrolledComposite(bar1, SWT.BORDER | SWT.V_SCROLL);

		Composite tableComposite = new Composite(tableCompositeScr, SWT.NONE);
		
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
//		axisTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		axisTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.V_SCROLL);

		tableCompositeScr.setContent(tableComposite);

//		ExpandItem eitem = new ExpandItem ( this.bar1, SWT.NONE, 0);
		ExpandItem eitem = new ExpandItem ( this.bar1, SWT.V_SCROLL | SWT.RESIZE, 0);
		eitem.setText("Axes");
		eitem.setHeight( 300);
		eitem.setControl( tableCompositeScr );
		eitem.setExpanded( axisExpanded );

//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.heightHint = 200;
//		gridData.widthHint = 330;
//		tableCompositeScr.setLayoutData( gridData );

	    tableCompositeScr.setExpandHorizontal(true);
	    tableCompositeScr.setExpandVertical(true);
	    tableCompositeScr.setMinWidth(200);
	    tableCompositeScr.setMinHeight(200);

		
		axisTableViewer.getTable().setHeaderVisible(true);
		axisTableViewer.getTable().setLinesVisible(true);

		axisContentProvider = new CommonTableContentProvider(axisTableViewer, eitem, this.devices );
		axisTableViewer.setContentProvider(axisContentProvider);

		TableViewerColumn delColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
//		delColumn.getColumn().setText("Remove");
		delColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "remove"));
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return deleteIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(delColumn.getColumn(), new ColumnPixelData(22));

		TableViewerColumn nameColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("value");
			}
		});
		tableColumnLayout.setColumnData(nameColumn.getColumn(), new ColumnPixelData(100));

		TableViewerColumn valueColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Position");
		valueColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
		});
		tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnPixelData(100));

		TableViewerColumn engineColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		engineColumn.getColumn().setText("Engine");
		engineColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "engine"));
		engineColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("engine");
			}
		});
		tableColumnLayout.setColumnData(engineColumn.getColumn(), new ColumnPixelData(100));

		TableViewerColumn unitColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
		});
		tableColumnLayout.setColumnData(unitColumn.getColumn(), new ColumnPixelData(35));

		TableViewerColumn gotoColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		gotoColumn.getColumn().setText("Go to");
		gotoColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "goto"));
		gotoColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("goto");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("goto");
			}
		});
		tableColumnLayout.setColumnData(gotoColumn.getColumn(), new ColumnPixelData(100));

		TableViewerColumn stopColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		stopColumn.getColumn().setText("Stop");
		stopColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "stop"));
		stopColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return stopIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(stopColumn.getColumn(), new ColumnPixelData(40));

		TableViewerColumn statusColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		statusColumn.getColumn().setText("Status");
		statusColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "status"));
		statusColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("status");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("status");
			}
		});
		tableColumnLayout.setColumnData(statusColumn.getColumn(), new ColumnPixelData(70));

		TableViewerColumn setColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		setColumn.getColumn().setText("Set/Use");
		setColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "set"));
		setColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("set");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("set");
			}
		});
		tableColumnLayout.setColumnData(setColumn.getColumn(), new ColumnPixelData(60));

		TableViewerColumn tweakRColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakRColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "tweakreverse"));
		tweakRColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return leftArrowIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(tweakRColumn.getColumn(), new ColumnPixelData(22));

		TableViewerColumn tweakValueColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakValueColumn.getColumn().setText("Tweak");
		tweakValueColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "tweakvalue"));
		tweakValueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("tweakvalue");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("tweakvalue");
			}
		});
		tableColumnLayout.setColumnData(tweakValueColumn.getColumn(), new ColumnPixelData(70));

		TableViewerColumn tweakFColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		tweakFColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "tweakforward"));
		tweakFColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return rightArrowIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(tweakFColumn.getColumn(), new ColumnPixelData(22));

		axisTableViewer.setInput(axisContentProvider);

		// Sash anlegen
	    Color blue3 = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);
		this.sash1 = new Sash(parent, SWT.HORIZONTAL | SWT.BORDER);
	    this.sash1.setBackground(blue3);
			
		this.bar2 = new ExpandBar( parent, SWT.V_SCROLL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar2.setLayoutData( gridData );

		this.sash2 = new Sash(parent, SWT.HORIZONTAL | SWT.BORDER);
	    this.sash2.setBackground(blue3);

		this.bar3 = new ExpandBar( parent, SWT.V_SCROLL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar3.setLayoutData( gridData );
	    
		// Ereignisverarbeitung für Sash
		sash1.addSelectionListener(new SelectionAdapter() {
		public void widgetSelected(SelectionEvent e) {
		if (e.detail != SWT.DRAG) {
		moveSash1(parent, bar1, sash1, bar2,
		e.x, e.y, e.width, e.height, sash2);
		}
		}
		});

		sash2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			if (e.detail != SWT.DRAG) {
			moveSash2(parent, bar2, sash2, bar3,
			e.x, e.y, e.width, e.height, sash1);
			}
			}
			});

		
		tableComposite = new Composite(bar2, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

//		channelTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		channelTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.VERTICAL);

		eitem = new ExpandItem ( this.bar2, SWT.NONE, 0);
		eitem.setText("Channels");
		eitem.setHeight( 200);
		eitem.setControl( tableComposite );
		eitem.setExpanded( channelExpanded );

//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.heightHint = 300;
//		gridData.widthHint = 330;
//		tableComposite.setLayoutData( gridData );

		channelTableViewer.getTable().setHeaderVisible(true);;
		channelTableViewer.getTable().setLinesVisible(true);
		//channelTableViewer.getControl().setSize(400, 400);
		
		channelContentProvider = new CommonTableContentProvider(channelTableViewer, eitem, this.devices );
		channelTableViewer.setContentProvider(channelContentProvider);

		delColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		//delColumn.getColumn().setText("Remove");
		delColumn.setEditingSupport(new CommonTableEditingSupport(channelTableViewer, "remove"));
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return deleteIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(delColumn.getColumn(), new ColumnPixelData(22));

		nameColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.setEditingSupport(new CommonTableEditingSupport(channelTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("name");
			}
		});
		tableColumnLayout.setColumnData(nameColumn.getColumn(), new ColumnPixelData(200));

		valueColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Value");
		valueColumn.setEditingSupport(new CommonTableEditingSupport(channelTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
		});
		tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnPixelData(100));

		engineColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		engineColumn.getColumn().setText("Engine");
		engineColumn.setEditingSupport(new CommonTableEditingSupport(channelTableViewer, "engine"));
		engineColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("engine");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
		});
		tableColumnLayout.setColumnData(engineColumn.getColumn(), new ColumnPixelData(100));

		unitColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
		});
		tableColumnLayout.setColumnData(unitColumn.getColumn(), new ColumnPixelData(35));

		TableViewerColumn triggerColumn = new TableViewerColumn(channelTableViewer, SWT.NONE);
		triggerColumn.getColumn().setText("Trig");
		triggerColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "trigger"));
		triggerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return playIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(triggerColumn.getColumn(), new ColumnPixelData(22));

		channelTableViewer.setInput(channelContentProvider);

		// device Table
		Composite tableComposite3 = new Composite(bar3, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite3.setLayout(tableColumnLayout);
		deviceTableViewer = new TableViewer(tableComposite3, SWT.BORDER | SWT.FULL_SELECTION);

		eitem = new ExpandItem ( this.bar3, SWT.NONE, 0);
		eitem.setText("Devices");
		eitem.setHeight( 200);
		eitem.setControl( tableComposite3 );
		eitem.setExpanded( deviceExpanded );
		
//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.heightHint = 300;
//		gridData.widthHint = 330;
//		tableComposite3.setLayoutData( gridData );

		deviceTableViewer.getTable().setHeaderVisible(true);;
		deviceTableViewer.getTable().setLinesVisible(true);
		//deviceTableViewer.getControl().setSize(400, 400);

		deviceContentProvider = new CommonTableContentProvider(deviceTableViewer, eitem, this.devices );
		deviceTableViewer.setContentProvider(deviceContentProvider);

		delColumn = new TableViewerColumn(deviceTableViewer, SWT.NONE);
		delColumn.setEditingSupport(new CommonTableEditingSupport(deviceTableViewer, "remove"));
		delColumn.getColumn().setMoveable(true);
		delColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return deleteIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(delColumn.getColumn(), new ColumnPixelData(22));

		nameColumn = new TableViewerColumn(deviceTableViewer, SWT.NONE);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setMoveable(true);
		nameColumn.setEditingSupport(new CommonTableEditingSupport(deviceTableViewer, "name"));
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("name");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getConnectColor("name");
			}
		});
		tableColumnLayout.setColumnData(nameColumn.getColumn(), new ColumnPixelData(100));

		valueColumn = new TableViewerColumn(deviceTableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setMoveable(true);
		valueColumn.setEditingSupport(new CommonTableEditingSupport(deviceTableViewer, "value"));
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("value");
			}
			public Color getForeground(Object element) {
				return ((CommonTableElement) element).getSeverityColor("value");
			}
		});
		tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnPixelData(100));

		unitColumn = new TableViewerColumn(deviceTableViewer, SWT.NONE);
		unitColumn.getColumn().setText("Unit");
		unitColumn.getColumn().setMoveable(true);
		unitColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "unit"));
		unitColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((CommonTableElement) element).getValue("unit");
			}
		});
		tableColumnLayout.setColumnData(unitColumn.getColumn(), new ColumnPixelData(35));

		deviceTableViewer.setInput(deviceContentProvider);
		deviceTableViewer.getTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		// TODO geht nicht
		//deviceTableViewer.addDropSupport(DND.DROP_COPY, types, dropListener);		
		// TODO geht auch nicht
//		target = new DropTarget( tableComposite3, DND.DROP_COPY );
//		target.setTransfer( types );
//		target.addDropListener( dropListener );
		
		
		this.newDeviceInspectorAction = new NewDeviceInspectorAction( this );
		
		this.newDeviceInspectorAction.setText( "New Device Inspector" );
		this.newDeviceInspectorAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_NEW_WIZARD ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.newDeviceInspectorAction );
		
		this.renameDeviceInspectorAction = new RenameDeviceInspector( this );
		this.renameDeviceInspectorAction.setText( "Rename Device Inspector" );
		this.renameDeviceInspectorAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_OBJS_INFO_TSK ) );
		this.getViewSite().getActionBars().getToolBarManager().add( this.renameDeviceInspectorAction );
		
		final List< AbstractDevice > devs = new ArrayList< AbstractDevice >( this.devices );
		this.devices.clear();
		for( final AbstractDevice d : devs ) {
			this.addAbstractDevice( d );
		}
		
		this.axisTableViewer.getTable().addMouseMoveListener( new MouseMoveListener() {

			@Override
			public void mouseMove( final MouseEvent e ) {
				
				final TableItem item = axisTableViewer.getTable().getItem( new Point( e.x, e.y ) );
				if( item != null ) {
					final MotorAxis axis = (MotorAxis)((CommonTableElement)item.getData()).getAbstractDevice();
					final int[] columns = axisTableViewer.getTable().getColumnOrder();
					int pix = 0;
					int colId = 0;
					for( int i = 0; i < columns.length; ++i ) {
						if( (pix + axisTableViewer.getTable().getColumns()[ columns[i] ].getWidth()) > e.x ) {
							colId = columns[i];
							break;
						}
						pix += axisTableViewer.getTable().getColumns()[ columns[i] ].getWidth();
					}
					
					final String colName = axisTableViewer.getTable().getColumns()[ colId ].getText();
					if( colName.equals( "Name" ) ) {
						axisTableViewer.getTable().setToolTipText( axis.getID() );
					} else if( colName.equals( "Position" ) && axis.getPosition() != null && axis.getPosition().getAccess() != null ) {
						axisTableViewer.getTable().setToolTipText( axis.getPosition().getAccess().getVariableID() );
					} else if( colName.equals( "Unit" ) && axis.getUnit() != null && axis.getUnit().getAccess() != null ) {
						axisTableViewer.getTable().setToolTipText( axis.getUnit().getAccess().getVariableID() );
					} else if( colName.equals( "Go to" ) && axis.getGoto() != null && axis.getGoto().getAccess() != null ) {
						axisTableViewer.getTable().setToolTipText( axis.getGoto().getAccess().getVariableID() );
					} else if( colName.equals( "Status" ) && axis.getStatus() != null && axis.getStatus().getAccess() != null ) {
						axisTableViewer.getTable().setToolTipText( axis.getStatus().getAccess().getVariableID() );
					} else if( colName.equals( "Tweak" ) && axis.getTweakValue() != null && axis.getTweakValue().getAccess() != null ) {
						axisTableViewer.getTable().setToolTipText( axis.getTweakValue().getAccess().getVariableID() );
					} else {
						axisTableViewer.getTable().setToolTipText( "" );
					}
					
				}
				
			}
			
		});
		
		this.channelTableViewer.getTable().addMouseMoveListener( new MouseMoveListener() {

			@Override
			public void mouseMove( final MouseEvent e ) {
				
				final TableItem item = channelTableViewer.getTable().getItem( new Point( e.x, e.y ) );
				if( item != null ) {
					final DetectorChannel channel = (DetectorChannel)((CommonTableElement)item.getData()).getAbstractDevice();
					final int[] columns = channelTableViewer.getTable().getColumnOrder();
					int pix = 0;
					int colId = 0;
					for( int i = 0; i < columns.length; ++i ) {
						if( (pix + channelTableViewer.getTable().getColumns()[ columns[i] ].getWidth()) > e.x ) {
							colId = columns[i];
							break;
						}
						pix += channelTableViewer.getTable().getColumns()[ columns[i] ].getWidth();
					}
					
					final String colName = channelTableViewer.getTable().getColumns()[ colId ].getText();
					if( colName.equals( "Name" ) ) {
						channelTableViewer.getTable().setToolTipText( channel.getID() );
					} else if( colName.equals( "Value" ) && channel.getRead() != null && channel.getRead().getAccess() != null ) {
						channelTableViewer.getTable().setToolTipText( channel.getRead().getAccess().getVariableID() );
					} else if( colName.equals( "Unit" ) && channel.getUnit() != null && channel.getUnit().getAccess() != null ) {
						channelTableViewer.getTable().setToolTipText( channel.getUnit().getAccess().getVariableID() );
					} else if( colName.equals( "Trig" ) && channel.getTrigger() != null && channel.getTrigger().getAccess() != null ) {
						channelTableViewer.getTable().setToolTipText( channel.getTrigger().getAccess().getVariableID() );
					} else {
						channelTableViewer.getTable().setToolTipText( "" );
					}
					
				}
				
			}
			
		});
		
		this.deviceTableViewer.getTable().addMouseMoveListener( new MouseMoveListener() {

			@Override
			public void mouseMove( final MouseEvent e ) {
				
				final TableItem item = deviceTableViewer.getTable().getItem( new Point( e.x, e.y ) );
				if( item != null ) {
					final Device device = (Device)((CommonTableElement)item.getData()).getAbstractDevice();
					final int[] columns = deviceTableViewer.getTable().getColumnOrder();
					int pix = 0;
					int colId = 0;
					for( int i = 0; i < columns.length; ++i ) {
						if( (pix + deviceTableViewer.getTable().getColumns()[ columns[i] ].getWidth()) > e.x ) {
							colId = columns[i];
							break;
						}
						pix += deviceTableViewer.getTable().getColumns()[ columns[i] ].getWidth();
					}
					
					final String colName = deviceTableViewer.getTable().getColumns()[ colId ].getText();
					if( colName.equals( "Name" ) ) {
						deviceTableViewer.getTable().setToolTipText( device.getID() );
					} else if( colName.equals( "Value" ) && device.getValue() != null && device.getValue().getAccess() != null ) {
						deviceTableViewer.getTable().setToolTipText( device.getValue().getAccess().getVariableID() );
					} else if( colName.equals( "Unit" ) && device.getUnit() != null && device.getUnit().getAccess() != null ) {
						deviceTableViewer.getTable().setToolTipText( device.getUnit().getAccess().getVariableID() );
					} else {
						channelTableViewer.getTable().setToolTipText( "" );
					}
					
				}
				
			}
			
		});
		
		final IWorkbenchPage page = getSite().getPage();
		MenuManager menuManager = new MenuManager( "#PopupMenu" );
		menuManager.setRemoveAllWhenShown( true );
		menuManager.addMenuListener( new IMenuListener() {

			public void menuAboutToShow( final IMenuManager manager ) {
				final TableItem[] selectedItems = axisTableViewer.getTable().getSelection();
				if( selectedItems != null && selectedItems.length > 0 ) {
					final Action openAction = new Action() {
						public void run() {
							super.run();
							for( final TableItem item : selectedItems ) {
								if( item.getData() instanceof CommonTableElement ) {
									try {
										final IViewPart view = page.showView( "DeviceOptionsView", ((CommonTableElement)item.getData()).getAbstractDevice().getName(), IWorkbenchPage.VIEW_CREATE );
										((DeviceOptionsViewer)view).setDevice( ((CommonTableElement)item.getData()).getAbstractDevice() );
										((DeviceOptionsViewer)view).setFixed( true );
									} catch (PartInitException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						}
					};
					openAction.setText( "Open in seperate options window" );
					manager.add( openAction );
				}
				
			}
			
		});
		
		Menu contextMenu = menuManager.createContextMenu( this.axisTableViewer.getTable() );
		this.axisTableViewer.getTable().setMenu( contextMenu );
		
		this.axisTableViewer.getTable().addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = axisTableViewer.getTable().getSelection();
				if( items.length > 0 ) {
					if( items[0].getData() instanceof CommonTableElement ) {
						IViewReference[] ref = getSite().getPage().getViewReferences();
						DeviceOptionsViewer view = null;
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( "DeviceOptionsView" ) &&  !((DeviceOptionsViewer)ref[i].getPart(true)).isFixed() ) {
								view = (DeviceOptionsViewer)ref[i].getPart( true );
							}
						}
						if (view != null) view.setDevice( ((CommonTableElement)items[0].getData()).getAbstractDevice() );
					}
				}
				
			}
			
		});
		
		//-----------------------------------------------
		
		menuManager = new MenuManager( "#PopupMenu" );
		menuManager.setRemoveAllWhenShown( true );
		menuManager.addMenuListener( new IMenuListener() {

			public void menuAboutToShow( final IMenuManager manager ) {
				final TableItem[] selectedItems = channelTableViewer.getTable().getSelection();
				if( selectedItems != null && selectedItems.length > 0 ) {
					final Action openAction = new Action() {
						public void run() {
							super.run();
							for( final TableItem item : selectedItems ) {
								if( item.getData() instanceof CommonTableElement ) {
									try {
										final IViewPart view = page.showView( "DeviceOptionsView", ((CommonTableElement)item.getData()).getAbstractDevice().getName(), IWorkbenchPage.VIEW_CREATE );
										((DeviceOptionsViewer)view).setDevice( ((CommonTableElement)item.getData()).getAbstractDevice() );
										((DeviceOptionsViewer)view).setFixed( true );
									} catch (PartInitException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						}
					};
					openAction.setText( "Open in seperate options window" );
					manager.add( openAction );
				}
				
			}
			
		});
		
		contextMenu = menuManager.createContextMenu( this.channelTableViewer.getTable() );
		this.channelTableViewer.getTable().setMenu( contextMenu );
		
		this.channelTableViewer.getTable().addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = channelTableViewer.getTable().getSelection();
				if( items.length > 0 ) {
					if( items[0].getData() instanceof CommonTableElement ) {
						IViewReference[] ref = getSite().getPage().getViewReferences();
						DeviceOptionsViewer view = null;
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( "DeviceOptionsView" ) &&  !((DeviceOptionsViewer)ref[i].getPart(true)).isFixed() ) {
								view = (DeviceOptionsViewer)ref[i].getPart( true );
							}
						}
						if (view != null) view.setDevice( ((CommonTableElement)items[0].getData()).getAbstractDevice() );
					}
				}
				
			}
			
		});
		
		//----------------------------------
		
		menuManager = new MenuManager( "#PopupMenu" );
		menuManager.setRemoveAllWhenShown( true );
		menuManager.addMenuListener( new IMenuListener() {

			public void menuAboutToShow( final IMenuManager manager ) {
				final TableItem[] selectedItems = deviceTableViewer.getTable().getSelection();
				if( selectedItems != null && selectedItems.length > 0 ) {
					final Action openAction = new Action() {
						public void run() {
							super.run();
							for( final TableItem item : selectedItems ) {
								if( item.getData() instanceof CommonTableElement ) {
									try {
										final IViewPart view = page.showView( "DeviceOptionsView", ((CommonTableElement)item.getData()).getAbstractDevice().getName(), IWorkbenchPage.VIEW_CREATE );
										((DeviceOptionsViewer)view).setDevice( ((CommonTableElement)item.getData()).getAbstractDevice() );
										((DeviceOptionsViewer)view).setFixed( true );
									} catch (PartInitException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
							}
						}
					};
					openAction.setText( "Open in seperate options window" );
					manager.add( openAction );
				}
				
			}
			
		});
		
		contextMenu = menuManager.createContextMenu( this.deviceTableViewer.getTable() );
		this.deviceTableViewer.getTable().setMenu( contextMenu );
		
		this.deviceTableViewer.getTable().addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = deviceTableViewer.getTable().getSelection();
				if( items.length > 0 ) {
					if( items[0].getData() instanceof CommonTableElement ) {
						IViewReference[] ref = getSite().getPage().getViewReferences();
						DeviceOptionsViewer view = null;
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( "DeviceOptionsView" ) &&  !((DeviceOptionsViewer)ref[i].getPart(true)).isFixed() ) {
								view = (DeviceOptionsViewer)ref[i].getPart( true );
							}
						}
						if (view != null) view.setDevice( ((CommonTableElement)items[0].getData()).getAbstractDevice() );
					}
				}
				
			}
			
		});
		
		//----------------------------------
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
	
	public void addAbstractDevice( final AbstractDevice device ) {
		if( device != null ) {
			if( device instanceof MotorAxis ) {
				if( !devices.contains( device ) ) {
					addMotorAxisEntry( device );
				}
			} else if( device instanceof DetectorChannel ){
				if( !devices.contains( device ) ) {
					addDetectorChannelEntry( device );
				}
			} else if( device instanceof Device ){
				if( !devices.contains( device ) ) {
					addDeviceEntry( device);
				}
			
			} else if( device instanceof Motor ){
				final Motor motor = (Motor)device;
				if( motor.getAxes().size() > 0 ) {
					List<MotorAxis> axisList = motor.getAxes();
					for (MotorAxis axis : axisList) {
						if (!devices.contains(axis)) addMotorAxisEntry(axis);
					}
				}
			} else if( device instanceof Detector ){
				final Detector detector = (Detector)device;
				if( detector.getChannels().size() > 0 ) {
					List<DetectorChannel> channelList = detector.getChannels();
					for (DetectorChannel channel : channelList) {
						if (!devices.contains(channel)) addDetectorChannelEntry( channel );
					}
				}
			}
		}
	}
	
	private void addMotorAxisEntry( final AbstractDevice device){
		CommonTableElement cte = new CommonTableElement(device, axisTableViewer);
		axisContentProvider.addElement(cte);
		cte.init();
		this.devices.add( device );
	}
	
	private void addDetectorChannelEntry( final AbstractDevice device){
		CommonTableElement cte = new CommonTableElement(device, channelTableViewer);
		channelContentProvider.addElement(cte);
		cte.init();
		this.devices.add( device );
	}

	private void addDeviceEntry( final AbstractDevice device){
		CommonTableElement cte = new CommonTableElement(device, deviceTableViewer);
		deviceContentProvider.addElement(cte);
		cte.init();
		this.devices.add( device );
	}
	
	public void setName( final String name ) {
		this.setPartName( name );
	}
	
	public void saveState( final IMemento memento ) {
	      memento.putString( "name", this.getPartName() );
	      final StringBuffer devicesString = new StringBuffer();
	      for( final AbstractDevice d : this.devices ) {
	    	  devicesString.append( d.getFullIdentifyer() );
	    	  devicesString.append( ',' );
	      }
	      
	      memento.putString( "devices", devicesString.toString() );
	      
	      for( final ExpandItem e : this.bar1.getItems() ) {
	    	  memento.putBoolean( e.getText(), e.getExpanded() );
	      }
	      
	}
	
	private void moveSash1(final Composite parent, final ExpandBar c1,
			final Sash sash, final ExpandBar c2,
			int x, int y, int width, int height, final Sash sash2) {
			// Sash repositionieren
			sash.setBounds(x, y, width, height);
			// die nutzbare Fläche der Gruppe abfragen
			Rectangle r = parent.getClientArea();

/************/
			System.out.println("moveSashdrei, Werte müssen neu gesetzt werden");
			System.out.println("   X (alt) " + c1.getBounds().x);
			System.out.println("   Y (alt) " + c1.getBounds().y);
			System.out.println("   width (alt) " + c1.getBounds().width);
			System.out.println("   height (alt) " + c1.getBounds().height);

			System.out.println("   X (neu) " + r.x);
			System.out.println("   Y (neu) " + r.y);
			System.out.println("   width (neu) " + r.width);
			System.out.println("   height (neu) " + r.height);
/*************/			

			System.out.println("   Höhenänderung von C1:" + (y - r.y - c1.getBounds().height));
			int deltah = y - r.y - c1.getBounds().height;
			
			int neueHoehe = c2.getBounds().height - y + r.y + c1.getBounds().height;
			System.out.println("neue Höhe: " + neueHoehe);

			int c1NewHeight = y - r.y;
			int c2NewHeight = c2.getBounds().height - y + r.y + c1.getBounds().height;
			
			/**** dies gilt für oben / unten ******/
			// das obere Widget in der Größe anpassen
			c1.setBounds(r.x, r.y, r.width, c1NewHeight);
			// das untere Widget in der Größe anpassen
//			c2.setBounds(r.x, y + height, r.width, r.height - (y + height));
			c2.setBounds(r.x, y + height, r.width, c2NewHeight);

			
			System.out.println("Neue Werte:");
			System.out.println("   c1 new " + c1NewHeight);
			System.out.println("   c1 ist " + c1.getBounds().height);
			System.out.println("   c2 new " + c2NewHeight);
			System.out.println("   c2 ist " + c2.getBounds().height);

			if (c1.getBounds().height > c1NewHeight) {
				// C1 ist kleiner als Minimalwert gesetzt worden,
				// Größen korrigieren
				int delta = c1.getBounds().height - c1NewHeight;
				c2.setBounds(r.x, y + height + delta, r.width, c2NewHeight - delta);
				sash.setBounds(x, y + delta, width, height);
			}
			
/*************/
			if (c2.getBounds().height > c2NewHeight) {
				// C2 ist kleiner als Minimalwert gesetzt worden,
				// Größen korrigieren
				int sash2ort = sash2.getBounds().y;
				int c2ende = c2.getBounds().y + c2.getBounds().height;
				
				System.out.println("   Ort sash2: " + sash2ort);
				System.out.println("   Ende von c2: " + c2ende);				
				
				int delta = c2.getBounds().height - c2NewHeight;

				if (sash2ort < c2ende) {
					c1.setBounds(r.x, r.y, r.width, c1NewHeight - delta);
					sash.setBounds(x, y - delta, width, height);
					c2.setBounds(r.x, y + height - delta, r.width, c2NewHeight);
				}
			}
/************/
			
			// das linke Widget in der Größe anpassen
//			c1.setBounds(r.x, r.y, x - r.x, r.height);
			// das rechte Widget in der Größe anpassen
//			c2.setBounds(x + width, r.y, r.width - (x + width), r.height);

	}

	private void moveSash2(final Composite parent, final ExpandBar c1,
			final Sash sash, final ExpandBar c2,
			int x, int y, int width, int height, final Sash sash2) {
			// Sash repositionieren
			sash.setBounds(x, y, width, height);
			// die nutzbare Fläche der Gruppe abfragen
			Rectangle r = parent.getClientArea();

/************/
			System.out.println("moveSashdrei, Werte müssen neu gesetzt werden");
			System.out.println("   X (alt) " + c1.getBounds().x);
			System.out.println("   Y (alt) " + c1.getBounds().y);
			System.out.println("   width (alt) " + c1.getBounds().width);
			System.out.println("   height (alt) " + c1.getBounds().height);
/*************/			

			int	c1xalt = c1.getBounds().x;
			int	c1yalt = c1.getBounds().y;
			int c1halt = c1.getBounds().height;

			int	c2xalt = c2.getBounds().x;
			int	c2yalt = c2.getBounds().y;
			int c2halt = c2.getBounds().height;
			
			
			System.out.println("   Höhenänderung von C1:" + (y - c1yalt - c1halt));
			int deltah = y - c1yalt - c1halt;
			
			int c1NewHeight = c1halt + deltah;
			int c2NewHeight = c2halt - deltah;
			
			/**** dies gilt für oben / unten ******/
			// das obere Widget in der Größe anpassen
			c1.setBounds(r.x, c1yalt, r.width, c1NewHeight);
			// das untere Widget in der Größe anpassen
			c2.setBounds(r.x, y + height, r.width, c2NewHeight);
//			c2.setBounds(r.x, c2yalt - deltah, r.width, c2NewHeight);

			
			System.out.println("Neue Werte:");
			System.out.println("   c1 new " + c1NewHeight);
			System.out.println("   c1 ist " + c1.getBounds().height);
			System.out.println("   c2 new " + c2NewHeight);
			System.out.println("   c2 ist " + c2.getBounds().height);

			if (c1.getBounds().height > c1NewHeight) {
				// C1 ist kleiner als Minimalwert gesetzt worden,
				// Größen korrigieren
				int delta = c1.getBounds().height - c1NewHeight;
				c2.setBounds(r.x, y + height + delta, r.width, c2NewHeight - delta);
				sash.setBounds(x, y + delta, width, height);
			}
			
			if (c2.getBounds().height > c2NewHeight) {
				// C2 ist kleiner als Minimalwert gesetzt worden,
				// Größen korrigieren
				int sash2ort = sash2.getBounds().y;
				int c2ende = c2.getBounds().y + c2.getBounds().height;
				
				System.out.println("   Ort sash2: " + sash2ort);
				System.out.println("   Ende von c2: " + c2ende);				
				
				int delta = c2.getBounds().height - c2NewHeight;

				if (sash2ort < c2ende) {
					c1.setBounds(r.x, c1yalt, r.width, c1NewHeight - delta);
					sash.setBounds(x, y - delta, width, height);
					c2.setBounds(r.x, y + height - delta, r.width, c2NewHeight);
				}
			}
	

	}

	
	public void init( final IViewSite site, final IMemento memento ) throws PartInitException {
		super.init( site, memento );
		if( memento != null ) {
			final String name = memento.getString( "name" );
			if( name != null ) {
				this.setName( name );
			}
			final String devicesString = memento.getString( "devices" );
			if( devicesString != null ) {
				if( this.devices == null ) {
					this.devices = new ArrayList< AbstractDevice >();
				};
				final String[] ds = devicesString.split( "," );
				for( final String d : ds ) {
					if( d != null && !d.equals( "" ) ) {
						final AbstractDevice device = Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( d );
						if( device != null ) {
							this.devices.add( device );
						}
					}
				}
			}
			try {
				this.axisExpanded = memento.getBoolean( "Axes" );
				this.channelExpanded = memento.getBoolean( "Channels" );
				this.deviceExpanded = memento.getBoolean( "Devices" );
			} catch( final Exception e ) {
				
			}
			
		}
		
	}
}
