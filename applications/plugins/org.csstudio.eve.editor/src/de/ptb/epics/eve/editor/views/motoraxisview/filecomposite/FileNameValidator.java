package de.ptb.epics.eve.editor.views.motoraxisview.filecomposite;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.math.statistics.DescriptiveStats;
import de.ptb.epics.eve.util.math.statistics.Range;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class FileNameValidator implements IValidator {

	private TableViewer viewer;
	private Axis axis;
	
	/**
	 * @param axis the axis
	 * @param viewer the table viewer
	 */
	public FileNameValidator(Axis axis, TableViewer viewer) {
		this.viewer = viewer;
		this.axis = axis;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String filename = String.valueOf(value);
		File file = new File(filename);
		if (filename.isEmpty()) {
			this.resetTable();
			return ValidationStatus.error("Providing a file name is mandatory!");
		} else if (!file.exists()) {
			this.resetTable();
			return ValidationStatus.warning("File does not exist!");
		}
		
		Double hlm = this.axis.getMotorAxis().getChannelAccess().getHighLimit();
		Double llm = this.axis.getMotorAxis().getChannelAccess().getLowLimit();
		
		switch (axis.getType()) {
		case DOUBLE: // intended fall through
		case INT:
			try {
				List<Double> values = StringUtil.getDoubleList(FileUtil
						.readLines(file));
				if (values == null) {
					this.resetTable();
					return ValidationStatus.error("File contains syntax errors!");
				}
				DescriptiveStats stats = new DescriptiveStats(values);
				stats.calculateStats();
				List<DescriptiveStats> statList = new ArrayList<DescriptiveStats>();
				statList.add(stats);
				this.viewer.getTable().setEnabled(true);
				this.viewer.setInput(statList);
				
				// do limit check only for non-discrete motor axes
				if (!this.axis.getMotorAxis().getGoto().isDiscrete()) {
					List<Double> problems = new ArrayList<Double>();
					if (hlm != null && llm != null) {
						for (double d : values) {
							if (!Range.isInRange(d, llm, hlm)) {
								problems.add(d);
							}
						}
					}
					if (!problems.isEmpty()) {
						return ValidationStatus.warning(
							"The following values exceed the current axis bounds: "
							+ StringUtil.buildCommaSeparatedString(problems));
					}
				}
			} catch (IOException e) {
				this.resetTable();
				return ValidationStatus.error(e.getMessage());
			}
			break;
		case STRING:
			try {
				DescriptiveStats stats = new DescriptiveStats(FileUtil
						.readLines(file).size());
				List<DescriptiveStats> statList = new ArrayList<DescriptiveStats>();
				statList.add(stats);
				this.viewer.getTable().setEnabled(true);
				this.viewer.setInput(statList);
			} catch (IOException e) {
				return ValidationStatus.error(e.getMessage());
			}
			break;
		default:
			return ValidationStatus.error("unknown axis type");
		}
		return ValidationStatus.ok();
	}
	
	private void resetTable() {
		this.viewer.setInput(null);
		this.viewer.getTable().setEnabled(false);
	}
}