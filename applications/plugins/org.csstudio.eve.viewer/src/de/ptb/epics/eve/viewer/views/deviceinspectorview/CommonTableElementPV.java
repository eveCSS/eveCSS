package de.ptb.epics.eve.viewer.views.deviceinspectorview;

import static org.csstudio.utility.pvmanager.ui.SWTUtil.swtThread;
import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.util.TimeDuration.ms;

import java.util.List;

import org.apache.log4j.Logger;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.Alarm;
import org.epics.pvmanager.data.AlarmSeverity;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.VEnum;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.data.ValueUtil;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;

/**
 * <code>CommonTableElementPV</code> wraps a {@link } 
 * (process variable) and corresponds to a 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement} 
 * (i.e. the row it is contained in).
 * 
 * @author Marcus Michalsky
 */
public class CommonTableElementPV {

	private static Logger logger = 
			Logger.getLogger(CommonTableElementPV.class.getName());
	
	private PV<Object,Object> pv;
	private ReadListener readListener;
	private CommonTableElement commonTableElement;
	
	private ValueFormat valueFormat;
	
	private String pvValue;
	private AlarmSeverity pvStatus;
	
	private boolean isConnected;
	
	private boolean isDiscrete = false;
	private List<String> discreteValues;

	private int pvUpdateInterval;
	
	/**
	 * Constructs a <code>CommonTableElementPV</code>.
	 * 
	 * @param pvname the name of the process variable
	 * @param tableElement the 
	 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement}
	 * 		(row) the process variable corresponds to
	 */
	public CommonTableElementPV(String pvname, CommonTableElement tableElement) {
		
		this.pvValue = "";
		
		pvUpdateInterval = Activator.getDefault().getPreferenceStore().
				getInt(PreferenceConstants.P_PV_UPDATE_INTERVAL);
		
		this.pv = PVManager.readAndWrite(channel(pvname)).notifyOn(swtThread()).
					asynchWriteAndReadEvery(ms(pvUpdateInterval));
		this.readListener = new ReadListener();
		this.pv.addPVReaderListener(readListener);
		
		this.valueFormat = new SimpleValueFormat(1);
		
		this.isConnected = false;
		
		this.pvStatus = AlarmSeverity.UNDEFINED;
		
		this.commonTableElement = tableElement;
	}
	
	public AlarmSeverity getStatus() {
		return this.pvStatus;
	}
	
	public String getValue() {
		return this.pvValue;
	}
	
	public void setValue(String newValue) {
		pv.write(newValue);
	}
	
	public boolean isConnected() {
		return this.isConnected;
	}
	
	public boolean isDiscrete() {
		return this.isDiscrete;
	}
	
	public String[] getDiscreteValues() {
		return this.discreteValues.toArray(new String[0]);
	}
	
	public void disconnect() {
		this.pv.close();
	}
	
	public boolean isReadOnly() {
		return false;
	}
	
	public void setReadOnly(boolean foo) {
		// no way
	}
	
	
	public void setDiscreteValues(String[] bar) {
		// i don't think so
	}
	
	/* ******************************************************************** */
	
	/**
	 * 
	 * @author Marcus Michalsky
	 *
	 */
	private class ReadListener implements PVReaderListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void pvChanged() {
			isConnected = !pv.isClosed();
			pvValue = valueFormat.format(pv.getValue());
			pvStatus = ((Alarm)ValueUtil.alarmOf(pv.getValue())).getAlarmSeverity();
			
			commonTableElement.update();
			
			Exception e = pv.lastException();
			if(e != null) {
				logger.warn(e.getMessage(), e);
			}
			if(logger.isDebugEnabled()) {
				logger.debug("new value for '" + pv.getName() + "' : " + 
							valueFormat.format(pv.getValue()) + 
							" (" + ValueUtil.timeOf(
							pv.getValue()).getTimeStamp().asDate().toString() 
							+ ")");
			}
			
			if(pv.getValue() instanceof VEnum) {
				discreteValues = ((VEnum)pv.getValue()).getLabels();
				isDiscrete = true;
			}
		}
	}
}