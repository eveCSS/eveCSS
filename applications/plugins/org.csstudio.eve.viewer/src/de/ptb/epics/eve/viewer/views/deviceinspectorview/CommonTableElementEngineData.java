package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import java.util.Locale;

import com.cosylab.util.PrintfFormat;

import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;
import de.ptb.epics.eve.ecp1.intern.DataModifier;
import de.ptb.epics.eve.ecp1.intern.DataType;
import de.ptb.epics.eve.viewer.Activator;

/**
 * <code>CommonTableElementEngineData</code>.
 * 
 * @author Jens Eden
 *
 */
public class CommonTableElementEngineData implements IMeasurementDataListener {

	private String dataId;
	private DataModifier datamodifier;
	private Boolean isActive;
	private String textvalue;
	private CommonTableElement tableElement;
	
	/**
	 * Constructs a <code>CommonTableElementEngineData</code<.
	 * 
	 * @param dataId the id of the data
	 * @param tableElement the 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * 		the data corresponds to
	 */
	public CommonTableElementEngineData(String dataId, CommonTableElement tableElement) {
		this.dataId = dataId;
		this.tableElement = tableElement;
		Activator.getDefault().getEcp1Client().addMeasurementDataListener(this);
		datamodifier = DataModifier.UNMODIFIED;
		isActive = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void measurementDataTransmitted(final MeasurementData measurementData) {

		if (!isActive) return;
		if (measurementData == null) return;
		if (dataId == null) return;
		
		if (this.dataId.equals(measurementData.getName()) && 
			(measurementData.getDataModifier() == datamodifier)) {
			if ((measurementData.getDataType() == DataType.DOUBLE) || 
				(measurementData.getDataType() == DataType.FLOAT)) {
					Double value = (Double) measurementData.getValues().get(0);
					textvalue = new PrintfFormat(
							Locale.ENGLISH, "%12.4g").sprintf(value);
			} else {
				textvalue = measurementData.getValues().get( 0 ).toString();
			}
			tableElement.update();
		}
	}
	
	/**
	 * 
	 * @param dataId
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	/**
	 * 
	 * @return
	 */
	public String getValue() {
		return textvalue;
	}

	/**
	 * 
	 * @param datamodifier
	 */
	public void setDataModifier(DataModifier datamodifier) {
		this.datamodifier = datamodifier;
	}
	
	/**
	 * 
	 * @param active
	 */
	public void setActive(boolean active) {
		isActive = active;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean getActive() {
		return isActive;
	}
}