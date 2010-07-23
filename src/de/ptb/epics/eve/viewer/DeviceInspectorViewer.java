package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.viewer.actions.ClearMessagesAction;
import de.ptb.epics.eve.viewer.actions.NewDeviceInspectorAction;
import de.ptb.epics.eve.viewer.actions.RenameDeviceInspector;

import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

/**
 * This class represents the DeviceInspector view that provides interaction with motor axes
 * 
 * @author Stephan Rehfeld <stephan.rehfeld@ptb.de>
 *
 */
public class DeviceInspectorViewer extends ViewPart {

	private ExpandBar bar;
	
	private List< AbstractDevice > devices;

	private TableViewer axisTableViewer;
	private TableViewer channelTableViewer;
	private TableViewer deviceTableViewer;
	private CommonTableContentProvider deviceContentProvider;
	private CommonTableContentProvider axisContentProvider;
	private CommonTableContentProvider channelContentProvider;
	
	private NewDeviceInspectorAction newDeviceInspectorAction;
	private RenameDeviceInspector renameDeviceInspectorAction;
	
	@Override
	public void createPartControl( Composite parent ) {
		
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
								final MeasuringStation measuringStation = view.getMeasuringStation();
								if (measuringStation.getClassNameList().contains( comp[1] ) ) {
									for (AbstractDevice absdevice : measuringStation.getDeviceList(comp[1])) {
										if (absdevice instanceof MotorAxis ) addMotorAxisEntry((MotorAxis) absdevice );
										else if (absdevice instanceof DetectorChannel )
											addDetectorChannelEntry((DetectorChannel) absdevice );
										else if (absdevice instanceof Device ) 
											addDeviceEntry((Device) absdevice );
										else if( absdevice instanceof Motor )
											for (MotorAxis axis : ((Motor)absdevice).getAxis()) 
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
		
		this.bar = new ExpandBar( parent, SWT.V_SCROLL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData( gridData );

		target = new DropTarget( this.bar, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
						
		Composite tableComposite = new Composite(bar, SWT.NONE);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		axisTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);

		ExpandItem eitem = new ExpandItem ( this.bar, SWT.NONE, 0);
		eitem.setText("Axes");
		eitem.setHeight( 300);
		eitem.setControl( tableComposite );

//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.heightHint = 200;
//		gridData.widthHint = 330;
//		tableComposite.setLayoutData( gridData );

		axisTableViewer.getTable().setHeaderVisible(true);
		axisTableViewer.getTable().setLinesVisible(true);

		axisContentProvider = new CommonTableContentProvider(axisTableViewer, eitem, this.devices );
		axisTableViewer.setContentProvider(axisContentProvider);

		TableViewerColumn delColumn = new TableViewerColumn(axisTableViewer, SWT.NONE);
		//delColumn.getColumn().setText("Remove");
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
		stopColumn.setEditingSupport(new CommonTableEditingSupport(axisTableViewer, "stop"));
		stopColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				return stopIcon;
			}
			public String getText(Object element) {
				return null;
			}
		});
		tableColumnLayout.setColumnData(stopColumn.getColumn(), new ColumnPixelData(22));

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


		tableComposite = new Composite(bar, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		channelTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);

		eitem = new ExpandItem ( this.bar, SWT.NONE, 0);
		eitem.setText("Detector Channels");
		eitem.setHeight( 200);
		eitem.setControl( tableComposite );

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
		tableComposite = new Composite(bar, SWT.NONE);
		tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		deviceTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);

		eitem = new ExpandItem ( this.bar, SWT.NONE, 0);
		eitem.setText("Devices");
		eitem.setHeight( 200);
		eitem.setControl( tableComposite );

//		gridData = new GridData();
//		gridData.grabExcessHorizontalSpace = true;
//		gridData.grabExcessVerticalSpace = true;
//		gridData.heightHint = 300;
//		gridData.widthHint = 330;
//		tableComposite.setLayoutData( gridData );

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
//		target = new DropTarget( tableComposite, DND.DROP_COPY );
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
				if( motor.getAxis().size() > 0 ) {
					List<MotorAxis> axisList = motor.getAxis();
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
			
		}
		
	}
}
