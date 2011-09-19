package de.ptb.epics.eve.editor.views.motoraxisview;

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
	private PositionlistTextModifyListener positionlistTextModifyListener;
	private Label positionlistErrorLabel;
	private Label amountLabel;
	private Label amountLabelCount;
	
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
		this.positionlistLabel.setText("Positionlist:   ( Sign between positions is ; )");
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.positionlistLabel.setLayoutData(gridData);
		
		this.positionlistErrorLabel = new Label(this, SWT.NONE);
		
		this.positionlistText = new Text(this, SWT.BORDER | SWT.V_SCROLL);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.positionlistText.setLayoutData(gridData);
		this.positionlistTextModifyListener = new PositionlistTextModifyListener();
		this.positionlistText.addModifyListener(positionlistTextModifyListener);
		
		this.amountLabel = new Label(this, SWT.NONE);
		this.amountLabel.setText("Amount of positions:");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.amountLabel.setLayoutData(gridData);
		
		this.amountLabelCount = new Label(this, SWT.BORDER);
		this.amountLabelCount.setText("?");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.amountLabelCount.setLayoutData(gridData);

		this.positionlistLabel.setEnabled(false);
	}

	/**
	 * calculate the height to see all entries of this composite
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (amountLabel.getBounds().y + amountLabel.getBounds().height + 5);
	}

	/**
	 * calculate the width to see all entries of this composite
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (amountLabel.getBounds().x + amountLabel.getBounds().width + 5);
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
				this.amountLabelCount.setText(Integer.toString(
					       this.positionlistText.getText().split(";").length));
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
		this.positionlistErrorLabel.setToolTipText("");
		
		final Iterator<IModelError> it = this.axis.getModelErrors().iterator();
		
		while(it.hasNext()) {
			final IModelError modelError = it.next();
			if(modelError instanceof AxisError) {
				final AxisError axisError = (AxisError)modelError;
				if(axisError.getErrorType() == AxisErrorTypes.POSITIONLIST_NOT_SET) {
					this.positionlistErrorLabel.setImage(PlatformUI.
							getWorkbench().getSharedImages().getImage(
							ISharedImages.IMG_OBJS_ERROR_TSK));
					this.positionlistErrorLabel.setToolTipText("Positionlist has no position!");
					this.positionlistErrorLabel.getParent().layout();
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
		positionlistText.addModifyListener(positionlistTextModifyListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		positionlistText.removeModifyListener(positionlistTextModifyListener);
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
		public void modifyText(ModifyEvent e) {
			motorAxisView.suspendModelUpdateListener();

			if(axis != null) {
				axis.setPositionlist(positionlistText.getText());

				if (positionlistText.getText().equals("")) {
					amountLabelCount.setText("0");
				} else {
					amountLabelCount.setText(Integer.toString(
						       positionlistText.getText().split(";").length));
				}	
			}
			checkForErrors();
			motorAxisView.resumeModelUpdateListener();
		}	
	}
}