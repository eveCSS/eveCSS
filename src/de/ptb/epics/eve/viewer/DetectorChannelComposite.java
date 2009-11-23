package de.ptb.epics.eve.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;

public class DetectorChannelComposite extends Composite {

	private DetectorChannel detectorChannel;
	
	private Label detectorChannelNameLabel;
	private Label currentValueLabel;
	private Label unitLabel;
	private Label methodLabel;
	private Label currentStateLabel;
	private Button triggerButton;
	
	public DetectorChannelComposite( final Composite parent, final int style, final DetectorChannel detectorChannel ) {
		super( parent, style );
		this.detectorChannel = detectorChannel;
		
		initialize();
	}
	
	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 6;
		
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
		
		this.unitLabel = new Label( this, SWT.NONE );
		if( this.detectorChannel.getUnit() != null ) {
			if( this.detectorChannel.getUnit().getValue() != null ) {
				this.unitLabel.setText( this.detectorChannel.getUnit().getValue() );
			} else {
				// Einheit vom Netz lesen.
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
	}

}
