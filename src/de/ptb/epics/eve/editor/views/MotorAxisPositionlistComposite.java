package de.ptb.epics.eve.editor.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;

public class MotorAxisPositionlistComposite extends Composite  {
	
	private Label positionlistLabel;
	private Text positionlistInput;
	private Label amountLabel;
	private Text amountText;
	
	private Axis axis;
	
	public MotorAxisPositionlistComposite( final Composite parent, final int style ) {
		super( parent, style );
		initialize();
	}

	private void initialize() {
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout( gridLayout );

		
		
		this.positionlistLabel = new Label( this, SWT.NONE );
		this.positionlistLabel.setText( "Positionlist:" );
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.positionlistLabel.setLayoutData( gridData );
		
		this.positionlistInput = new Text( this, SWT.BORDER | SWT.V_SCROLL );
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.positionlistInput.setLayoutData( gridData );
		this.positionlistInput.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( axis != null ) {
					axis.setPositionlist( positionlistInput.getText() );
				}
			}
			
		});
		this.positionlistInput.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				amountText.setText( "" + positionlistInput.getText().split( ";" ).length );
			}
			
		});
		
		this.amountLabel = new Label( this, SWT.NONE );
		this.amountLabel.setText( "Amount of positions:" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.amountLabel.setLayoutData( gridData );
		
		this.amountText = new Text( this, SWT.BORDER );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.amountText.setLayoutData( gridData );
		this.amountText.setEnabled( false );
		
		this.positionlistLabel.setEnabled( false );
		
	}
	
	public void setAxis( final Axis axis ) {
		this.axis = axis;
		if( this.axis != null ) {
			if( this.axis.getPositionlist() != null ) { 
				this.positionlistInput.setText( axis.getPositionlist() ); 
			}
			this.positionlistLabel.setEnabled( true );
		} else {
			this.positionlistInput.setText( "" );
			this.positionlistLabel.setEnabled( false );
		}
	}

}
