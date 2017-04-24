package com.creants.creants_2x.core;

/**
 * @author LamHa
 *
 */
public class ObjectFactory {
	protected Object loadClass(String className) throws Exception {
		Class<?> serviceClass = Class.forName(className);
		return serviceClass.newInstance();
	}
}
