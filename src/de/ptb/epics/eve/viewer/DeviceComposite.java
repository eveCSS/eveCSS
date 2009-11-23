package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.swt.SWT;
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
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		
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
		gridData.widthHint = 20;
		this.currentValueLabel.setLayoutData( gridData );
		
		if( this.device.isDiscrete()) {
			this.inputCombo = new Combo( this, SWT.NONE );
			gridData = new GridData();
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			this.inputCombo.setLayoutData( gridData );
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
				// Einheit vom Netz lesen.
			}
		}
		gridData = new GridData();
		//gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.widthHint = 20;
		this.unitLabel.setLayoutData( gridData );
		
		
		this.setButton = new Button( this, SWT.PUSH );
		this.setButton.setText( "Set" );
		
		this.currentStateLabel = new Label( this, SWT.NONE );
		this.currentStateLabel.setText( "Disconnected" );
		
		gridData = new GridData();
		//gridData.horizontalAlignment = SWT.BEGINNING;
		gridData.widthHint = 150;
		this.currentStateLabel.setLayoutData( gridData );
		
		RowData rowData = new RowData();
		
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();

		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress( "" );
		
		service.register( this, pv,  Helper.dataTypesToValueType( this.device.getValue().getValue().getType() ) );
		
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
