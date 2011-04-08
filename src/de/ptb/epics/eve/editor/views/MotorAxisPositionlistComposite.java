package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>MotorAxisPositionlistComposite</code> is a composite to input a list
 * of positions for the motor axis.
 * @author Hartmut Scherr
 *
 */
public class MotorAxisPositionlistComposite extends Composite implements IModelUpdateListener {
	
	private Label positionlistLabel;
	private Text positionlistText;
	private Label positionlistErrorLabel;
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
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.positionlistLabel.setLayoutData( gridData );
		
		this.positionlistErrorLabel = new Label( this, SWT.NONE );
		this.positionlistErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ));
		
		this.positionlistText = new Text( this, SWT.BORDER | SWT.V_SCROLL );
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.positionlistText.setLayoutData( gridData );
		this.positionlistText.addModifyListener( new PositionlistTextModifyListener());
		
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

	/**
	 * calculate the height to see all entries of this composite
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (amountText.getBounds().y + amountText.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (amountText.getBounds().x + amountText.getBounds().width + 5);
	}

	public void setAxis( final Axis axis ) {
		
		if( this.axis != null ) {
			this.axis.removeModelUpdateListener( this );
		}
		this.axis = axis;
		if( this.axis != null ) {
			if( this.axis.getPositionlist() != null ) { 
				this.positionlistText.setText( axis.getPositionlist() ); 
			}
			final Iterator< IModelError > it = this.axis.getModelErrors().iterator();
			this.positionlistErrorLabel.setImage( null );
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof AxisError ) {
					final AxisError axisError = (AxisError)modelError;
					if( axisError.getErrorType() == AxisErrorTypes.POSITIONLIST_NOT_SET ) {
						this.positionlistErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
						break;
					}
				}
			}
			this.positionlistLabel.setEnabled( true );
			this.axis.addModelUpdateListener( this );
			
		} else {
			this.positionlistText.setText( "" );
			this.positionlistLabel.setEnabled( false );
		}
	}

	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.positionlistErrorLabel.setImage( null );
		final Iterator< IModelError > it = this.axis.getModelErrors().iterator();
		while( it.hasNext() ) {
			final IModelError modelError = it.next();
			if( modelError instanceof AxisError ) {
				final AxisError axisError = (AxisError)modelError;
				if( axisError.getErrorType() == AxisErrorTypes.POSITIONLIST_NOT_SET ) {
					this.positionlistErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					break;
				}
			}
		}
	}
	
	@Override
	public void dispose() {
		if( this.axis != null ) {
			this.axis.removeModelUpdateListener( this );
		}
		super.dispose();
	}

	///////////////////////////////////////////////////////////
	// Hier kommen jetzt die verschiedenen Listener Klassen
	///////////////////////////////////////////////////////////
	/**
	 * <code>ModifyListener</code> of PositionlistInput Text from
	 * <code>MotorAxisPositionlistComposite</code>
	 */
	class PositionlistTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText( final ModifyEvent e ) {
			if( axis != null ) {
				axis.setPositionlist( positionlistText.getText() );
			}
			amountText.setText( "" + positionlistText.getText().split( ";" ).length );
		}
		
	};

}
