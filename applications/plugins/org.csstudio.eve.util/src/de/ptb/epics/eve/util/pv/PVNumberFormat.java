package de.ptb.epics.eve.util.pv;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Formatter;
import java.util.Locale;

/**
 * A Modified {@link java.text.DecimalFormat} that uses 
 * {@link java.util.Formatter} for general scientific notation.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class PVNumberFormat extends DecimalFormat {

	private static final long serialVersionUID = 1L;

	private DecimalFormat decimalFormat;
	
	public PVNumberFormat(String pattern) {
		Locale locale = new Locale("en");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
		this.decimalFormat = new DecimalFormat(pattern, symbols);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringBuffer format(double number, StringBuffer toAppendTo,
			FieldPosition pos) {
		StringBuffer sb = new StringBuffer();
		Formatter formatter = new Formatter(
				new Locale(Locale.ENGLISH.getCountry()));
		sb.append(formatter.format("%14.7g", number).out().toString().trim());
		return sb;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringBuffer format(long number, StringBuffer toAppendTo, 
			FieldPosition pos) {
		StringBuffer sb = new StringBuffer();
		Formatter formatter = new Formatter(
				new Locale(Locale.ENGLISH.getCountry()));
		sb.append(formatter.format("%d", number).out().toString().trim());
		return sb;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Number parse(String str, ParsePosition parsPos) {
		return this.decimalFormat.parse(str, parsPos);
	}
}