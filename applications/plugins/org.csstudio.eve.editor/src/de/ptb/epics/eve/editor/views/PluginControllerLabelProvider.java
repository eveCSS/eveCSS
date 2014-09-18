package de.ptb.epics.eve.editor.views;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.editor.views.motoraxisview.plugincomposite.PluginParameterValue;

/**
 * 
 * @author ?
 */
public class PluginControllerLabelProvider implements ITableLabelProvider {

	private PluginController pluginController;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Image getColumnImage(final Object entry, final int colIndex) {
		final List<IModelError> modelErrors = this.pluginController
				.getModelErrors();

		PluginParameterValue pluginParameterValue = (PluginParameterValue) entry;

		if (pluginController.getModelErrors().size() > 0 && colIndex == 1) {

			final Iterator<IModelError> it = modelErrors.iterator();
			while (it.hasNext()) {
				final IModelError modelError = it.next();
				if (modelError instanceof PluginError) {
					final PluginError pluginError = (PluginError) modelError;
					if (pluginError.getPluginErrorType() == PluginErrorTypes.WRONG_VALUE
							&& pluginError.getParameterName().equals(
									pluginParameterValue.getPluginParameter()
											.getName())) {
						return PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
					} else if (pluginError.getPluginErrorType() == PluginErrorTypes.MISSING_MANDATORY_PARAMETER
							&& pluginError.getParameterName().equals(
									pluginParameterValue.getPluginParameter()
											.getName())) {
						return PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
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
	@SuppressWarnings("unchecked")
	public String getColumnText(final Object entry, final int colIndex) {

		PluginParameterValue pluginParameterValue = (PluginParameterValue) entry;

		switch (colIndex) {
		case 0:
			return pluginParameterValue.getPluginParameter().getName();
		case 1:
			if (pluginParameterValue.getPluginParameter().isDiscrete() ) {
				return pluginParameterValue.getPluginParameter().getNameFromId(
						pluginParameterValue.getValue());
			}
			else
			   return pluginParameterValue.getValue();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object arg0, final String arg1) {
		return false;
	}

	/**
	 * 
	 * @param pluginController
	 */
	public void setPluginController(final PluginController pluginController) {
		this.pluginController = pluginController;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}