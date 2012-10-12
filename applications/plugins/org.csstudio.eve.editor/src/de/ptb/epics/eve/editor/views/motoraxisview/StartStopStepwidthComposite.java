package de.ptb.epics.eve.editor.views.motoraxisview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * <code>MotorAxisStartStopStedwidthComposite</code> is a composite to define
 * Start, Stop, Stepwidth and Stepcount of the motor axis. (Shown if Step 
 * Function is either Add or Multiply)
 * 
 * @author Hartmut Scherr
 * @author Marcus Michalsky
 */
public class StartStopStepwidthComposite extends Composite implements
		PropertyChangeListener {
	
	// indicates whether an axis in the scan module containing 
	// the current axis is set as main axis
	private boolean mainAxisSet; // TODO should be in the model !
	

	
	
	/**
	 * Constructs a <code>MotorAxisStartStopStepwidthComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param parentView the view this composite is contained in
	 */
	public StartStopStepwidthComposite(final Composite parent, 
										final int style) {
		
		this.mainAxisSet = false;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.Axis}.
	 *  
	 * @param axis the {@link de.ptb.epics.eve.data.scandescription.Axis} that 
	 * 		  should be set
	 */
	public void setCurrentAxis(final Axis axis) {
		removeListeners();
		
		// update to the new axis
		this.currentAxis = axis;
		
		if(this.currentAxis != null) {

			// set tooltips
			switch(this.currentAxis.getMotorAxis().getPosition().getType()) {
				case DATETIME:
					String tooltip = "The input format is yyyy-MM-dd HH:mm:ss.SSS " +
					"or \nHH:mm:ss.SSS";
					this.startText.setToolTipText(tooltip);
					this.stopText.setToolTipText(tooltip);
					String tooltip2 = "The input format is HH:mm:ss.SSS";
					this.stepwidthText.setToolTipText(tooltip2);
					break;
			} // end of: set tooltips
			

			checkForErrors();
		}
	}

	
	/*
	 *
	 */
	private void checkForErrors() {
		// reset errors
		this.startTextControlDecoration.hide();
		this.stopTextControlDecoration.hide();
		this.stepwidthTextControlDecoration.hide();
		this.stepcountTextControlDecoration.hide();
		
		// check if start/stop are out of bounds, i.e. below LLM / above HLM
		try {
			Double start = Double.parseDouble(this.startText.getText());
			if (start.compareTo(this.currentAxis.getMotorAxis()
					.getChannelAccess().getLowLimit().doubleValue()) <= 0) {
				// start is below LOWLIMIT
				this.startTextControlDecoration.setImage(warnImage);
				this.startTextControlDecoration.setDescriptionText(
						"Value is below the current low limit!");
				this.startTextControlDecoration.show();
			}
			if (start.compareTo(this.currentAxis.getMotorAxis()
					.getChannelAccess().getHighLimit().doubleValue()) >= 0) {
				// start is above HIGHLIMIT
				this.startTextControlDecoration.setImage(warnImage);
				this.startTextControlDecoration.setDescriptionText(
						"Value is above the current high limit!");
				this.startTextControlDecoration.show();
			}
		} catch (NumberFormatException e) {
			logger.debug("bounds check not possible.");
		} catch (NullPointerException e) {
			logger.debug("null, maybe PV wasn't ready / doesn't exist.");
		}
		try {
			Double stop = Double.parseDouble(this.stopText.getText());
			if (stop.compareTo(this.currentAxis.getMotorAxis()
					.getChannelAccess().getLowLimit().doubleValue()) <= 0) {
				// stop is below LOWLIMIT
				this.stopTextControlDecoration.setImage(warnImage);
				this.stopTextControlDecoration.setDescriptionText(
						"Value is below the current low limit!");
				this.stopTextControlDecoration.show();
			}
			if (stop.compareTo(this.currentAxis.getMotorAxis()
					.getChannelAccess().getHighLimit().doubleValue()) >= 0) {
				// stop is above HIGHLIMIT
				this.stopTextControlDecoration.setImage(warnImage);
				this.stopTextControlDecoration.setDescriptionText(
						"Value is above the current high limit!");
				this.stopTextControlDecoration.show();
			}
		} catch (NumberFormatException e) {
			logger.debug("bounds check not possible.");
		} catch (NullPointerException e) {
			logger.debug("null, maybe PV wasn't ready / doesn't exist.");
		}
		// end of: bounds check

	}

	// TODO REMOVE !!!
	private void autoFill() {

		boolean startOk = true;
		boolean stopOk = true;
		boolean stepwidthOk = true;
		boolean stepcountOk = true;

		for (IModelError error : this.currentAxis.getModelErrors()) {
			if (error instanceof AxisError) {
				final AxisError axisError = (AxisError) error;
				switch (axisError.getErrorType()) {
				case START_NOT_SET:
				case START_VALUE_NOT_POSSIBLE:
					startOk = false;
					break;
				case STOP_NOT_SET:
				case STOP_VALUE_NOT_POSSIBLE:
					stopOk = false;
					break;
				case STEPWIDTH_NOT_SET:
					stepwidthOk = false;
					break;
				case STEPCOUNT_NOT_SET:
					stepcountOk = false;
					break;
				}
			}
		}

		if (this.currentAxis != null) {
			if (this.startRadioButton.getSelection()) {
				if (stopOk && stepwidthOk && stepcountOk) {
					// Alle Werte OK, Start-Wert kann berechnet werden
					if (!this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {

						double stop;
						double stepwidth;
						double stepcount;

						switch (this.currentAxis.getMotorAxis().getPosition()
								.getType()) {
						case DATETIME:
							// Korrekturzahlen für die Umrechnung der
							// Millisekunden von der Zahl ohne führende Nullen
							// in die Zahl als sogenannter float Wert
							// Bei der Eingabe von der Zeit in z.B. 00:00:00.5
							// macht die Funktion
							// getTimeInMillis() aus der Zeit 00:00:00.005
							int addStop = 0;
							int addStepwidth = 0;

							int stopJahr = 0; // 1 = Format von Jahr =
												// yyyy-MM-dd HH:mm:ss.SSS
												// 0 = Format von Jahr =
												// HH:mm:ss(.SSS)

							DateFormat stopDate = DateFormat.getTimeInstance();
							DateFormat stepwidthDate = DateFormat
									.getTimeInstance();

							if (this.stopText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								stopJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss");
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss.SSS");
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							}

							if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stepwidthDate = new SimpleDateFormat("HH:mm:ss");
							} else if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stepwidthDate = new SimpleDateFormat(
										"HH:mm:ss.SSS");
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stepwidthText.getText()
										.indexOf('.');
								double nachkomma = Double
										.parseDouble(this.stepwidthText
												.getText().substring(indexP));
								int nachMinus = Integer
										.parseInt(this.stepwidthText.getText()
												.substring(indexP + 1));
								addStepwidth = (int) (nachkomma * 1000 - nachMinus);
							}

							stop = 0;
							stepwidth = 0;

							try {
								stopDate.setLenient(false);
								stopDate.parse(this.stopText.getText());
								Calendar stopTime = stopDate.getCalendar();
								stop = stopTime.getTimeInMillis() + addStop
										+ 3600000;

								stepwidthDate.setLenient(false);
								stepwidthDate.parse(this.stepwidthText
										.getText());
								Calendar stepwidthTime = stepwidthDate
										.getCalendar();
								stepwidth = stepwidthTime.getTimeInMillis()
										+ addStepwidth + 3600000;

							} catch (final ParseException ef) {
								logger.error(ef.getMessage(), ef);
							}
							stepcount = Double.parseDouble(this.stepcountText
									.getText());

							// calculate new value for start
							double start = stop - (stepwidth * stepcount);

							if (stopJahr == 1) {
								Calendar startTime = Calendar.getInstance();
								startTime
										.setTimeInMillis((long) (start - 3600000));

								String startString = String.format(
										"%s-%02d-%02d %02d:%02d:%02d.%03d",
										startTime.get(Calendar.YEAR),
										startTime.get(Calendar.MONTH) + 1,
										startTime.get(Calendar.DATE),
										startTime.get(Calendar.HOUR_OF_DAY),
										startTime.get(Calendar.MINUTE),
										startTime.get(Calendar.SECOND),
										startTime.get(Calendar.MILLISECOND));
								this.startText.setText(startString);
								currentAxis.setStart(this.startText.getText());
							} else if (start < 0) {
								// start value not valid
								this.startText.setText("not calculable");
								currentAxis.setStart(this.startText.getText());
							} else {
								// convert start in calender value
								Calendar startTime = Calendar.getInstance();
								startTime
										.setTimeInMillis((long) (start - 3600000));

								// convert calender Time in an output string
								String startString = String.format(
										"%02d:%02d:%02d.%03d",
										startTime.get(Calendar.HOUR_OF_DAY),
										startTime.get(Calendar.MINUTE),
										startTime.get(Calendar.SECOND),
										startTime.get(Calendar.MILLISECOND));
								this.startText.setText(startString);
								currentAxis.setStart(this.startText.getText());
							}
							break;
						}
					} 
				}
			} else if (this.stopRadioButton.getSelection()) {
				if (startOk && stepwidthOk && stepcountOk) {
					if (!this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {

						double start;
						double stepwidth;
						double stepcount;

						switch (this.currentAxis.getMotorAxis().getPosition()
								.getType()) {
						case DATETIME:
							// Korrekturzahlen für die Umrechnung der
							// Millisekunden von der Zahl ohne führende Nullen
							// in die Zahl als sogenannter float Wert
							// Bei der Eingabe von der Zeit in z.B. 00:00:00.5
							// macht die Funktion
							// getTimeInMillis() aus der Zeit 00:00:00.005
							int addStart = 0;
							int addStepwidth = 0;

							int startJahr = 0; // 1 = Format von Jahr =
												// yyyy-MM-dd HH:mm:ss.SSS
												// 0 = Format von Jahr =
												// HH:mm:ss(.SSS)

							DateFormat startDate = DateFormat.getTimeInstance();
							DateFormat stepwidthDate = DateFormat
									.getTimeInstance();

							// Herausfinden welches Format die übergebene Zeit
							// hat
							if (this.startText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								startJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss");
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss.SSS");
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							}

							if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stepwidthDate = new SimpleDateFormat("HH:mm:ss");
							} else if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stepwidthDate = new SimpleDateFormat(
										"HH:mm:ss.SSS");
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stepwidthText.getText()
										.indexOf('.');
								double nachkomma = Double
										.parseDouble(this.stepwidthText
												.getText().substring(indexP));
								int nachMinus = Integer
										.parseInt(this.stepwidthText.getText()
												.substring(indexP + 1));
								addStepwidth = (int) (nachkomma * 1000 - nachMinus);
							}

							start = 0;
							stepwidth = 0;

							try {
								startDate.setLenient(false);
								startDate.parse(this.startText.getText());
								Calendar startTime = startDate.getCalendar();
								start = startTime.getTimeInMillis() + addStart
										+ 3600000;

								stepwidthDate.setLenient(false);
								stepwidthDate.parse(this.stepwidthText
										.getText());
								Calendar stepwidthTime = stepwidthDate
										.getCalendar();
								stepwidth = stepwidthTime.getTimeInMillis()
										+ addStepwidth + 3600000;

							} catch (final ParseException ef) {
								logger.error(ef.getMessage(), ef);
							}
							stepcount = Double.parseDouble(this.stepcountText
									.getText());

							// calculate new value for stop
							double stop = start + (stepwidth * stepcount);

							if (startJahr == 1) {
								Calendar stopTime = Calendar.getInstance();
								stopTime.setTimeInMillis((long) (stop - 3600000));

								// convert calender Time in an output string
								String stopString = String.format(
										"%s-%02d-%02d %02d:%02d:%02d.%03d",
										stopTime.get(Calendar.YEAR),
										stopTime.get(Calendar.MONTH) + 1,
										stopTime.get(Calendar.DATE),
										stopTime.get(Calendar.HOUR_OF_DAY),
										stopTime.get(Calendar.MINUTE),
										stopTime.get(Calendar.SECOND),
										stopTime.get(Calendar.MILLISECOND));
								this.stopText.setText(stopString);
								currentAxis.setStop(this.stopText.getText());
							} else if (stop >= 86400000) {
								// stop value not valid, more than 24 hours
								this.stopText
										.setText("not calculable, more than 24 hours");
								currentAxis.setStop(this.stopText.getText());
							} else {
								// convert stop in calender value
								Calendar stopTime = Calendar.getInstance();
								stopTime.setTimeInMillis((long) (stop - 3600000));

								// convert calender Time in an output string
								String stopString = String.format(
										"%02d:%02d:%02d.%03d",
										stopTime.get(Calendar.HOUR_OF_DAY),
										stopTime.get(Calendar.MINUTE),
										stopTime.get(Calendar.SECOND),
										stopTime.get(Calendar.MILLISECOND));
								this.stopText.setText(stopString);
								currentAxis.setStop(this.stopText.getText());
							}
							break;
						}
					} else {
						
					}
				}
			} else if (this.stepwidthRadioButton.getSelection()) {
				if (startOk && stopOk && stepcountOk) {
					if (!this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {

						double start;
						double stop;
						double stepcount;

						switch (this.currentAxis.getMotorAxis().getPosition()
								.getType()) {
						case DATETIME:
							// Korrekturzahlen für die Umrechnung der
							// Millisekunden von der Zahl ohne führende Nullen
							// in die Zahl als sogenannter float Wert
							// Bei der Eingabe von der Zeit in z.B. 00:00:00.5
							// macht die Funktion
							// getTimeInMillis() aus der Zeit 00:00:00.005
							int addStart = 0;
							int addStop = 0;

							int startJahr = 0; // 1 = Format von Jahr =
												// yyyy-MM-dd HH:mm:ss.SSS
							int stopJahr = 0; // 0 = Format von Jahr =
												// HH:mm:ss(.SSS)

							DateFormat startDate = DateFormat.getTimeInstance();
							DateFormat stopDate = DateFormat.getTimeInstance();

							// Herausfinden welches Format die übergebene Zeit
							// hat
							if (this.startText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								startJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss");
								startJahr = 0;
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss.SSS");
								startJahr = 0;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							}

							if (this.stopText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								stopJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss");
								stopJahr = 0;
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss.SSS");
								stopJahr = 0;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							}

							start = 0;
							stop = 0;

							// Wenn startJahr != stopJahr dann wird nicht
							// weitergemacht, weil die Formate nicht gleich sind
							if (startJahr != stopJahr)
								return;

							try {
								startDate.setLenient(false);
								startDate.parse(this.startText.getText());
								Calendar startTime = startDate.getCalendar();
								start = startTime.getTimeInMillis() + addStart
										+ 3600000;

								stopDate.setLenient(false);
								stopDate.parse(this.stopText.getText());
								Calendar stopTime = stopDate.getCalendar();
								stop = stopTime.getTimeInMillis() + addStop
										+ 3600000;
							} catch (final ParseException ef) {
								logger.error(ef.getMessage(), ef);
							}

							stepcount = Double.parseDouble(this.stepcountText
									.getText());

							// calculate new value for stop
							if ((stop - start == 0) || (stepcount == 0)) {
								this.stepwidthText.setText("0");
								currentAxis.setStepwidth(this.stepwidthText
										.getText());
							} else {
								double stepwidth = (stop - start) / stepcount;
								// convert stop in calender value
								Calendar stepwidthTime = Calendar.getInstance();
								stepwidthTime
										.setTimeInMillis((long) (stepwidth - 3600000));

								// convert calender Time in an output string
								String stepwidthString = String
										.format("%02d:%02d:%02d.%03d",
												stepwidthTime
														.get(Calendar.HOUR_OF_DAY),
												stepwidthTime
														.get(Calendar.MINUTE),
												stepwidthTime
														.get(Calendar.SECOND),
												stepwidthTime
														.get(Calendar.MILLISECOND));
								this.stepwidthText.setText(stepwidthString);
								currentAxis.setStepwidth(this.stepwidthText
										.getText());
							}
							break;
						}
					} else {
						
					}
				}
			} else if (this.stepcountRadioButton.getSelection()) {
				if (startOk && stopOk && stepwidthOk) {
					if (!this.currentAxis.getMotorAxis().getGoto().isDiscrete()) {

						double start;
						double stop;
						double stepwidth;

						// Korrekturzahlen für die Umrechnung der Millisekunden
						// von der Zahl ohne führende Nullen
						// in die Zahl als sogenannter float Wert
						// Bei der Eingabe von der Zeit in z.B. 00:00:00.5 macht
						// die Funktion
						// getTimeInMillis() aus der Zeit 00:00:00.005
						int addStart = 0;
						int addStop = 0;
						int addStepwidth = 0;

						int startJahr = 0; // 1 = Format von Jahr = yyyy-MM-dd
											// HH:mm:ss.SSS
						int stopJahr = 0; // 0 = Format von Jahr =
											// HH:mm:ss(.SSS)

						switch (this.currentAxis.getMotorAxis().getPosition()
								.getType()) {
						case DATETIME:
							DateFormat startDate = DateFormat.getTimeInstance();
							DateFormat stopDate = DateFormat.getTimeInstance();
							DateFormat stepwidthDate = DateFormat
									.getTimeInstance();
							// Herausfinden welches Format die übergebene Zeit
							// hat
							if (this.startText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								startJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss");
								startJahr = 0;
							} else if (this.startText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								startDate = new SimpleDateFormat("HH:mm:ss.SSS");
								startJahr = 0;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.startText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.startText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.startText
										.getText().substring(indexP + 1));
								addStart = (int) (nachkomma * 1000 - nachMinus);
							}

							if (this.stopText
									.getText()
									.matches(
											"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat(
										"yyyy-MM-dd HH:mm:ss.SSS");
								stopJahr = 1;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss");
								stopJahr = 0;
							} else if (this.stopText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stopDate = new SimpleDateFormat("HH:mm:ss.SSS");
								stopJahr = 0;
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stopText.getText().indexOf(
										'.');
								double nachkomma = Double
										.parseDouble(this.stopText.getText()
												.substring(indexP));
								int nachMinus = Integer.parseInt(this.stopText
										.getText().substring(indexP + 1));
								addStop = (int) (nachkomma * 1000 - nachMinus);
							}

							if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+?$")) {
								stepwidthDate = new SimpleDateFormat("HH:mm:ss");
							} else if (this.stepwidthText.getText().matches(
									"\\d+:\\d+:\\d+([.]\\d{1,3})?$")) {
								stepwidthDate = new SimpleDateFormat(
										"HH:mm:ss.SSS");
								// Nachkommazahl bestimmen
								// Stelle des Punktes
								int indexP = this.stepwidthText.getText()
										.indexOf('.');
								double nachkomma = Double
										.parseDouble(this.stepwidthText
												.getText().substring(indexP));
								int nachMinus = Integer
										.parseInt(this.stepwidthText.getText()
												.substring(indexP + 1));
								addStepwidth = (int) (nachkomma * 1000 - nachMinus);
							}

							start = 0;
							stop = 0;
							stepwidth = 0;

							// Wenn startJahr != stopJahr dann wird nicht
							// weitergemacht, weil die Formate nicht gleich sind
							if (startJahr != stopJahr)
								return;

							try {
								startDate.setLenient(false);
								startDate.parse(this.startText.getText());
								Calendar startTime = startDate.getCalendar();
								start = startTime.getTimeInMillis() + addStart
										+ 3600000;

								stopDate.setLenient(false);
								stopDate.parse(this.stopText.getText());
								Calendar stopTime = stopDate.getCalendar();
								stop = stopTime.getTimeInMillis() + addStop
										+ 3600000;

								stepwidthDate.setLenient(false);
								stepwidthDate.parse(this.stepwidthText
										.getText());
								Calendar stepwidthTime = stepwidthDate
										.getCalendar();
								stepwidth = stepwidthTime.getTimeInMillis()
										+ addStepwidth + 3600000;

								// Wenn Start > Stop beenden
								if (start > stop)
									return;
							} catch (final ParseException ef) {
								logger.error(ef.getMessage(), ef);
							}

							break;
						default:
							start = Double
									.parseDouble(this.startText.getText());
							stop = Double.parseDouble(this.stopText.getText());
							stepwidth = Double.parseDouble(this.stepwidthText
									.getText());
							break;
						}

						if ((start - stop) > 0) {
							// stepwidth muß negativ sein!
							if (stepwidth > 0) {
								// Vorzeichen von Stepwidth umdrehen!
								switch (this.currentAxis.getMotorAxis()
										.getPosition().getType()) {
								case INT:
									int stepwidthInt = (int) (stepwidth * -1);
									this.stepwidthText.setText(""
											+ stepwidthInt);
									currentAxis.setStepwidth(this.stepwidthText
											.getText());
									break;
								default:
									stepwidth = stepwidth * -1;
									this.stepwidthText.setText("" + stepwidth);
									currentAxis.setStepwidth(this.stepwidthText
											.getText());
									break;
								}
							}
						}
						if ((start - stop) < 0) {
							switch (this.currentAxis.getMotorAxis()
									.getPosition().getType()) {
							case DATETIME:
								// stop time less than start time is not
								// possible
								break;
							case INT:
								if (stepwidth < 0) {
									int stepwidthInt = (int) (stepwidth * -1);
									this.stepwidthText.setText(""
											+ stepwidthInt);
									currentAxis.setStepwidth(this.stepwidthText
											.getText());
								}
								break;
							default:
								// stepwidth muß positiv sein!
								if (stepwidth < 0) {
									// Vorzeichen von Stepwidth umdrehen!
									stepwidth = stepwidth * -1;
									this.stepwidthText.setText("" + stepwidth);
									currentAxis.setStepwidth(this.stepwidthText
											.getText());
								}
								break;
							}
						}

						if (!this.stepcountText.getText().equals("")) {
							// stepcount Eintrag schon vorhanden
							final double stepcount = Double
									.parseDouble(this.stepcountText.getText());
							// Wenn Zähler oder Nenner gleich 0, besondere
							// Behandlung
							if ((stop - start == 0) || (stepwidth == 0)) {
								if (stepcount == 0) {
									// Wert wird nicht nochmal gesetzt
								} else {
									this.stepcountText.setText("0");
									currentAxis.setStepCount(0);
								}
							} else if (stepcount == ((stop - start) / stepwidth)) {
								// Wert wird nicht nochmal gesetzt
							} else {
								this.stepcountText.setText(""
										+ ((stop - start) / stepwidth));
								currentAxis.setStepCount(Double
										.parseDouble(this.stepcountText
												.getText()));
							}
						} else {
							this.stepcountText.setText(""
									+ ((stop - start) / stepwidth));
							currentAxis.setStepCount(Double
									.parseDouble(this.stepcountText.getText()));
						}
					} else {
						List<String> values = this.currentAxis.getMotorAxis()
								.getGoto().getDiscreteValues();

						final int start = values.indexOf(this.startText
								.getText());
						final int stop = values
								.indexOf(this.stopText.getText());
						int stepwidth;
						try {
							stepwidth = Integer.parseInt(this.stepwidthText
									.getText());
						} catch (NumberFormatException e) {
							stepwidth = 0;
						}

						if ((start - stop) > 0) {
							// stepwidth muß negativ sein!
							if (stepwidth < 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText
										.setText("" + (int) stepwidth);
								this.stepwidthText.setSelection(2);
								currentAxis.setStepwidth(this.stepwidthText
										.getText());
							}
						}
						if ((start - stop) < 0) {
							// stepwidth muß positiv sein!
							if (stepwidth < 0) {
								// Vorzeichen von Stepwidth umdrehen!
								stepwidth = stepwidth * -1;
								this.stepwidthText
										.setText("" + (int) stepwidth);
								this.stepwidthText.setSelection(1);
								currentAxis.setStepwidth(this.stepwidthText
										.getText());
							}
						}

						if (stepwidth != 0) {
							if (!this.stepcountText.getText().equals("")) {
								// stepcount Eintrag schon vorhanden
								final double stepcount_d = Double
										.parseDouble(this.stepcountText
												.getText());
								final int stepcount = (int) stepcount_d;
								if (stepcount == ((stop - start) / stepwidth)) {
									// Wert wird nicht nochmal gesetzt
								} else {
									this.stepcountText.setText(""
											+ ((stop - start) / stepwidth));
									currentAxis.setStepCount(Double
											.parseDouble(this.stepcountText
													.getText()));
								}
							} else {
								this.stepcountText.setText(""
										+ ((stop - start) / stepwidth));
								currentAxis.setStepCount(Double
										.parseDouble(this.stepcountText
												.getText()));
							}
						}
					}
				}
			}
		}
	}

	/*
	 * If stepcount of main axis changes, stepwidth of all other axis
	 * recalculated.
	 */
	private void recalculateStepwidth() {
		if (currentAxis.isMainAxis()) {
			ScanModule scanModul = currentAxis.getScanModule();
			Axis[] axis = scanModul.getAxes();

			for (int i = 0; i < axis.length; ++i) {
				if (!axis[i].isMainAxis()) {
					// Axis ist keine mainAxis und wird neu berechnet
					if (!axis[i].getMotorAxis().getGoto().isDiscrete()) {
						// Achse i ist eine normale Achse
						final double start = Double.parseDouble(axis[i]
								.getStart());
						final double stop = Double.parseDouble(axis[i]
								.getStop());
						final double stepwidth = Double.parseDouble(axis[i]
								.getStepwidth());
						final double stepcount = Double
								.parseDouble(stepcountText.getText());

						// Wenn Zähler oder Nenner gleich 0, besondere
						// Behandlung
						if ((stop - start == 0) || (stepcount == 0)) {
							if (stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							} else {
								axis[i].setStepwidth("0");
							}
						} else if (stepwidth == ((stop - start) / stepcount)) {
							// Wert wird nicht nochmal gesetzt
						} else {
							axis[i].setStepwidth(""
									+ ((stop - start) / stepcount));
						}
					} else {
						// Achse i ist eine diskrete Achse
						int start = 0;
						int stop = 0;

						String[] werte = axis[i].getMotorAxis().getGoto()
								.getDiscreteValues().toArray(new String[0]);
						// Schleife über Werte durchlaufen lassen
						for (int j = 0; j < werte.length; ++j) {
							if (werte[j].equals(axis[i].getStart())) {
								start = j;
							}
							if (werte[j].equals(axis[i].getStop())) {
								stop = j;
							}
						}
						final double stepwidth_d = Double.parseDouble(axis[i]
								.getStepwidth());
						final int stepwidth = (int) stepwidth_d;
						final double stepcount_d = Double
								.parseDouble(stepcountText.getText());
						final int stepcount = (int) stepcount_d;

						// Wenn Zähler oder Nenner gleich 0, besondere
						// Behandlung
						if ((stop - start == 0) || (stepcount == 0)) {
							if (stepwidth == 0) {
								// Wert wird nicht nochmal gesetzt
							} else {
								axis[i].setStepwidth("0");
							}
						} else if (stepwidth == ((stop - start) / stepcount)) {
							// Wert wird nicht nochmal gesetzt
						} else {
							axis[i].setStepwidth(""
									+ ((stop - start) / stepcount));
						}
					}
				}
			}
		}
	}

	
	/* ********************************************************************* */
	/* **************************** Listeners ****************************** */
	/* ********************************************************************* */

	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>stopText</code>.
	 */
	private class StopTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			removeListeners();
			if (currentAxis != null) {
				switch (currentAxis.getMotorAxis().getPosition().getType()) {
				case DATETIME:
					if (currentAxis.getMotorAxis().isValuePossible(
							stopText.getText())) {
						currentAxis.setStop(stopText.getText());
						autoFill();
					} else {
						currentAxis.setStop(null);
					}
					break;
				default:
					// if string is a double set value in model
					// else set null in model
					try {
						Double.parseDouble(stopText.getText());
						currentAxis.setStop(stopText.getText());
						autoFill();
					} catch (final NumberFormatException ex) {
						// string is not a double
						currentAxis.setStop(null);
					}
				}
				checkForErrors();
				addListeners();
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.ModifyListener} of 
	 * <code>stepwidthText</code>.
	 */
	private class StepwidthTextModifyListener implements ModifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modifyText(final ModifyEvent e) {
			removeListeners();

			// if Text is empty, do nothing
			if (currentAxis != null) {
				if (currentAxis.getMotorAxis().getGoto().isDiscrete()) {
					try {
						Integer.parseInt(stepwidthText.getText());
						currentAxis.setStepwidth(stepwidthText.getText());
						autoFill();
					} catch (final NumberFormatException ex) {
						// string is not an integer
						currentAxis.setStepwidth(null);
					}
				} else {
					switch (currentAxis.getMotorAxis().getPosition().getType()) {
					case DATETIME:
						if (currentAxis.getMotorAxis().isValuePossible(
								stepwidthText.getText())) {
							currentAxis.setStepwidth(stepwidthText.getText());
							autoFill();
						} else {
							currentAxis.setStepwidth(null);
						}
						break;
					default:
						try {
							double start = Double.parseDouble(startText
									.getText());
							double stop = Double
									.parseDouble(stopText.getText());
							double stepwidth = Double.parseDouble(stepwidthText
									.getText());

							if (start > stop) {
								if (stepwidth > 0) {
									// Vorzeichen von Stepwidth umdrehen!
									stepwidth = stepwidth * -1;
									stepwidthText.setText("" + (int) stepwidth);
									stepwidthText.setSelection(2);
								}

								if (stepwidthText.getText().equals("0")) {
									// Vorzeichen umdrehen
									stepwidthText.setText("-0");
									stepwidthText.setSelection(2);
								}
							}

							if (start < stop) {
								if (stepwidth < 0) {
									// Vorzeichen von Stepwidth umdrehen!
									stepwidth = stepwidth * -1;
									stepwidthText.setText("" + (int) stepwidth);
									stepwidthText.setSelection(1);
								}
							}

							Double.parseDouble(stepwidthText.getText());
							currentAxis.setStepwidth(stepwidthText.getText());
							autoFill();
						} catch (final NumberFormatException ex) {
							// string is not a double
							currentAxis.setStepwidth(null);
						}
					}
				}
			}
			checkForErrors();
			addListeners();
		}
	}



	/**
	 * {@link org.eclipse.swt.events.VerifyListener} of text widgets.
	 */
	private class TextVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
			case 0:
				return;
			}

			String oldText = ((Text) (e.widget)).getText();

			switch (currentAxis.getMotorAxis().getPosition().getType()) {
			case DATETIME:
				if (!Character.isDigit(e.character)) {
					if (e.character == '.') {
						// charecter . is a valid character, if he is not in the
						// old string
						if (oldText.contains("."))
							e.doit = false;
					} else if (e.character == ':') {
						// character : is a valid characterm for the timer
						return;
					} else if (e.character == '-') {
						// character - is a valid characterm for the timer, if
						// he is not in the old string
						return;
					} else if (e.character == ' ') {
						// charecter ' ' is a valid character, if he is not in
						// the old string
						if (oldText.contains(" "))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			case INT:
				if (!Character.isDigit(e.character)) {
					if (e.character == '-') {
						// character - is a valid character as first sign and
						// after an e
						if (oldText.isEmpty()) {
							// oldText is emtpy, - is valid
						} else if ((((CCombo) e.widget).getSelection().x) == 0) {
							// - is the first sign an valid
						} else {
							// wenn das letzte Zeichen von oldText ein e ist,
							// ist das minus auch erlaubt
							int index = oldText.length();
							if (oldText.substring(index - 1).equals("e")) {
								// letzte Zeichen ist ein e und damit erlaubt
							} else
								e.doit = false;
						}
					} else if (e.character == 'e') {
						// character e is a valid character, if he is not in the
						// old string
						if (oldText.contains("e"))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			default:
				if (!Character.isDigit(e.character)) {
					if (e.character == '.') {
						// charecter . is a valid character, if he is not in the
						// old string
						if (oldText.contains("."))
							e.doit = false;
					} else if (e.character == '-') {
						// character - is a valid character as first sign and
						// after an e
						if (oldText.isEmpty()) {
							// oldText is emtpy, - is valid
						} else if ((((CCombo) e.widget).getSelection().x) == 0) {
							// - is the first sign an valid
						} else {
							// wenn das letzte Zeichen von oldText ein e ist,
							// ist das minus auch erlaubt
							int index = oldText.length();
							if (oldText.substring(index - 1).equals("e")) {
								// letzte Zeichen ist ein e und damit erlaubt
							} else
								e.doit = false;
						}
					} else if (e.character == 'e') {
						// character e is a valid character, if he is not in the
						// old string
						if (oldText.contains("e"))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.VerifyListener} of text widgets.
	 */
	private class TextDoubleVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
				return;
			}

			String oldText = ((Text) (e.widget)).getText();

			switch (currentAxis.getMotorAxis().getPosition().getType()) {
			case DATETIME:
				if (!Character.isDigit(e.character)) {
					if (e.character == '.') {
						// charecter . is a valid character, if he is not in the
						// old string
						if (oldText.contains("."))
							e.doit = false;
					} else if (e.character == ':') {
						// character : is a valid characterm for the timer
						return;
					} else if (e.character == '-') {
						// character - is a valid characterm for the timer, if
						// he is not in the old string
						return;
					} else if (e.character == ' ') {
						// charecter ' ' is a valid character, if he is not in
						// the old string
						if (oldText.contains(" "))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			case INT:
				if (!Character.isDigit(e.character)) {
					if (e.character == '-') {
						// character - is a valid character as first sign and
						// after an e
						if (oldText.isEmpty()) {
							// oldText is emtpy, - is valid
						} else if ((((Text) e.widget).getSelection().x) == 0) {
							// - is the first sign an valid
						} else {
							// wenn das letzte Zeichen von oldText ein e ist,
							// ist das minus auch erlaubt
							int index = oldText.length();
							if (oldText.substring(index - 1).equals("e")) {
								// letzte Zeichen ist ein e und damit erlaubt
							} else
								e.doit = false;
						}
					} else if (e.character == 'e') {
						// character e is a valid character, if he is not in the
						// old string
						if (oldText.contains("e"))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			default:
				if (!Character.isDigit(e.character)) {
					if (e.character == '.') {
						// charecter . is a valid character, if he is not in the
						// old string
						if (oldText.contains("."))
							e.doit = false;
					} else if (e.character == '-') {
						// character - is a valid character as first sign and
						// after an e
						if (oldText.isEmpty()) {
							// oldText is emtpy, - is valid
						} else if ((((Text) e.widget).getSelection().x) == 0) {
							// - is the first sign an valid
						} else {
							// wenn das letzte Zeichen von oldText ein e ist,
							// ist das minus auch erlaubt
							int index = oldText.length();
							if (oldText.substring(index - 1).equals("e")) {
								// letzte Zeichen ist ein e und damit erlaubt
							} else
								e.doit = false;
						}
					} else if (e.character == 'e') {
						// character e is a valid character, if he is not in the
						// old string
						if (oldText.contains("e"))
							e.doit = false;
					} else {
						e.doit = false; // disallow the action
					}
				}
				break;
			}
		}
	}

	/**
	 * {@link org.eclipse.swt.events.VerifyListener} of Text Widget.
	 */
	private class TextNumberVerifyListener implements VerifyListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void verifyText(VerifyEvent e) {

			switch (e.keyCode) {
			case SWT.BS: // Backspace
			case SWT.DEL: // Delete
			case SWT.HOME: // Home
			case SWT.END: // End
			case SWT.ARROW_LEFT: // Left arrow
			case SWT.ARROW_RIGHT: // Right arrow
				return;
			}
			String oldText = ((Text) (e.widget)).getText();

			if (!Character.isDigit(e.character)) {
				if (e.character == '.') {
					// charecter . is a valid character, if he is not in the old
					// string
					if (oldText.contains("."))
						e.doit = false;
				} else {
					e.doit = false;
				}
			}
		}
	}
}