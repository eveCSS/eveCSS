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
public class CommonTableElementEngineData implements IMeasurementDataListener {

	private String dataId;
	private DataModifier datamodifier;
	private Boolean isActive;
	private String textvalue;
	private CommonTableElement tableElement;
	/**
	 * @param parent
	 * @param style
	 */
	public CommonTableElementEngineData(String dataId, CommonTableElement tableElement) {
		this.dataId = dataId;
		this.tableElement = tableElement;
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
		
		if (this.dataId.equals(measurementData.getName()) && (measurementData.getDataModifier() == datamodifier)) {
			if ((measurementData.getDataType() == DataType.DOUBLE) || (measurementData.getDataType() == DataType.FLOAT)) {
				Double value = (Double) measurementData.getValues().get(0);
				textvalue = new PrintfFormat(Locale.ENGLISH, "%12.4g").sprintf(value);
			}
			else {
				textvalue = measurementData.getValues().get( 0 ).toString();
			}
			tableElement.update();
		}
	}
	
	public void setDataId(String dataId){
		this.dataId = dataId;
	}

	public String getValue() {
		return textvalue;
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
}
