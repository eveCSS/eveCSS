/**
 * 
 */
package de.ptb.epics.eve.viewer;

import java.util.Locale;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.cosylab.util.PrintfFormat;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;

/**
 * @author eden
 *
 */
public class EngineDataLabel extends Composite implements
		IMeasurementDataListener {

	private String dataId;
	private Label labelWidget;
	private DataModifier datamodifier;
	private Boolean isActive;

	/**
	 * @param parent
	 * @param style
	 */
	public EngineDataLabel(Composite parent, int style, String dataId) {
		super(parent, style);
		this.dataId = dataId;
		this.setLayout(new FillLayout());
		labelWidget = new Label(this, style);
		labelWidget.setToolTipText(dataId);
		Activator.getDefault().getEcp1Client().addMeasurementDataListener( this );
		datamodifier = DataModifier.UNMODIFIED;
		isActive = true;
	}

	/* (non-Javadoc)
	 * @see de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener#measurementDataTransmitted(de.ptb.epics.eve.ecp1.client.model.MeasurementData)
	 */
	@Override
	public void measurementDataTransmitted(final MeasurementData measurementData) {

		if (!isActive) return;
		if (measurementData == null) return;
		if (dataId == null) return;
		
//		System.out.println("DEBUG EngineDataLabel: new label data " + measurementData.getName());
//		// TODO DEBUG
//		if (true) return;

		if (this.dataId.equals(measurementData.getName()) && (measurementData.getDataModifier() == datamodifier)) {
			if (!labelWidget.isDisposed()) labelWidget.getDisplay().syncExec( new Runnable() {

				public void run() {
					if (!labelWidget.isDisposed()){
						if ((measurementData.getDataType() == DataType.DOUBLE) || (measurementData.getDataType() == DataType.FLOAT)) {
							Double value = (Double) measurementData.getValues().get(0);
							labelWidget.setText( new PrintfFormat(Locale.ENGLISH, "%12.4g").sprintf(value));
						}
						else {
							labelWidget.setText( measurementData.getValues().get( 0 ).toString());
						}							
					}
				}
			});
		}
	}
	
	public void setDataId(String dataId){
		this.dataId = dataId;
		labelWidget.setToolTipText(dataId);
	}

	public void setText(String text){
		labelWidget.setText( text );
	}

	public String getText(){
		return labelWidget.getText();
	}

	public void setFont(Font font){
		labelWidget.setFont( font );
	}

	public void setDataModifier(DataModifier datamodif){
		this.datamodifier = datamodif;
	}
	
	public void setActive(boolean active){
		isActive = active;
	}
	
	public Boolean getActive(){
		return isActive;
	}
	@Override
	public void layout() {
		@SuppressWarnings("unused")
		Object obj = labelWidget.getLayoutData();
		super.layout();
	}


}
