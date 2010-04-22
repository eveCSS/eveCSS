package de.ptb.epics.eve.viewer;


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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
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
public class DetectorChannelComposite extends Composite implements SelectionListener {

	/**
	 * The detector channel where this composite is connected to.
	 * 
	 */
	private final DetectorChannel detectorChannel;
	
	private Button closeButton;
	private Font newFont;
	
	/**
	 * The label, which shows the name of the detector channel.
	 * 
	 */
	private Label detectorChannelNameLabel;
	
	/**
	 * The label, which shows the current value of the detector channel.
	 * 
	 */
	private Control valueLabel;
	
	private EngineDataLabel currentEngineValueLabel;
	
	/**
	 * The label, which shows the unit of the detector channel.
	 * 
	 */
	private Control unitLabel;
	
	/**
	 * The button, that sends a trigger signal to the detector.
	 * 
	 */
	private PvButtonComposite triggerButton;
	
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
		newFont = Activator.getDefault().getFont("VIEWERFONT");
		
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		gridLayout.marginHeight = 0;
		
		this.setLayout( gridLayout );
				
		ImageData imageData = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ).getImageData();
		Image deleteIcon = new Image(this.getForeground().getDevice(), imageData.scaledTo(12, 12));
		
		this.closeButton = new Button( this, SWT.NONE );
		this.closeButton.setImage(deleteIcon);
		this.closeButton.addSelectionListener( this );
		
		// channel name
		this.detectorChannelNameLabel = new Label( this, SWT.NONE );
		this.detectorChannelNameLabel.setText( this.detectorChannel.getName() );
		GridData gridData = new GridData();
		gridData.widthHint = 140;
		this.detectorChannelNameLabel.setLayoutData( gridData );
		this.detectorChannelNameLabel.setFont(newFont);
		
		// channel value
		if ((detectorChannel.getRead() != null) && ( detectorChannel.getRead().getAccess().getTransport() == TransportTypes.CA )) {
			this.valueLabel = new PvLabelComposite( this, SWT.NONE, detectorChannel.getRead().getAccess().getVariableID() );
		}			
		else {
			this.valueLabel = new Label( this, SWT.NONE );
			((Label) this.valueLabel).setText("unknown");
		}
		gridData = new GridData();
		gridData.widthHint = 100;
		this.valueLabel.setLayoutData( gridData );
		this.valueLabel.setFont(newFont);
		
		this.currentEngineValueLabel = new EngineDataLabel( this, SWT.NONE, detectorChannel.getID());
		this.currentEngineValueLabel.setText( "-" );
		gridData = new GridData();
		gridData.widthHint = 100;
		this.currentEngineValueLabel.setLayoutData( gridData );
		this.currentEngineValueLabel.setFont(newFont);
		
		// unit Label
		if ((detectorChannel.getUnit() != null) && (detectorChannel.getUnit().getAccess() != null) &&( detectorChannel.getUnit().getAccess().getTransport() == TransportTypes.CA )) {
			unitLabel = new PvLabelComposite( this, SWT.NONE, this.detectorChannel.getUnit().getAccess().getVariableID() );
			((PvLabelComposite) this.unitLabel).setText("unit");
		}			
		else {
			this.unitLabel = new Label( this, SWT.NONE );
			if ((detectorChannel.getUnit() != null) && (detectorChannel.getUnit().getValue() != null))
				((Label) this.unitLabel).setText(this.detectorChannel.getUnit().getValue());
			else
				((Label) this.unitLabel).setText("unit");
		}
		gridData = new GridData();
		gridData.widthHint = 30;
		this.unitLabel.setLayoutData( gridData );
		this.unitLabel.setFont(newFont);
		
		// trigger button if needed
		Function trigger = detectorChannel.getTrigger()!=null?detectorChannel.getTrigger():detectorChannel.getDetector().getTrigger();
		if( trigger != null ) {
			this.triggerButton = new PvButtonComposite( this, SWT.PUSH, trigger.getAccess().getVariableID(), trigger.getValue() );
			this.triggerButton.setText( "Trigger" );
			this.triggerButton.setFont(newFont);
		}
		else {
			new Label( this, SWT.NONE );
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void widgetSelected(SelectionEvent e) {
		this.dispose();
		this.getParent().layout();
		this.getParent().redraw();
		
	}

	public DetectorChannel getDetectorChannel() {
		return this.detectorChannel;
	}
	
}
