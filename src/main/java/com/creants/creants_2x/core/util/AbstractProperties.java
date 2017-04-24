package com.creants.creants_2x.core.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Abstract class which provides various methods which are common to
 * GameProperties, GameLabels and Labels.
 *
 * @author LamHa
 */
public class AbstractProperties {

	String filename;
	/** Resource bundle. */
	protected ResourceBundle rb = null;// resource bundle
	/**
	 * Language for this bundle.
	 */
	private Locale locale;

	/**
	 * Private constructor (Can only be called by the getInstance() method.
	 * 
	 * @param filename
	 */
	public AbstractProperties(String filename, Locale locale) {
		this.filename = filename;
		this.locale = locale;
		// Load resource bundles
		reload();
	}

	/**
	 * Reload all resource bundles (if there has been a
	 */
	public void reload() {
		// Populate resource bundle
		try {
			rb = ResourceBundle.getBundle(filename, locale);
		} catch (MissingResourceException mrEx) {
			mrEx.printStackTrace();
		}
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as a
	 * String.
	 *
	 * @param key
	 *            Key to get value from.
	 * @return Trimed value from a specified trimmed key.
	 */
	public String get(String key) {
		if (rb != null) {
			return rb.getString(key.trim()).trim();
		}
		return null;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as a
	 * String. If the value doesn't
	 *
	 * @param key
	 *            Key to get value from.
	 * @param defaultValue
	 *            Default value if key doesn't retrieve a value.
	 * @return String value.
	 */
	public String get(String key, String defaultValue) {
		try {
			if (rb != null) {
				return rb.getString(key.trim()).trim();
			}
		} catch (Exception mrEx) {
			mrEx.printStackTrace();
		}

		return defaultValue;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * int value.
	 *
	 * @param key
	 *            Key to get value from.
	 * @return String value.
	 */
	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * int value. If the value doesn't exist then return its defaultValue
	 * instead.
	 *
	 * @param key
	 *            Key to get value from.
	 * @param defaultValue
	 *            Default integer value if key isn't found.
	 * @return Integer value.
	 */
	public int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt(get(key));
		} catch (Exception mrEx) {
			mrEx.printStackTrace();
		}

		return defaultValue;
	}

	/**
	 * Return the "value" for a specified "key" inside the resource bundle as an
	 * String value, in case "value" contains "variables". If the value doesn't
	 * exist then return null. instead. Example of use:
	 * property.with.five.variables = This property is the {1} and contains this
	 * variables: {2},{3},{4} and {5} In java code: ObjectArray[] = {"1",new
	 * Integer(2),"-23",new Double(2.34),0} String text =
	 * labels.get("property.with.five.variables",ObjectArray) and then text =
	 * This property is the 1 and contains this variables: 2,-23,2.34 and 0
	 *
	 * @param key
	 *            Key to get value from.
	 * @param arguments
	 *            Arguments
	 * @return String value.
	 */
	public String get(String key, Object[] arguments) {
		String pattern = get(key);
		MessageFormat mf = new MessageFormat(pattern);
		return mf.format(arguments, new StringBuffer(), null).toString();
	}

	/**
	 * Return a boolean value from a key.
	 *
	 * @param key
	 *            Key to get value from.
	 * @return Returns true if value="true"
	 */
	public boolean getBoolean(String key) {
		return get(key).equals("true");
	}

	/**
	 * Return a boolean value from a key. If the key doesn't exist then return
	 * the default value.
	 *
	 * @param key
	 *            Key to get value from.
	 * @param defaultValue
	 *            Default boolean value if key isn't found.
	 * @return Returns true if value="true" or default value if not value
	 *         exists.
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		try {
			return getBoolean(key);
		} catch (Exception mrEx) {
			mrEx.printStackTrace();
		}

		return defaultValue;
	}

	public ResourceBundle getResourceBundle() {
		return rb;
	}
}
