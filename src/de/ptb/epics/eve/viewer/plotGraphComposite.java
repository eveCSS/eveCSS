/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.ChainStatus;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;

/**
 * @author eden
 *
 */


public class plotGraphComposite extends Composite implements IMeasurementDataListener, IChainStatusListener {

	private String detector1Id;
	private String detector2Id;
	private String motorId;
	private Label detector1Label;
	private EngineDataLabel detector1EngineLabel;
	private Label detector2Label;
	private EngineDataLabel detector2EngineLabel;
	private Combo normalizeComboBox;
	private Label normalizeLabel;
	private Double xValue;
	private PlotWindow plotWindow;
	private int chid;
	private int smid;
	private boolean isActive;
	private Double y2value;
	private Double y1value;
	private Canvas canvas;
	private XYPlot xyPlot;
	private String detector1Name;
	private String detector2Name;
	
	/**
	 * @param parent
	 * @param style
	 */
	public plotGraphComposite(Composite parent, int style) {
		super(parent, style);

		Activator.getDefault().getEcp1Client().addMeasurementDataListener( this );
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		setLayout( gridLayout );
		
		GridData gridData = new GridData();
		gridData.verticalSpan = 2;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.minimumHeight = 300;
		gridData.minimumWidth = 500;
		canvas = new Canvas(this, SWT.NONE);
        canvas.setLayoutData(gridData);
	    //use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		//set it as the content of LightwightSystem
		xyPlot = new XYPlot();
		lws.setContents(xyPlot);
	}

	public void refresh(PlotWindow plotWindow, int chid, int smid, String motorId, String motorName, String detector1Id, String detector1Name, String detector2Id, String detector2Name) {

		Font newFont = Activator.getDefault().getFont("VIEWERFONT");
		GridData gridData;

		// do not clean if plot has "isInit=false"
		if ((this.motorId == motorId) && (this.detector1Id == detector1Id) && 
				(this.detector2Id == detector2Id)) {
			if (plotWindow.isInit()) xyPlot.removeAllTraces();
		}
		else {
			xyPlot.removeAllTraces();
		}

		this.plotWindow = plotWindow;
		this.chid = chid;
		this.smid = smid;
		this.detector1Id = detector1Id;
		this.detector2Id = detector2Id;
		this.motorId = motorId;

		xyPlot.setXAxisTitle(motorName);
               
		if (this.detector1Id != null){
			this.detector1Name = detector1Name;
			if (xyPlot.getTrace(detector1Name) == null){
				xyPlot.addTrace(detector1Name);
			}

	        if (this.detector1Label == null) {
				this.detector1Label = new Label( this, SWT.NONE );
				this.detector1Label.setFont(newFont);
			}
			this.detector1Label.setText( detector1Name );
			
			if (this.detector1EngineLabel == null) {
				this.detector1EngineLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineLabel.setFont(newFont);
				gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				this.detector1EngineLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineLabel.setDataId(detector1Id);
			}
		}
		else {
			xyPlot.removeTrace(detector1Name);
			this.detector1Name = null;
			if (detector1Label != null) { detector1Label.dispose(); detector1Label = null; }
			if (detector1EngineLabel != null) { detector1EngineLabel.dispose(); detector1EngineLabel = null; }
		}
		if (this.detector2Id != null){
			this.detector2Name = detector2Name;
			if (xyPlot.getTrace(detector2Name) == null){
				xyPlot.addTrace(detector2Name);
			}

	        if (this.detector2Label == null) {
				this.detector2Label = new Label( this, SWT.NONE );
				this.detector2Label.setFont(newFont);
			}
	        this.detector2Label.setText( detector2Name );
	        
			if (this.detector2EngineLabel == null) {
				this.detector2EngineLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineLabel.setFont(newFont);
				gridData = new GridData();
				gridData.horizontalAlignment = SWT.FILL;
				this.detector2EngineLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineLabel.setDataId(detector2Id);
			}
		}
		else {
			xyPlot.removeTrace(detector2Name);
			this.detector2Name = null;
			if (detector2Label != null) { detector2Label.dispose(); detector2Label = null; }
			if (detector2EngineLabel != null) { detector2EngineLabel.dispose(); detector2EngineLabel = null; }
		}
		if ((this.detector1Id != null) && (this.detector2Id != null)){

			if (this.normalizeLabel == null) {
				this.normalizeLabel = new Label( this, SWT.NONE );
				this.normalizeLabel.setFont(newFont);
				this.normalizeComboBox = new Combo( this, SWT.NONE );
				gridData = new GridData();
				//gridData.grabExcessHorizontalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.normalizeComboBox.setLayoutData( gridData );
				this.normalizeComboBox.setFont(newFont);
			}
			this.normalizeLabel.setText( "Normalize:" );
			final String[] normalizeItems = { "None", detector1Name+"/"+detector2Name, detector2Name+"/"+detector1Name };
			this.normalizeComboBox.setItems( normalizeItems ); 
		}
		else {
			if (normalizeLabel != null) { normalizeLabel.dispose(); normalizeLabel = null; }
			if (normalizeComboBox != null) { normalizeComboBox.dispose(); normalizeComboBox = null; }
		}
		canvas.layout();
		canvas.redraw();
		this.layout();
		this.redraw();
		this.getParent().layout();
		this.getParent().redraw();
	}

