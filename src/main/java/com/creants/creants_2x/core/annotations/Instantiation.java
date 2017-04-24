package com.creants.creants_2x.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Instantiation {
	InstantiationMode value() default InstantiationMode.NEW_INSTANCE;

	public enum InstantiationMode {
		NEW_INSTANCE("NEW_INSTANCE", 0), SINGLE_INSTANCE("SINGLE_INSTANCE", 1);

		private InstantiationMode(final String s, final int n) {
		}
	}
}
