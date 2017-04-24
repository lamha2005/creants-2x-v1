package com.creants.creants_2x.core.controllers;

import com.creants.creants_2x.core.service.IService;

/**
 * @author LamHa
 *
 */
public interface IControllerManager extends IService {
	IController getControllerById(byte id);

	void addController(byte id, IController controller);

	void removeController(byte id);
}
