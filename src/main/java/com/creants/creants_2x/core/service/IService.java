package com.creants.creants_2x.core.service;

/**
 * @author LamHa
 *
 */
public interface IService {
	void init(Object obj);


	void destroy(Object obj);


	void handleMessage(Object message) throws Exception;


	String getName();


	void setName(String name);
}