	@Override
	public void measurementDataTransmitted(MeasurementData measurementData) {

		if (measurementData == null) return;
		boolean redraw = false;
		
		if (!isActive) return;
		boolean detector1HaveData = false;
		boolean detector2HaveData = false;
		
//		System.err.println("DEBUG PlotGraphComposite: new plot data " + measurementData.getName() + " / " + this.detector1Id);
//		// TODO DEBUG
//		if (true) return;
		
		if ((this.detector1Id != null) && this.detector1Id.equals(measurementData.getName()) && (measurementData.getDataModifier() == DataModifier.UNMODIFIED)) {
			DataType dt = measurementData.getDataType();
			if ((dt == DataType.DOUBLE) || (dt == DataType.FLOAT) || (dt == DataType.INT32) || 
					(dt == DataType.INT16) || (dt == DataType.INT8)){
				y1value = (Double) measurementData.getValues().get(0);
				redraw = true;
				detector1HaveData = true;
			}
		}
		else if ((this.detector2Id != null) && this.detector2Id.equals(measurementData.getName()) && (measurementData.getDataModifier() == DataModifier.UNMODIFIED)) {
			DataType dt = measurementData.getDataType();
			if ((dt == DataType.DOUBLE) || (dt == DataType.FLOAT) || (dt == DataType.INT32) || 
					(dt == DataType.INT16) || (dt == DataType.INT8)){
				y2value = (Double) measurementData.getValues().get(0);
				redraw = true;
				detector2HaveData = true;
			}
		}
		else if (this.motorId.equals(measurementData.getName()) && (measurementData.getDataModifier() == DataModifier.UNMODIFIED)) {
			DataType dt = measurementData.getDataType();
			if ((dt == DataType.DOUBLE) || (dt == DataType.FLOAT) || (dt == DataType.INT32) || 
					(dt == DataType.INT16) || (dt == DataType.INT8)){
				xValue = (Double) measurementData.getValues().get(0);
			}
		}
		else
			return;
		
		final boolean det1HaveData = detector1HaveData;
		final boolean det2HaveData = detector2HaveData;
		if (redraw && !this.isDisposed()) this.getDisplay().syncExec( new Runnable() {

			public void run() {
				System.out.println("redrawing plot ");
				if (!isDisposed()) {
					if (det1HaveData){
						xyPlot.setData(detector1Name, xValue,y1value);
					}
					if (det2HaveData){
						xyPlot.setData(detector2Name, xValue,y2value);
					}
				}
			}
		});
	}

	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {

		int chid = chainStatusCommand.getChainId();
		int smid = chainStatusCommand.getScanModulId();
		if ((this.chid == chid) && (this.smid == smid) && (chainStatusCommand.getChainStatus() == ChainStatus.EXECUTING_SM)){
			isActive = true;
			System.out.println("Executing chid: " + chid + "; smid: " + smid);
		}
		else {
			isActive = false;
		}
		if (detector1EngineLabel != null) detector1EngineLabel.setActive(isActive);
		if (detector1EngineLabel != null) detector1EngineLabel.setActive(isActive);
	}
}
