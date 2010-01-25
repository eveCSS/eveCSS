package de.ptb.epics.eve.viewer;

import java.awt.TrayIcon.MessageType;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Function;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;


/**
 * The detector Channel composite is used by the DeviceInspectorView to interact with a detector.
 * 
 * It carries the following informations:
 * - Name of the detector channel
 * - The current Value of the detector channel.
 * - The Unit of the detector
 * 
 * As interactive parts it has:
 * - A button to trigger the detector.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld@ptb.de>
 *
 */
public class DetectorChannelComposite extends Composite implements IProcessVariableValueListener, IMeasurementDataListener {

	/**
	 * The detector channel where this composite is connected to.
	 * 
	 */
	private final DetectorChannel detectorChannel;
	
	/**
	 * The label, which shows the name of the detector channel.
	 * 
	 */
	private Label detectorChannelNameLabel;
	
	/**
	 * The label, which shows the current value of the detector channel.
	 * 
	 */
	private Label currentValueLabel;
	
	private Label currentEngineValueLabel;
	
	/**
	 * The label, which shows the unit of the detector channel.
	 * 
	 */
	private Label unitLabel;
	
	/**
	 * The label that shows the method of the detector channel.
	 * 
	 */
	private Label methodLabel;
	
	/**
	 * The label that shows the current connection state the the pv of the detector channel.
	 * 
	 */
	private Label currentStateLabel;
	
	/**
	 * The button, that sends a trigger signal to the detector.
	 * 
	 */
	private Button triggerButton;
	
	/**
	 * This constructor creates a new DetectorChannelComposite connected to a given Detector Channel.
	 * 
	 * @param parent The parent composite of the detector channel.
	 * @param style The style of the detector channel.
	 * @param detectorChannel The detector channel where the composite should be connected to. Must not be null!
	 */
	public DetectorChannelComposite( final Composite parent, final int style, final DetectorChannel detectorChannel ) {
		super( parent, style );
		if( detectorChannel == null ) {
			throw new IllegalArgumentException( "The parameter 'detectorChannel' must not be null!" );
		}
		this.detectorChannel = detectorChannel;
		
		initialize();
	}
	
	/**
	 * This method initializes the detector channel composite and connects it to the given detector channel.
	 * 
	 */
	private void initialize() {

		//this.setBackground( new Color( this.getBackground().getDevice(), 255, 0, 0 ) );
		
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 7;
		gridLayout.marginHeight = 0;
		
		this.setLayout( gridLayout );
		
		GridData gridData = new GridData();
		
		this.detectorChannelNameLabel = new Label( this, SWT.NONE );
		this.detectorChannelNameLabel.setText( this.detectorChannel.getFullIdentifyer() );
		gridData.widthHint = 200;
		this.detectorChannelNameLabel.setLayoutData( gridData );
		
		this.currentValueLabel = new Label( this, SWT.NONE );
		this.currentValueLabel.setText( "?" );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.currentValueLabel.setLayoutData( gridData );
		
		this.currentEngineValueLabel = new Label( this, SWT.NONE );
		this.currentEngineValueLabel.setText( "(?)" );
		gridData = new GridData();
		gridData.widthHint = 80;
		this.currentEngineValueLabel.setLayoutData( gridData );
		
		
		this.unitLabel = new Label( this, SWT.NONE );
		if( this.detectorChannel.getUnit() != null ) {
			if( this.detectorChannel.getUnit().getValue() != null ) {
				this.unitLabel.setText( this.detectorChannel.getUnit().getValue() );
			} else {
				String prefix = null;
				if( this.detectorChannel.getUnit().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( this.detectorChannel.getUnit().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.detectorChannel.getUnit().getAccess().getVariableID() );
				try {
					final String unit = service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.detectorChannel.getUnit().getAccess().getType() ) ).toString();
					this.unitLabel.setText( unit );
				} catch( final ConnectionException e ) {
					this.unitLabel.setText( "?" );
				}
				
			}
		}
		gridData = new GridData();
		gridData.widthHint = 30;
		this.unitLabel.setLayoutData( gridData );
		
