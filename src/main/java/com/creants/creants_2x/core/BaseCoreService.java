package com.creants.creants_2x.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LamHM
 *
 */
public abstract class BaseCoreService implements ICoreService {
	private static final AtomicInteger serviceId;
	protected String name;
	protected volatile boolean active;

	static {
		serviceId = new AtomicInteger(0);
	}


	public BaseCoreService() {
		this.active = false;
	}


	public void init(final Object o) {
		this.name = getId();
		this.active = true;
	}


	public void destroy(final Object o) {
		this.active = false;
	}


	public String getName() {
		return this.name;
	}


	public void setName(final String name) {
		this.name = name;
	}


	public void handleMessage(final Object param) throws Exception {
		throw new UnsupportedOperationException("This method should be overridden by the child class!");
	}


	@Override
	public boolean isActive() {
		return this.active;
	}


	@Override
	public String toString() {
		return "[Core Service]: " + this.name + ", State: " + (this.isActive() ? "active" : "not active");
	}


	protected static String getId() {
		return "AnonymousService-" + BaseCoreService.serviceId.getAndIncrement();
	}

}
