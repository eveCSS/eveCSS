package de.ptb.epics.eve.data.scandescription.macro;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class MacroResolver {
	private static MacroResolver INSTANCE;
	
	private List<Macro> macros;
	
	private MacroResolver() {
		macros = new ArrayList<Macro>();
		macros.add(new WeekMacro());
		macros.add(new YearMacro());
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
		List<String> searchList = new ArrayList<>();
		List<String> replacementList = new ArrayList<>();
		for (Macro macro : this.macros) {
			searchList.add(macro.getName());
			replacementList.add(macro.resolve());
		}
		return StringUtil.replaceEach(macroString, searchList, replacementList);
	}
	
	/**
	 * Returns a new list containing all macros of MacroResolver.
	 * @return a new list containing all macros
	 */
	public List<Macro> getMacros() {
		return new ArrayList<Macro>(this.macros);
	}
}