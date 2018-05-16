package de.ptb.epics.eve.editor.handler.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class DoWithCurrent implements IParameterValues {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map getParameterValues() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("Append current Scanmodule", "append");
		parameters.put("Nest current Scanmodule", "nest");
		return parameters;
	}
}