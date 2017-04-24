package com.creants.creants_2x.core.api;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.service.IService;
import com.creants.creants_2x.core.util.QAntTracer;

/**
 * @author LamHM
 *
 */
public class APIManager implements IService {
	private final String serviceName = "APIManager";
	private QAntServer qant;
	private IQAntApi api;


	@Override
	public void init(Object obj) {
		QAntTracer.info(this.getClass(), "- init APIManager");
		qant = QAntServer.getInstance();
		api = new QAntAPI(qant);
	}


	public IQAntApi getQAntApi() {
		return api;
	}


	@Override
	public void destroy(Object obj) {
	}


	@Override
	public void handleMessage(Object message) {
		throw new UnsupportedOperationException("Not supported");

	}


	@Override
	public String getName() {
		return serviceName;
	}


	@Override
	public void setName(String name) {
		throw new UnsupportedOperationException("Not supported");
	}

}
