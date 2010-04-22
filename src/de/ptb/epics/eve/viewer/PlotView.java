package de.ptb.epics.eve.viewer;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.TransportTypes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.ChainStatus;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.intern.EngineStatus;

public class PlotView extends ViewPart {

	protected static final String ID = "PlotView";

	private int id = -1;
	
	private DetectorChannel detectorChannel1 = null;
	private DetectorChannel detectorChannel2 = null;
	
	private Label motorPositionLabel;
	private Label currentMotorPositionLabel;
	private Label detector1Label;
	private Label detector1CurrentValueLabel;
	private Label detector2Label;
	private Label detector2CurrentValueLabel;
	
	private Label normalizeLabel;
	private Combo normalizeComboBox;
	
	
	private PlotWindow plotWindow;
	private plotGraphComposite plotGraphComposite;
	private PlotDetectorComposite plotDetectorComposite;

	private String detector2Id;

	private String detector1Id;

	private String motorId;

	private String motorPv;
	
	@Override
	public void createPartControl( final Composite parent ) {
		
		final GridLayout gridLayout = new GridLayout();
		
		gridLayout.numColumns = 2;

		parent.setLayout( gridLayout );
		plotGraphComposite = new plotGraphComposite(parent, SWT.NONE );
		plotDetectorComposite = new PlotDetectorComposite(parent, SWT.NONE );
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		//gridData.verticalAlignment = SWT.FILL;
		this.plotDetectorComposite.setLayoutData( gridData );
		
		this.setContentDescription(this.getTitle()+" "+this.getViewSite().getSecondaryId());
		this.setPartName("Plot "+this.getViewSite().getSecondaryId());
	}

	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public int getId() {
		return this.id;
	}

	public void setId( final int id ) {
		this.id = id;
	}

	public void setPlotWindow(PlotWindow plotWindow, int chid, int smid) {

		String detector1Id = null;
		String detector1Name = null;
		String detector2Id = null;
		String detector2Name = null;
		String motorPv = null;
		
		int yAxisCount = plotWindow.getYAxisAmount();
		if (yAxisCount > 0) {
			detector1Id = plotWindow.getYAxes().get(0).getDetectorChannel().getID();
			detector1Name = plotWindow.getYAxes().get(0).getDetectorChannel().getName();
			if ((detector1Name == null)) detector1Name = detector1Id;
		}
		if (yAxisCount > 1) {
			detector2Id = plotWindow.getYAxes().get(1).getDetectorChannel().getID();
			detector2Name = plotWindow.getYAxes().get(1).getDetectorChannel().getName();
			if ((detector2Name == null) || detector2Name.length() == 0) detector1Name = detector2Id;
		}
		MotorAxis motorAxis = plotWindow.getXAxis();
		String motorId = motorAxis.getID();
		String motorName = motorAxis.getName();
		
		if ((motorId == null) || (motorId.length() < 1)) {
			// TODO create proper error message
			System.err.println("invalid motorId: " + motorId);
			return;
		}
		
		if ((motorName == null) || (motorName.length() == 0)) motorName = motorId;
		if ((motorAxis.getGoto().getAccess() != null) && (motorAxis.getGoto().getAccess().getTransport() == TransportTypes.CA))
				motorPv = motorAxis.getGoto().getAccess().getVariableID();
		
		this.detector1Id = detector1Id;
		this.detector2Id = detector2Id;
		this.motorId = motorId;
		this.motorPv = motorPv;
		
		String detector1 = null;
		String detector2 = null;
		
		if ((detector1Id != null) && (detector1Id.length() > 0)) detector1 = detector1Id;
		if ((detector2Id != null) && (detector2Id.length() > 0)) detector2 = detector2Id;
				
		plotGraphComposite.refresh(plotWindow, chid, smid, motorId, motorName, detector1, detector1Name, detector2, detector2Name);
		plotDetectorComposite.refresh(chid, smid, motorId, motorPv, detector1, detector2);
		
	}
}
