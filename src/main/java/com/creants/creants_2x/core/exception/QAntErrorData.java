package com.creants.creants_2x.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LamHM
 *
 */
public class QAntErrorData {
	IErrorCode code;
	List<String> params;


	public QAntErrorData(IErrorCode code) {
		this.code = code;
		this.params = new ArrayList<String>();
	}


	public IErrorCode getCode() {
		return code;
	}


	public void setCode(IErrorCode code) {
		this.code = code;
	}


	public List<String> getParams() {
		return params;
	}


	public void setParams(List<String> params) {
		this.params = params;
	}


	public void addParameter(String parameter) {
		params.add(parameter);
	}
}
