package de.ptb.epics.eve.viewer.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * <code>Plot View</code> contains an xy plot and tables with statistics 
 * for two detector channels.  
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PlotView extends ViewPart {

	public static final String ID = "PlotView";

	private int id = -1;
	
	// the composite for the xy-plot
	private PlotGraphComposite plotGraphComposite;
	// the composite for the statistics tables
	private PlotDetectorComposite plotDetectorComposite;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		
		// we use a grid layout with two columns
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		parent.setLayout(gridLayout);
		
		// in the left column we put a our plotGraphComposite (the xy plot)
		plotGraphComposite = new PlotGraphComposite(parent, SWT.NONE);
		// in the right column we put the statistics tables
		plotDetectorComposite = new PlotDetectorComposite(parent, SWT.NONE);
		
		// some alignments for the plotDetectorComposite
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		this.plotDetectorComposite.setLayoutData(gridData);
		
		// finally a description and name are added
		this.setContentDescription(this.getTitle() + " " + 
								    this.getViewSite().getSecondaryId());
		this.setPartName("Plot " + this.getViewSite().getSecondaryId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() { }
	
	/**
	 * Getter for id.
	 * 
	 * @return the id of the plot view
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Setter for id.
	 * 
	 * @param id the id that should be set for the plot view
	 */
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * Links the plot window with a <code>plotWindow</code> in the data model.
	 * 
	 * @param plotWindow the <code>plotWindow</code> of the scan description 
	 * 		   representing the plot
	 * @param chid the id of the chain
	 * @param smid the id of the scan module
	 */
	public void setPlotWindow(PlotWindow plotWindow, int chid, int smid) {

		String detector1Id = null;
		String detector1Name = null;
		String detector2Id = null;
		String detector2Name = null;
		String motorPv = null;
		
		// get the number of y axis variables from the data model
		int yAxisCount = plotWindow.getYAxisAmount();
		
		// check if there is at least 1 variable registered
		if (yAxisCount > 0) {
			// there is, so get the id and name
			detector1Id = plotWindow.getYAxes().get(0).
									 getDetectorChannel().getID();
			detector1Name = plotWindow.getYAxes().get(0).
									   getDetectorChannel().getName();
			// if detector has no name, set it to the value of the id
			if ((detector1Name == null)) detector1Name = detector1Id;
		}
		// check if there are at least 2 variables registered
		if (yAxisCount > 1) {
			// there are, so get the id and name of the second
			detector2Id = plotWindow.getYAxes().get(1).
									 getDetectorChannel().getID();
			detector2Name = plotWindow.getYAxes().get(1).
									   getDetectorChannel().getName();
			// if detector has no name, set it to the value of the id
			if ((detector2Name == null) || detector2Name.length() == 0) 
				detector1Name = detector2Id;
		}
		// get the motor axis, its id and its name from the data model
		MotorAxis motorAxis = plotWindow.getXAxis();
		String motorId = motorAxis.getID();
		String motorName = motorAxis.getName();
		
		// check for valid motor id
		if ((motorId == null) || (motorId.length() < 1)) {
			System.err.println("invalid motorId: " + motorId);
			return;
		}
		
		// if motor has no name, set it to the value of the id
		if ((motorName == null) || (motorName.length() == 0)) 
			motorName = motorId;
		
		// if there is an Access for the motor axis and the transport type 
		// is Channel Access -> get the Process Variable
		if ((motorAxis.getGoto().getAccess() != null) && 
			(motorAxis.getGoto().getAccess().getTransport()==TransportTypes.CA))
				motorPv = motorAxis.getGoto().getAccess().getVariableID();
		
		String detector1 = null;
		String detector2 = null;
		
		if ((detector1Id != null) && (detector1Id.length() > 0)) 
			detector1 = detector1Id;
		if ((detector2Id != null) && (detector2Id.length() > 0)) 
			detector2 = detector2Id;
			
		// refresh the two composites contained in this view (plot and tables)
		// meaning update them with new values
		plotGraphComposite.refresh(plotWindow, chid, smid, motorId, motorName, 
						detector1, detector1Name, detector2, detector2Name);
		plotDetectorComposite.refresh(chid, smid, motorId, motorName, motorPv, 
						detector1, detector1Name, detector2, detector2Name);		
	}
}