package com.creants.creants_2x.core.controllers;

import com.creants.creants_2x.core.service.IService;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public interface IController extends IService {
	Object getId();

	void setId(Object id);

	void enqueueRequest(IRequest request) throws Exception;

	int getQueueSize();

	int getMaxQueueSize();

	void setMaxQueueSize(int maXQueueSize);

	int getThreadPoolSize();

	void setThreadPoolSize(int poolSize);
}
