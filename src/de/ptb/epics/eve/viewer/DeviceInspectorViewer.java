package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.AbstractDevice;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.Device;

/**
 * This class represents the DeviceInspector view that provides interaction with motor axes
 * 
 * @author Stephan Rehfeld <stephan.rehfeld@ptb.de>
 *
 */
public class DeviceInspectorViewer extends ViewPart implements DisposeListener {

	private ExpandBar bar;
	
	private Composite motorAxesComposite;
	private Composite detectorChannelsComposite;
	private Composite devicesComposite;
	
	private ExpandItem item0;
	private ExpandItem item1;
	private ExpandItem item2;
	
	private List< AbstractDevice > devices;
	
	
	@Override
	public void createPartControl( Composite parent ) {
		
		this.devices = new ArrayList< AbstractDevice >();
		
		parent.setLayout( new GridLayout() );
		
		DropTarget target = new DropTarget( parent, DND.DROP_COPY );
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final Transfer[] types = new Transfer[] { textTransfer };
		target.setTransfer( types );
		
		final DeviceInspectorViewer own = this;
		
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
				if( textTransfer.isSupportedType( event.currentDataType ) ) {
					IViewReference[] ref = getSite().getPage().getViewReferences();
					MeasuringStationView view = null;
					for( int i = 0; i < ref.length; ++i ) {
						if( ref[i].getId().equals( MeasuringStationView.ID ) ) {
							view = (MeasuringStationView)ref[i].getPart( false );
							final MeasuringStation measuringStation = view.getMeasuringStation();
							AbstractDevice device = measuringStation.getAbstractDeviceByFullIdentifyer( (String)event.data );
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
							break;
						}
							
					}
				}
				
			}

			private void addMotorAxisEntry( final AbstractDevice device){
				MotorAxisComposite motorAxisComposite = new MotorAxisComposite( motorAxesComposite, SWT.NONE, (MotorAxis)device );
				motorAxisComposite.addDisposeListener( own );
				Activator.getDefault().getEcp1Client().addMeasurementDataListener( motorAxisComposite );
				
				GridData gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				gridData.grabExcessHorizontalSpace = true;
				motorAxisComposite.setLayoutData( gridData );
				motorAxesComposite.layout();
				item0.setHeight( motorAxesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				devices.add( device );
				
				DropTarget target = new DropTarget( motorAxisComposite, DND.DROP_COPY );
				target.setTransfer( types );
				target.addDropListener( this );

			}
			
			private void addDetectorChannelEntry( final AbstractDevice device){
				DetectorChannelComposite detectorChannelComposite = new DetectorChannelComposite( detectorChannelsComposite, SWT.NONE, (DetectorChannel)device );
				detectorChannelComposite.addDisposeListener( own );
				Activator.getDefault().getEcp1Client().addMeasurementDataListener( detectorChannelComposite );
				GridData gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				gridData.grabExcessHorizontalSpace = true;
				detectorChannelComposite.setLayoutData( gridData );
				detectorChannelsComposite.layout();
				item1.setHeight( detectorChannelsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				devices.add( device );
				
				DropTarget target = new DropTarget( detectorChannelComposite, DND.DROP_COPY );
				target.setTransfer( types );
				target.addDropListener( this );
			}

			private void addDeviceEntry( final AbstractDevice device){
				DeviceComposite deviceComposite = new DeviceComposite( devicesComposite, SWT.NONE, (Device)device );
				deviceComposite.addDisposeListener( own );
				Activator.getDefault().getEcp1Client().addMeasurementDataListener( deviceComposite );
				GridData gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				gridData.grabExcessHorizontalSpace = true;
				deviceComposite.setLayoutData( gridData );
				devicesComposite.layout();
				item2.setHeight( devicesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
				devices.add( device );
				
				DropTarget target = new DropTarget( deviceComposite, DND.DROP_COPY );
				target.setTransfer( types );
				target.addDropListener( this );
			}
			

			public void dropAccept( final DropTargetEvent event ) {
				
			}
		
		};
		
		
		target.addDropListener( dropListener );

		
		this.bar = new ExpandBar( parent, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData( gridData );
		
		target = new DropTarget( this.bar, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
		
		this.motorAxesComposite = new Composite( this.bar, SWT.NONE );
		target = new DropTarget( this.motorAxesComposite, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
		
		this.detectorChannelsComposite = new Composite( this.bar, SWT.NONE );
		target = new DropTarget( this.detectorChannelsComposite, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
		
		this.devicesComposite = new Composite( this.bar, SWT.NONE );
		target = new DropTarget( this.devicesComposite, DND.DROP_COPY );
		target.setTransfer( types );
		target.addDropListener( dropListener );
		
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		this.motorAxesComposite.setLayout( gridLayout );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		this.detectorChannelsComposite.setLayout( gridLayout );
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginHeight = 0;
		this.devicesComposite.setLayout( gridLayout );
		
				
		
		
		
		this.item0 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item0.setText("Motor Axis");
		item0.setHeight( this.motorAxesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl( this.motorAxesComposite );
		
		this.item1 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item1.setText("Detector Channels");
		item1.setHeight( this.detectorChannelsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl( this.detectorChannelsComposite );
		
		this.item2 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item2.setText("Devices");
		item2.setHeight( this.devicesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setControl( this.devicesComposite );
		
		
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	public void widgetDisposed( final DisposeEvent e ) {
		if( e.widget instanceof MotorAxisComposite ) {
			final MotorAxisComposite motorAxisComposite = (MotorAxisComposite)e.widget;
			this.devices.remove( motorAxisComposite.getMotorAxis() );
		} else if( e.widget instanceof DetectorChannelComposite ) {
			final DetectorChannelComposite detectorChannelComposite = (DetectorChannelComposite)e.widget;
			this.devices.remove( detectorChannelComposite.getDetectorChannel() );
		} if( e.widget instanceof DeviceComposite ) {
			final DeviceComposite deviceComposite = (DeviceComposite)e.widget;
			this.devices.remove( deviceComposite.getDevice() );
		}
		this.motorAxesComposite.layout();
		this.detectorChannelsComposite.layout();
		this.devicesComposite.layout();
		
		item0.setHeight( this.motorAxesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setHeight( this.detectorChannelsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setHeight( this.devicesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}

}
