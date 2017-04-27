package com.creants.creants_2x.core.entities;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author LamHM
 *
 */
public class QAntIDGenerator implements IDGenerator {

	private final AtomicInteger autoID;


	public QAntIDGenerator() {
		this.autoID = new AtomicInteger(0);
	}


	@Override
	public int generateID() {
		return this.autoID.getAndIncrement();
	}

}
