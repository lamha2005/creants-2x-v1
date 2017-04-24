package com.creants.creants_2x.core.controllers;

import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHM
 *
 */
public interface IControllerCommand {
	void execute(IRequest request) throws Exception;


	boolean validate(IRequest request) throws Exception;


	Object preProcess(IRequest request) throws Exception;

}
