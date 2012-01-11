package de.ptb.epics.eve.editor.views.motoraxisview;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisPositionlistComposite</code> is a 
 * {@link org.eclipse.swt.widgets.Composite} contained in the 
 * <code>MotorAxisView</code>. It allows entering position lists for 
 * motor axes using a position list as step function.
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class PositionlistComposite extends Composite {
	
	private static Logger logger = 
			Logger.getLogger(PositionlistComposite.class.getName());
	
	// position list Label
	private Label positionlistLabel;
	private ControlDecoration positionlistLabelDecoration;
	
	// position list Text
	private Text positionlistText;
	private PositionlistTextModifyListener positionlistTextModifyListener;
	private ControlDecoration positionlistTextDecoration;
	
	// position count Label
	private Label positionCountLabel;
	
	private Image infoImage;
	private Image errorImage;
	
	private Axis axis;
	
	private MotorAxisView motorAxisView;
	
	/**
	 * Constructs a <code>MotorAxisPositionlistComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view the composite is contained in
	 */
	public PositionlistComposite(final Composite parent, 
										  final int style, 
										  MotorAxisView parentView) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginRight = 12;
		this.setLayout(gridLayout);
		
		motorAxisView = parentView;
		
		this.infoImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_INFORMATION).getImage();
		this.errorImage = FieldDecorationRegistry.getDefault().getFieldDecoration(
				FieldDecorationRegistry.DEC_ERROR).getImage();
		
		// position list Label
		this.positionlistLabel = new Label(this, SWT.NONE);
		this.positionlistLabel.setText("Positionlist:");
		this.positionlistLabelDecoration = new ControlDecoration(
				positionlistLabel, SWT.TOP | SWT.RIGHT);
		this.positionlistLabelDecoration.setImage(infoImage);
		this.positionlistLabelDecoration.setDescriptionText(
				"use , (comma) as delimiter");
		this.positionlistLabelDecoration.show();
		
		// position list Text field 
		this.positionlistText = new Text(this, SWT.BORDER | SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.positionlistText.setLayoutData(gridData);
		this.positionlistTextModifyListener = new PositionlistTextModifyListener();
		this.positionlistText.addModifyListener(positionlistTextModifyListener);
		this.positionlistTextDecoration = new ControlDecoration(
				positionlistText, SWT.TOP | SWT.RIGHT);
		
		// position count label
		this.positionCountLabel = new Label(this, SWT.NONE);
		this.positionCountLabel.setText("0 positions");
	} // end of: Constructor

	/**
	 * Calculates the height to see all entries of this composite
	 * 
	 * @return the needed height of Composite to see all entries
	 */
	public int getTargetHeight() {
		return (positionCountLabel.getBounds().y + 
				positionCountLabel.getBounds().height + 5);
	}

	/**
	 * Calculates the width to see all entries of this composite
	 * 
	 * @return the needed width of Composite to see all entries
	 */
	public int getTargetWidth() {
		return (positionCountLabel.getBounds().x + 
				positionCountLabel.getBounds().width + 5);
	}

	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 * 
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis}
	 * 		  that should be set
	 */
	public void setAxis(final Axis axis) {
		removeListeners();
		
		this.axis = axis;
		
		if(this.axis != null) {
			if(this.axis.getPositionlist() != null) { 
				this.positionlistText.setText(axis.getPositionlist());
				countPositions();
			}
			checkForErrors();
		} else {
			this.positionlistText.setText("");
			this.positionCountLabel.setText("0 positions");
		}
		addListeners();
	}

	
	/*
	 * 
	 */
	private void countPositions() {
		if(positionlistText.getText().isEmpty()) {
			this.positionCountLabel.setText("0 positions");
		} else {
			int count = positionlistText.getText().split(",").length;
			if(count == 1) {
				this.positionCountLabel.setText("1 position");
				logger.debug("1 position");
			} else {
				this.positionCountLabel.setText(count + " positions");
				if(logger.isDebugEnabled()) {
					logger.debug(count + " positions");
				}
			}
		}
		this.positionCountLabel.getParent().layout();
	}
	
	/*
	 * 
	 */
	private void checkForErrors() {
		this.positionlistTextDecoration.hide();
		
		for(IModelError error : this.axis.getModelErrors()) {
			if(error instanceof AxisError) {
				final AxisError axisError = (AxisError)error;
				if(axisError.getErrorType() == AxisErrorTypes.POSITIONLIST_NOT_SET) {
					this.positionlistTextDecoration.setImage(errorImage);
					this.positionlistTextDecoration.setDescriptionText(
							"Please provide at least one position!");
					this.positionlistTextDecoration.show();
					break;
				}
			}
		}
	}
	
	/*
	 * 
	 */
	private void addListeners() {
		positionlistText.addModifyListener(positionlistTextModifyListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners() {
		positionlistText.removeModifyListener(positionlistTextModifyListener);
	}
	
	/* ********************************************************************* */
	/* ************************** Listeners ******************************** */
	/* ********************************************************************* */
	
	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>positionlistText</code>.
	 */
	private class PositionlistTextModifyListener implements ModifyListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			
			if(axis != null) {
				axis.setPositionlist(positionlistText.getText());
				countPositions();
			}
			checkForErrors();
		}
	}
}