package de.ptb.epics.eve.viewer.views.engineview.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.viewer.Activator;

/**
 * @since 1.26
 */
public class StatusTableSelectionListener extends SelectionAdapter implements 
		IEngineStatusListener {
	private TableViewer viewer;
	private Shell shellTable[] = new Shell[10];
	
	public StatusTableSelectionListener(TableViewer viewer) {
		this.viewer = viewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		// für die selektierte Zeile wird ein Info-Fenster angezeigt
		// mit den Details der Chain oder des ScanModuls
		int selection = viewer.getTable().getSelectionIndex();

		// nachsehen ob es für diese selection schon eine shellTable gibt
		if (selection >= shellTable.length) {
			// shellTable muss vergrößert werden (auf 5 mehr als selection)
			Shell tempShell[] = new Shell[selection + 5];
			System.arraycopy(shellTable, 0, tempShell, 0, shellTable.length);
			shellTable = tempShell;
		}

		// Überprüfen, ob Info schon da ist oder nicht.
		// Wenn ja, Info wieder wegnehmen.
		if (shellTable[selection] != null) {
			// Info ist vorhanden, da shellTable gesetzt

			// Wenn Info-Fenster wirklich nicht gelöscht wurde, wird es jetzt
			// gelöscht, ansonsten wird das Info-Fenster geöffnet
			if (!shellTable[selection].isDisposed()) {
				shellTable[selection].dispose();
				shellTable[selection] = null;
				return;
			}
		}

		final TableItem[] rows = viewer.getTable().getItems();

		int aktChain = Integer.parseInt(rows[selection].getText(0).trim());
		int aktSM;
		if (rows[selection].getText(1).trim().equals("")) {
			aktSM = 0;
		} else {
			aktSM = Integer.parseInt(rows[selection].getText(1).trim());
		}

		Rectangle workbenchBounds = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getShell().getBounds();
		
		Chain displayChain = Activator.getDefault().getCurrentScanDescription().getChain(aktChain);
		
		if (aktSM > 0) {
			// ScanModule Zeile wurde ausgewählt, ScanModule Infos anzeigen

			Display display = Activator.getDefault().getWorkbench().getDisplay();
			Shell chainShell = new Shell(display);
			chainShell.setSize(800, 600);
			chainShell.setLocation(workbenchBounds.x + workbenchBounds.width/2 - 400, 
					workbenchBounds.y + workbenchBounds.height/2 - 300);
			chainShell.setText("Scan Module Info");

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 6;
			GridData gridData;

			chainShell.setLayout(gridLayout);

			// 1. Zeile wird gefüllt
			Label chainLabel = new Label(chainShell, SWT.NONE);
			chainLabel.setText("Chain ID:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			chainLabel.setLayoutData(gridData);
			Label chainText = new Label(chainShell, SWT.NONE);
			chainText.setText(rows[selection].getText(0));
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			chainText.setLayoutData(gridData);

			Label numOfMeasurementsLabel = new Label(chainShell, SWT.NONE);
			numOfMeasurementsLabel.setText("No of Measurements:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			numOfMeasurementsLabel.setLayoutData(gridData);
			Label numOfMeasurementsText = new Label(chainShell, SWT.NONE);
			numOfMeasurementsText.setText("" + displayChain.getScanModuleById(aktSM).getValueCount());
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			numOfMeasurementsText.setLayoutData(gridData);

			// 2. Zeile wird gefüllt
			Label smLabel = new Label(chainShell, SWT.NONE);
			smLabel.setText("Scan Module ID:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			smLabel.setLayoutData(gridData);
			Label smText = new Label(chainShell, SWT.NONE);
			smText.setText(rows[selection].getText(1));
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			smText.setLayoutData(gridData);

			Label trigDelLabel = new Label(chainShell, SWT.NONE);
			trigDelLabel.setText("Trigger Delay:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			trigDelLabel.setLayoutData(gridData);
			Label trigDelText = new Label(chainShell, SWT.NONE);
			trigDelText.setText("" + displayChain.getScanModuleById(aktSM).getTriggerDelay());
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			trigDelText.setLayoutData(gridData);

			Label confLabelMot = new Label(chainShell, SWT.NONE);
			confLabelMot.setText("Manual Trigger Motors:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confLabelMot.setLayoutData(gridData);
			Label confTextMot = new Label(chainShell, SWT.NONE);
			if (displayChain.getScanModuleById(aktSM).isTriggerConfirmAxis()) {
				confTextMot.setText(" YES ");
			} else {
				confTextMot.setText(" NO ");
			}
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confTextMot.setLayoutData(gridData);

			// 3. Zeile wird gefüllt
			Label smName = new Label(chainShell, SWT.NONE);
			smName.setText("Scan Module Name:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			smName.setLayoutData(gridData);
			Label smNameText = new Label(chainShell, SWT.NONE);
			smNameText.setText(rows[selection].getText(2));
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			smName.setLayoutData(gridData);

			Label settleLabel = new Label(chainShell, SWT.NONE);
			settleLabel.setText("Settle Time:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			settleLabel.setLayoutData(gridData);
			Label settleText = new Label(chainShell, SWT.NONE);
			settleText.setText("" + displayChain.getScanModuleById(aktSM).getSettleTime());
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			settleText.setLayoutData(gridData);

			Label confLabelDet = new Label(chainShell, SWT.NONE);
			confLabelDet.setText("Manual Trigger Detectors:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confLabelDet.setLayoutData(gridData);
			Label confTextDet = new Label(chainShell, SWT.NONE);
			if (displayChain.getScanModuleById(aktSM).isTriggerConfirmChannel()) {
				confTextDet.setText(" YES ");
			} else {
				confTextDet.setText(" NO ");
			}
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confTextDet.setLayoutData(gridData);

			SashForm sashForm = new SashForm(chainShell, SWT.VERTICAL);
			sashForm.SASH_WIDTH = 4;
			gridData = new GridData();
			gridData.horizontalSpan = 6;
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;
			sashForm.setLayoutData(gridData);

			// Tabelle für die Motor Axes erzeugen
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;

			Table motTable = new Table(sashForm, SWT.NONE);
			motTable.setHeaderVisible(true);
			motTable.setLinesVisible(true);
			motTable.setLayoutData(gridData);
			TableColumn motColumn = new TableColumn(motTable, SWT.NONE);
			motColumn.setWidth(130);
			motColumn.setText("Motor Axis");
			TableColumn motColumn1 = new TableColumn(motTable, SWT.NONE);
			motColumn1.setWidth(110);
			motColumn1.setText("Stepfunction");
			TableColumn motColumn2 = new TableColumn(motTable, SWT.NONE);
			motColumn2.setWidth(500);
			motColumn2.setText("Positions");

			Axis[] axis = displayChain.getScanModuleById(aktSM).getAxes();
			for (int i = 0; i < axis.length; i++) {
				// Neuer Tabelleneintrag muß gemacht werden
				switch (axis[i].getStepfunction()) {
				case ADD:
				case MULTIPLY:
					TableItem tableItem = new TableItem(motTable, 0);
					tableItem.setText(0, axis[i].getAbstractDevice().getName());
					tableItem.setText(1, axis[i].getStepfunction().toString());
					tableItem.setText(2, axis[i].getStart().toString()
							+ " \u2192 " 
							+ axis[i].getStop().toString()
							+ ", "
							+ axis[i].getStepwidth().toString());
					break;
				case FILE:
					TableItem tableItemFile = new TableItem(motTable, 0);
					tableItemFile.setText(0, axis[i].getAbstractDevice().getName());
					tableItemFile.setText(1, axis[i].getStepfunction().toString());
					tableItemFile.setText(2, axis[i].getFile().getAbsolutePath());
					break;
				case PLUGIN:
					TableItem tableItemPlug = new TableItem(motTable, 0);
					tableItemPlug.setText(0, axis[i].getAbstractDevice().getName());
					tableItemPlug.setText(1, axis[i].getStepfunction().toString());
					tableItemPlug.setText(2, axis[i].getPluginController().getPlugin().getName());
					break;
				case POSITIONLIST:
					TableItem tableItemPos = new TableItem(motTable, 0);
					tableItemPos.setText(0, axis[i].getAbstractDevice().getName());
					tableItemPos.setText(1, axis[i].getStepfunction().toString());
					tableItemPos.setText(2, axis[i].getPositionlist());
					break;
				default:
					break;
				}
			}

			// Tabelle für die Detector Channels erzeugen
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			gridData.verticalAlignment = GridData.FILL;
			gridData.horizontalSpan = 2;
			gridData.grabExcessHorizontalSpace = true;
			gridData.grabExcessVerticalSpace = true;

			Table detTable = new Table(sashForm, SWT.NONE);
			detTable.setHeaderVisible(true);
			detTable.setLinesVisible(true);
			detTable.setLayoutData(gridData);
			TableColumn detColumn = new TableColumn(detTable, SWT.NONE);
			detColumn.setWidth(140);
			detColumn.setText("Detector Channel");
			TableColumn detColumn1 = new TableColumn(detTable, SWT.NONE);
			detColumn1.setWidth(70);
			detColumn1.setText("Average");
			TableColumn detColumn2 = new TableColumn(detTable, SWT.NONE);
			detColumn2.setWidth(75);
			detColumn2.setText("Deferred");
			TableColumn detColumn3 = new TableColumn(detTable, SWT.NONE);
			detColumn3.setWidth(85);
			detColumn3.setText("Max. Dev.");
			TableColumn detColumn4 = new TableColumn(detTable, SWT.NONE);
			detColumn4.setWidth(78);
			detColumn4.setText("Minimum");
			TableColumn detColumn5 = new TableColumn(detTable, SWT.NONE);
			detColumn5.setWidth(120);
			detColumn5.setText("Max. Attempts");
			TableColumn detColumn6 = new TableColumn(detTable, SWT.NONE);
			detColumn6.setWidth(100);
			detColumn6.setText("Norm. Channel");

			String dash = Character.toString('\u2014');
			
			Channel[] channels = displayChain.getScanModuleById(aktSM).getChannels();
			for (int i = 0; i < channels.length; i++) {
				if (!channels[i].getChannelMode().equals(ChannelModes.STANDARD)) {
					continue;
				}
				// Neuer Tabelleneintrag muß gemacht werden
				TableItem tableItem = new TableItem(detTable, 0);
				tableItem.setText(0, channels[i].getAbstractDevice().getName());
				tableItem.setText(1, Integer.toString(channels[i].getAverageCount()));
				tableItem.setText(2, Boolean.toString(channels[i].isDeferred()));
				Double d = channels[i].getMaxDeviation();
				if (d != null) {
					tableItem.setText(3, Double.toString(d));
				} else {
					tableItem.setText(3, dash);
				}
				d = channels[i].getMinimum();
				if (d != null) {
					tableItem.setText(4, Double.toString(d));
				} else {
					tableItem.setText(4, dash);
				}
				if (channels[i].getMaxAttempts() != null) {
					tableItem.setText(5, Integer.toString(channels[i].getMaxAttempts()));
				} else {
					tableItem.setText(5, dash);
				}
				if (channels[i].getNormalizeChannel() != null) {
					tableItem.setText(6, channels[i].getNormalizeChannel().getName());
				} else {
					tableItem.setText(6, dash);
				}
			}
			
			Table meanTable = new Table(sashForm, SWT.NONE);
			meanTable.setHeaderVisible(true);
			meanTable.setLinesVisible(true);
			meanTable.setLayoutData(gridData);
			TableColumn nameColumn = new TableColumn(meanTable, SWT.NONE);
			nameColumn.setWidth(140);
			nameColumn.setText("Detector Channel");
			TableColumn intervalColumn = new TableColumn(meanTable, SWT.NONE);
			intervalColumn.setWidth(140);
			intervalColumn.setText("Trigger Interval");
			TableColumn normColumn = new TableColumn(meanTable, SWT.NONE);
			normColumn.setWidth(140);
			normColumn.setText("Norm. Channel");
			TableColumn stoppedByColumn = new TableColumn(meanTable, SWT.NONE);
			stoppedByColumn.setWidth(140);
			stoppedByColumn.setText("Stopped By");

			for (Channel channel : channels) {
				if (!channel.getChannelMode().equals(ChannelModes.INTERVAL)) {
					continue;
				}
				
				TableItem tableItem = new TableItem(meanTable, SWT.NONE);
				tableItem.setText(0, channel.getAbstractDevice().getName());
				tableItem.setText(1, Double.toString(channel.getTriggerInterval()));
				if (channel.getNormalizeChannel() != null) {
					tableItem.setText(2, channel.getNormalizeChannel().getName());
				} else {
					tableItem.setText(2, dash);
				}
				tableItem.setText(3, channel.getStoppedBy().getName());
			}
			
			chainShell.open();
			shellTable[selection] = chainShell;
		} else {
			// Chain Infos anzeigen
			Display display = Activator.getDefault().getWorkbench().getDisplay();
			Shell chainShell = new Shell(display);
			chainShell.setSize(500, 200);
			chainShell.setLocation(workbenchBounds.x + workbenchBounds.width/2 - 250, 
					workbenchBounds.y + workbenchBounds.height/2 - 100);
			chainShell.setText("Chain Info");

			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			GridData gridData;

			chainShell.setLayout(gridLayout);

			Label chainLabel = new Label(chainShell, SWT.NONE);
			chainLabel.setText("Chain ID:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			chainLabel.setLayoutData(gridData);
			Label chainText = new Label(chainShell, SWT.NONE);
			chainText.setText(rows[selection].getText(0));
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			chainText.setLayoutData(gridData);

			Label descLabel = new Label(chainShell, SWT.NONE);
			descLabel.setText("Save Scan-Description:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			descLabel.setLayoutData(gridData);
			Label descText = new Label(chainShell, SWT.NONE);
			if (displayChain.isSaveScanDescription()) {
				descText.setText(" YES ");
			} else {
				descText.setText(" NO ");
			}
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			descText.setLayoutData(gridData);

			Label confLabel = new Label(chainShell, SWT.NONE);
			confLabel.setText("Confirm Save:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confLabel.setLayoutData(gridData);
			Label confText = new Label(chainShell, SWT.NONE);
			if (displayChain.isConfirmSave()) {
				confText.setText(" YES ");
			} else {
				confText.setText(" NO ");
			}
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			confText.setLayoutData(gridData);

			Label autoincrLabel = new Label(chainShell, SWT.NONE);
			autoincrLabel.setText("Add Autoincrementing Number to Filename:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			autoincrLabel.setLayoutData(gridData);
			Label autoincrText = new Label(chainShell, SWT.NONE);
			if (displayChain.isAutoNumber()) {
				autoincrText.setText(" YES ");
			} else {
				autoincrText.setText(" NO ");
			}
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			autoincrText.setLayoutData(gridData);

			Label commentLabel = new Label(chainShell, SWT.NONE);
			commentLabel.setText("Comment:");
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			commentLabel.setLayoutData(gridData);
			Label commentText = new Label(chainShell, SWT.NONE);
			commentText.setText(displayChain.getComment());
			gridData = new GridData();
			gridData.horizontalAlignment = GridData.FILL;
			commentText.setLayoutData(gridData);

			chainShell.open();
			shellTable[selection] = chainShell;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, 
			int repeatCount) {
		if (EngineStatus.IDLE_XML_LOADED.equals(engineStatus)) {
			this.viewer.getTable().getDisplay().asyncExec(new Runnable() {
				@Override public void run() {
					// alte Info-Fenster des letzten XML-Files werden gelöscht
					for ( int j=0; j<shellTable.length; j++) {
						if (shellTable[j] != null) {
							if (!shellTable[j].isDisposed()) {
								shellTable[j].dispose();
							}
						}
					}
				}
			});
		}
	}
}