package de.ptb.epics.eve.editor.views.plotwindowview;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class IdTargetToModelValidator implements IValidator {
	private static final Logger LOGGER = Logger.getLogger(
			IdTargetToModelValidator.class.getName());
	private PlotWindowView plotWindowView;
	
	public IdTargetToModelValidator(PlotWindowView plotWindowView) {
		this.plotWindowView = plotWindowView;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String val = (String)value;
		LOGGER.debug("validating: " + val);
		if (val.isEmpty()) {
			LOGGER.debug("Id must not be empty!");
			return ValidationStatus.error("Id must not be empty!");
		}
		try {
			int i = Integer.parseInt(val);
			if (i < 0) {
				return ValidationStatus.error("Id must not be negative!");
			}
			List<ScanModule> scanModulesUsingSameId = this.plotWindowView.
					getPlotWindow().getScanModule().getChain().getUsedIds().
						get(i);
			if (scanModulesUsingSameId == null) {
				return ValidationStatus.ok();
			}
			scanModulesUsingSameId.remove(plotWindowView.getPlotWindow().
					getScanModule());
			if (!scanModulesUsingSameId.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				for (ScanModule sm : scanModulesUsingSameId) {
					stringBuilder.append(sm.getName() + ", ");
				}
				String smList = stringBuilder.toString();
				smList = smList.substring(0, smList.length()-2);
				LOGGER.debug("Scan Modules using the same plot window id: " + 
						smList);
				if (scanModulesUsingSameId.size() == 1) {
					return ValidationStatus.info(
							"Plot Id is also used in Scan Module: " + 
							smList);
				} else {
					return ValidationStatus.info(
							"Plot Id is also used in Scan Modules: " +
							smList);
				}
			}
			LOGGER.debug("no scan modules are using the same plot window id.");
			return ValidationStatus.ok();
		} catch (NumberFormatException e) {
			return ValidationStatus.error("cannot parse integer.");
		}
	}
}