		this.methodLabel = new Label( this, SWT.NONE );
		this.methodLabel.setText( "?" );
		gridData = new GridData();
		gridData.widthHint = 40;
		this.methodLabel.setLayoutData( gridData );
		
		
		this.currentStateLabel = new Label( this, SWT.NONE );
		this.currentStateLabel.setText( "Disconnected" );
		gridData = new GridData();
		gridData.widthHint = 150;
		this.currentStateLabel.setLayoutData( gridData );
		
		this.triggerButton = new Button( this, SWT.PUSH );
		this.triggerButton.setText( "Trigger" );
		this.triggerButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				Function trigger = detectorChannel.getTrigger()!=null?detectorChannel.getTrigger():detectorChannel.getDetector().getTrigger();
				if( trigger != null ) {
					IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
					ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
					
					String prefix = null;
					if( trigger.getAccess().getTransport() == TransportTypes.CA ) {
						prefix = "dal-epics://";
					} else if( trigger.getAccess().getTransport() == TransportTypes.LOCAL ) {
						prefix = "local://";
					}
					
					IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + trigger.getAccess().getVariableID() );
					Object o;
					switch( trigger.getValue().getType() ) {
					
						case INT:
							o = Integer.parseInt( trigger.getValue().getValues() );
							break;
						
						case DOUBLE: 
							o = Double.parseDouble( trigger.getValue().getValues() );
							break;
					
						default:
							o = trigger.getValue().getValues();
				
					}
					try {
						service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( trigger.getType() ) );
					} catch( final ConnectionException e1 ) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
			
		});
		
		String prefix = null;
		if( this.detectorChannel.getRead().getAccess().getTransport() == TransportTypes.CA ) {
			prefix = "dal-epics://";
		} else if( this.detectorChannel.getRead().getAccess().getTransport() == TransportTypes.LOCAL ) {
			prefix = "local://";
		}
		
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.detectorChannel.getRead().getAccess().getVariableID() );
		
		/*try {
			this.currentValueLabel.setText( service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.detectorChannel.getRead().getType() ) ).toString() );
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		service.register( this, pv,  Helper.dataTypesToValueType( this.detectorChannel.getRead().getType() ) );
		//service.readValueAsynchronously( pv, Helper.dataTypesToValueType( this.detectorChannel.getRead().getType() ), this );
		
		
	}

	/**
	 * This methods disposes the detector channels widget and disconnects the composite from the simpleDAL service.
	 * 
	 */
	public void dispose() {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		service.unregister( this );
		super.dispose();
	}
	
	/**
	 * This methods gets called by simpleDAL when receognized a change of the connection state to the process variable.
	 * 
	 * On call, the label of the connection state gets changed.
	 * 
	 * @param connectionState The new connection state.
	 */
	public void connectionStateChanged( final ConnectionState connectionState ) {
		this.currentStateLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				
				String newText = "";
				switch( connectionState ) {
				
					case CONNECTED:
						newText = "connected";
						break;
						
					case CONNECTION_FAILED:
						newText = "connection failed";
						break;
						
					case CONNECTION_LOST:
						newText = "connection lost";
						break;
						
					case INITIAL:
						newText = "initial";
						break;
						
					case UNKNOWN:
						newText = "unknown";
						
				}
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, detectorChannel.getFullIdentifyer() + " changed connection state to " + newText + "." ) );
				currentStateLabel.setText( newText );
				
			}
			
		});
		
	}

	/**
	 * This method gets called when an error occured on the simpleDAL Connection. 
	 *
	 */
	public void errorOccured( final String error ) {
		Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.ERROR, error ) );
	}

	/**
	 * This method gets called when the value of the detector has changed.  
	 *
	 * @param value The new value
	 * @param timestamp The timestamp of the value.
	 */
	public void valueChanged( final Object value, final Timestamp timestamp) {
		this.currentValueLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				currentValueLabel.setText( value.toString() );
			}
			
		});
		
	}
	
	public void measurementDataTransmitted( final MeasurementData measurementData ) {
		if( this.detectorChannel.getName().equals( measurementData.getName() ) ) {
			this.currentEngineValueLabel.getDisplay().syncExec( new Runnable() {

				public void run() {
					currentEngineValueLabel.setText( measurementData.getValues().get( 0 ).toString() );
					
				}
				
			});
		}
		
	}

}
