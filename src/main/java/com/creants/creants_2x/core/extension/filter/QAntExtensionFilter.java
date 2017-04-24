package com.creants.creants_2x.core.extension.filter;

import com.creants.creants_2x.core.extension.QAntExtension;

/**
 * @author LamHa
 *
 */
public abstract class QAntExtensionFilter implements IFilter {
	private String name;
	protected QAntExtension parentExtension;

	@Override
	public void init(QAntExtension ext) {
		this.parentExtension = ext;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
