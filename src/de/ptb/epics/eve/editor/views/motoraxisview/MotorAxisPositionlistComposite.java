package de.ptb.epics.eve.editor.views.motoraxisview;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

/**
 * <code>MotorAxisPositionlistComposite</code> is a composite to input a list
 * of positions for the motor axis.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class MotorAxisPositionlistComposite extends Composite {
	
	private Label positionlistLabel;
	private Text positionlistText;
	private PositionlistTextFocusListener positionlistTextFocusListener;
	private Label positionlistErrorLabel;
	private Label amountLabel;
	private Text amountText;
	
	private Axis axis;
	
	private MotorAxisView motorAxisView;
	
	/**
	 * Constructs a <code>MotorAxisPositionlistComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public MotorAxisPositionlistComposite(final Composite parent, 
										  final int style, 
										  MotorAxisView parentView) {
		super(parent, style);
		
		motorAxisView = parentView;
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		
		this.positionlistLabel = new Label(this, SWT.NONE);
		this.positionlistLabel.setText("Positionlist:");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.positionlistLabel.setLayoutData(gridData);
		
		this.positionlistErrorLabel = new Label(this, SWT.NONE);
		this.positionlistErrorLabel.setImage(PlatformUI.getWorkbench().
											 getSharedImages().getImage(
											 ISharedImages.IMG_OBJS_ERROR_TSK));
		
		this.positionlistText = new Text(this, SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.positionlistText.setLayoutData(gridData);
		positionlistTextFocusListener = new PositionlistTextFocusListener();
		this.positionlistText.addFocusListener(positionlistTextFocusListener);
		
		this.amountLabel = new Label(this, SWT.NONE);
		this.amountLabel.setText("Amount of positions:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.amountLabel.setLayoutData(gridData);
		
		this.amountText = new Text(this, SWT.BORDER);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.amountText.setLayoutData(gridData);
		this.amountText.setEnabled(false);
		
		this.positionlistLabel.setEnabled(false);
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

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 * 
	 * @param axis the the {@link de.ptb.epics.eve.data.scandescription.Axis}
	 * 		  that should be set
	 */
	public void setAxis(final Axis axis) {
		
		removeListeners();
		
		this.axis = axis;
		
		if(this.axis != null) {
			if(this.axis.getPositionlist() != null) { 
				this.positionlistText.setText(axis.getPositionlist()); 
			}
			
			checkForErrors();
			
			this.positionlistLabel.setEnabled(true);
		} else {
			this.positionlistText.setText("");
			this.positionlistLabel.setEnabled(false);
		}
		
		addListeners();
	}
	
	/*
	 * 
	 */
	private void checkForErrors()
	{
		this.positionlistErrorLabel.setImage(null);
		
		final Iterator<IModelError> it = this.axis.getModelErrors().iterator();
		
		while(it.hasNext()) {
			final IModelError modelError = it.next();
			if(modelError instanceof AxisError) {
				final AxisError axisError = (AxisError)modelError;
				if(axisError.getErrorType() == AxisErrorTypes.POSITIONLIST_NOT_SET) {
					this.positionlistErrorLabel.setImage(PlatformUI.
							getWorkbench().getSharedImages().getImage(
							ISharedImages.IMG_OBJS_ERROR_TSK));
					break;
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void addListeners()
	{
		positionlistText.addFocusListener(positionlistTextFocusListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		positionlistText.removeFocusListener(positionlistTextFocusListener);
	}
	
	///////////////////////////////////////////////////////////
	// Hier kommen jetzt die verschiedenen Listener Klassen
	///////////////////////////////////////////////////////////
	/**
	 * <code>ModifyListener</code> of PositionlistInput Text from
	 * <code>MotorAxisPositionlistComposite</code>
	 */
	class PositionlistTextFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {	
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
			motorAxisView.suspendModelUpdateListener();
			
			if(axis != null) {
				axis.setPositionlist(positionlistText.getText());
			}
			amountText.setText(Integer.toString(
						       positionlistText.getText().split(";").length));
			
			motorAxisView.resumeModelUpdateListener();
		}	
	}
}