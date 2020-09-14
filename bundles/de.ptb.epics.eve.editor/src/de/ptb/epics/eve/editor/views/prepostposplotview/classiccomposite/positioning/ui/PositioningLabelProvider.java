package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningErrorTypes;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.CommonLabelProvider;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PositioningLabelProvider extends CommonLabelProvider {
	private static final String EM_DASH = "\u2014";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		final Positioning positioning = (Positioning)element;
		if (columnIndex == 0) {
			return DELETE_IMG;
		} else if (columnIndex == 2) { // Plugin column
			for(IModelError error : positioning.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType().equals(
							PluginErrorTypes.PLUING_NOT_SET)) {
						return ERROR_IMG;
					}
				}
			}
		} else if (columnIndex == 3) { // Detector Channel column
			for (IModelError error : positioning.getModelErrors()) {
				if (error instanceof PositioningError) {
					final PositioningError positioningError = 
							(PositioningError)error;
					if (positioningError.getErrorType().equals(
							PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET)) {
						return ERROR_IMG;
					}
				}
			}
		} else if (columnIndex == 5) { // Parameters column
			for(IModelError error : positioning.getModelErrors()) {
				if (error instanceof PluginError) {
					final PluginError pluginError = (PluginError)error;
					if (pluginError.getPluginErrorType().equals(
							PluginErrorTypes.MISSING_MANDATORY_PARAMETER)) {
						return ERROR_IMG;
					}
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		final Positioning pos = (Positioning)element;
		switch (columnIndex) {
		case 1: return (pos.getMotorAxis() != null)
				? pos.getMotorAxis().getName()
				: null;
		case 2: return (pos.getPluginController().getPlugin() != null)
				? pos.getPluginController().getPlugin().getName()
				: null;
		case 3: return (pos.getDetectorChannel() != null)
				? pos.getDetectorChannel().getName()
				: null;
		case 4: return (pos.getNormalization() != null)
				? pos.getNormalization().getName()
				: EM_DASH;
		case 5: if (pos.getPluginController().getPlugin() != null && 
				!pos.getPluginController().getPlugin().getParameters().isEmpty()) {
					return pos.getPluginController().toString();
				}
				return EM_DASH;
		default: return null;
		}
	}
}
