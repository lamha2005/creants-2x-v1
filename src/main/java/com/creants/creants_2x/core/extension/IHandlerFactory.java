package com.creants.creants_2x.core.extension;

/**
 * @author LamHa
 *
 */
public interface IHandlerFactory {
	void addHandler(String p0, final Class<?> p1);

	void addHandler(final String p0, final Object p1);

	void removeHandler(final String p0);

	Object findHandler(final String p0) throws InstantiationException, IllegalAccessException;

	void clearAll();
}
