package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.Device;

public class DeviceComposite extends Composite implements IProcessVariableValueListener {

	private Device device;
	
	private Label deviceNameLabel;
	
	private Label currentValueLabel;
	
	private Combo inputCombo;
	private Text inputText;
	
	private Label unitLabel;
	
	private Button setButton;
	
	private Label currentStateLabel;
	
	public DeviceComposite( final Composite parent, final int style, final Device device ) {
		super( parent, style );
		this.device = device;
		
		initialize();
	}
	
	private void initialize() {
		
		//this.setBackground( new Color( this.getBackground().getDevice(), 255, 0, 0 ) );
		
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 0;
		
		this.setLayout( gridLayout );
		
		GridData gridData = new GridData();
		
		this.deviceNameLabel = new Label( this, SWT.NONE );
		this.deviceNameLabel.setText( this.device.getName() );
		//gridData.horizontalAlignment = SWT.BEGINNING;
		//gridData.minimumWidth = 600;
		gridData.widthHint = 150;
		this.deviceNameLabel.setLayoutData( gridData );
		
		this.currentValueLabel = new Label( this, SWT.NONE );
		this.currentValueLabel.setText( "?" );
		gridData = new GridData();
		//gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.widthHint = 80;
		this.currentValueLabel.setLayoutData( gridData );
		
		if( this.device.isDiscrete()) {
			this.inputCombo = new Combo( this, SWT.NONE );
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			this.inputCombo.setLayoutData( gridData );
			this.inputCombo.setItems( this.device.getValue().getDiscreteValues().toArray( new String[0] ) );
		} else {
			this.inputText = new Text( this, SWT.BORDER );
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			this.inputText.setLayoutData( gridData );
		}
		
		this.unitLabel = new Label( this, SWT.NONE );
		if( this.device.getUnit() != null ) {
			if( this.device.getUnit().getValue() != null ) {
				this.unitLabel.setText( this.device.getUnit().getValue() );
			} else {
				String prefix = null;
				if( this.device.getUnit().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( this.device.getUnit().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.device.getUnit().getAccess().getVariableID() );
				try {
					final String unit = service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.device.getUnit().getAccess().getType() ) ).toString();
					this.unitLabel.setText( unit );
				} catch( ConnectionException e ) {
					this.unitLabel.setText( "?" );
				}
			}
		}
		gridData = new GridData();
		//gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.widthHint = 20;
		this.unitLabel.setLayoutData( gridData );
		
		
		this.setButton = new Button( this, SWT.PUSH );
		this.setButton.setText( "Set" );
		this.setButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
				ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance();
				String prefix = null;
				if( device.getValue().getAccess().getTransport() == TransportTypes.CA ) {
					prefix = "dal-epics://";
				} else if( device.getValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
					prefix = "local://";
				}
				IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + device.getValue().getAccess().getVariableID() );
				try {
					Object o = null;
					switch( device.getValue().getValue().getType() ) {
					
						case INT:
							o = Integer.parseInt( inputText != null?inputText.getText():inputCombo.getText() );
							break;
							
						case DOUBLE: 
							o = Double.parseDouble( inputText != null?inputText.getText():inputCombo.getText() );
							break;
						
						default:
							o = inputText != null?inputText.getText():inputCombo.getText();
					
					}
					service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( device.getValue().getValue().getType() ) );
				} catch( final ConnectionException e1 ) {
					
					e1.printStackTrace();
				}
			}
			
		});
		
		this.currentStateLabel = new Label( this, SWT.NONE );
		this.currentStateLabel.setText( "Disconnected" );
		
		gridData = new GridData();
		//gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.widthHint = 150;
		this.currentStateLabel.setLayoutData( gridData );
		
		String prefix = null;
		if( device.getValue().getAccess().getTransport() == TransportTypes.CA ) {
			prefix = "dal-epics://";
		} else if( device.getValue().getAccess().getTransport() == TransportTypes.LOCAL ) {
			prefix = "local://";
		}
		
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( prefix + this.device.getValue().getAccess().getVariableID() );
		
		/*try {
			this.currentValueLabel.setText( service.readValueSynchronously( pv, Helper.dataTypesToValueType( this.device.getValue().getType() ) ).toString() );
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		service.register( this, pv,  Helper.dataTypesToValueType( this.device.getValue().getType() ) );
		//service.readValueAsynchronously( pv, Helper.dataTypesToValueType( this.device.getValue().getType() ), this );
		
	}

	public void dispose() {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		service.unregister( this );
		super.dispose();
	}
	
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
				
				currentStateLabel.setText( newText );
				
			}
			
		});
	}

	public void errorOccured( final String error ) {
		
		
	}

	public void valueChanged( final Object value, final Timestamp timestamp ) {
		
		this.currentValueLabel.getDisplay().syncExec( new Runnable() {

			public void run() {
				currentValueLabel.setText( value.toString() );
			}
			
		});
		
	}
	
}
