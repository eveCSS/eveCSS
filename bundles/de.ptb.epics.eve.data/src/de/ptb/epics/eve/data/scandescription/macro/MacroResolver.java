package de.ptb.epics.eve.data.scandescription.macro;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class MacroResolver {
	private static final Logger LOGGER = Logger.getLogger(MacroResolver.class
			.getName());
	
	private static final String REGEX_PREFIX = "\\$\\{PV:";
	private static final String REGEX_VALID_PV_CHARS = "[a-zA-Z0-9_\\-:\\.\\[\\]<>;]+";
	private static final String REGEX_SUFFIX = "\\}";
	
	private static MacroResolver INSTANCE;
	
	private List<Macro> macros;
	
	private MacroResolver() {
		macros = new ArrayList<Macro>();
		macros.add(new WeekMacro());
		macros.add(new YearMacro());
		macros.add(new Year2DMacro());
		macros.add(new MonthMacro());
		macros.add(new MonthStrMacro());
		macros.add(new DayMacro());
		macros.add(new DayStrMacro());
		macros.add(new DateMacro());
		macros.add(new DateHyphenMacro());
		macros.add(new TimeMacro());
		macros.add(new TimeHyphenMacro());
	}
	
	public static MacroResolver getInstance() {
		if (MacroResolver.INSTANCE == null) {
			MacroResolver.INSTANCE = new MacroResolver();
		}
		return MacroResolver.INSTANCE;
	}
	
	/**
	 * Resolves a string containing macros by replacing each occurence of a 
	 * known macro.
	 * 
	 * @param macroString the unresolved string
	 * @return the resolved string
	 */
	public String resolve(String macroString) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("resolving macros in '" + macroString + "'");
		}

		String regex = MacroResolver.REGEX_PREFIX
				+ MacroResolver.REGEX_VALID_PV_CHARS
				+ MacroResolver.REGEX_SUFFIX;
		LOGGER.debug("Pattern for PV replacement is: " + regex);
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(macroString);
		final List<String> pvmacros = new ArrayList<String>();
		while (m.find()) {
			pvmacros.add(m.group());
		}
		
		// connect pvs
		final List<MacroPV> pvs = new ArrayList<>();
		for (String s : pvmacros) {
			pvs.add(new MacroPV(s));
		}
		
		// add non-pv macros
		List<String> searchList = new ArrayList<>();
		List<String> replacementList = new ArrayList<>();
		for (Macro macro : this.macros) {
			searchList.add(macro.getName());
			replacementList.add(macro.resolve());
		}
		
		int loops = 0;
		final int max_loops = 40;
		
		while (loops < max_loops) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.warn(e.getMessage(), e);
			}
			if (allResolved(pvs)) {
				break;
			}
			loops++;
		}
		if (!pvs.isEmpty()) {
			if (loops < max_loops) {
				LOGGER.info("PVs read. Resolving macros.");
			} else {
				LOGGER.warn("One or more PVs could not be read and will not be resolved.");
			}
		}
		for (MacroPV macroPV: pvs) {
			if (!macroPV.isResolved()) {
				macroPV.disconnect();
			}
			searchList.add(macroPV.getName());
			replacementList.add(macroPV.resolve());
		}
		
		String resolvedString = StringUtil.replaceEach(macroString, searchList, replacementList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("resolved string is: '" + resolvedString + "'");
		}
		return resolvedString;
	}
	
	/*
	 * 
	 */
	private boolean allResolved(List<MacroPV> macropvlist) {
		for (MacroPV macroPV : macropvlist) {
			if (!macroPV.isResolved()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns a new list containing all macros of MacroResolver.
	 * @return a new list containing all macros
	 */
	public List<Macro> getMacros() {
		return new ArrayList<Macro>(this.macros);
	}
}
