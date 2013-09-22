package pl.ciruk.film.utils.core;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;

/**
 * Klasa uzytkowa zawierajaca metody przydatne podczas przetwarzania tekstu.
 * @author piotrc
 *
 */
public final class StringHelper {
	private static final Log LOG = LogFactory.getLog(StringHelper.class);
	
	private static final NumberFormat DEFAULT_NUMBER_FORMAT = NumberFormat.getInstance(new Locale("PL", "pl"));
	
	private StringHelper() {
		
	}
	
	public static boolean isEmpty(String text) {
		return text == null || text.isEmpty();
	}
	
	public static boolean isNotEmpty(String text) {
		return !isEmpty(text);
	}

	public static boolean fuzzyEquals(String first, String second) {
		Preconditions.checkArgument(first != null, PreconditionsHelper.CANT_BE_NULL, "First string");
		Preconditions.checkArgument(second != null, PreconditionsHelper.CANT_BE_NULL, "Second string");
		
		String trimmedFirst = first.trim().toLowerCase();
		String trimmedSecond = second.trim().toLowerCase();
		
		return StringUtils.getLevenshteinDistance(trimmedFirst, trimmedSecond) <= Math.max(trimmedFirst.length(), trimmedSecond.length()) * 0.1;
	}
	
	public static boolean fuzzyContains(List<String> elements, String element) {
		Preconditions.checkArgument(elements != null, PreconditionsHelper.CANT_BE_NULL, "Elements");
		Preconditions.checkArgument(element != null, PreconditionsHelper.CANT_BE_NULL, "Element");
		
		boolean contains = false;
		for (String e : elements) {
			if (fuzzyEquals(element, e)) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	
	/**
	 * Checks whether every element from {@code patterns} can be found within {@code elements} list.
	 * @param elements
	 * @param patterns
	 * @return
	 */
	public static boolean fuzzyContainsAll(List<String> elements, List<String> patterns) {
		Preconditions.checkArgument(elements != null, PreconditionsHelper.CANT_BE_NULL, "Elements");
		Preconditions.checkArgument(patterns != null, PreconditionsHelper.CANT_BE_NULL, "Patterns");
		
		boolean contains = true;
		for (String e : patterns) {
			if (!fuzzyContains(elements, e)) {
				contains = false;
				break;
			}
		}
		return contains;
	}

	public static boolean containsAny(String[] keys, String line) {
		for (String keyword : keys) {
			if (line.contains(keyword)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Double parseNumber(String number) {
		return parseNumber(number, DEFAULT_NUMBER_FORMAT);
	}
	
	public static Double parseNumber(String number, Locale locale) {
		Preconditions.checkArgument(locale != null, PreconditionsHelper.CANT_BE_NULL, "Locale");
		
		return parseNumber(number, NumberFormat.getInstance(locale));
	}
	
	private static Double parseNumber(String number, NumberFormat format) {
		Double result = Double.NaN;
		try { 
			result = format.parse(number).doubleValue(); 
		} catch (NumberFormatException nfe) {
			LOG.debug("parseNumber - This is not a number: " + number, nfe);
		} catch (ParseException e) {
			LOG.debug("parseNumber - This is not a number: " + number, e);
		}
		return result;
	}
	
	public static boolean containsAny(List<String> keys, String line) {
		return containsAny(keys.toArray(new String[keys.size()]), line);
	}
}
