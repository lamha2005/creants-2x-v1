package com.creants.creants_2x.core.controllers;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.wood.RequestComparator;
import com.creants.creants_2x.socket.io.IRequest;

/**
 * @author LamHa
 *
 */
public abstract class AbstractController implements IController, Runnable {
	protected Object id;
	protected String name;
	protected BlockingQueue<IRequest> requestQueue;
	protected ExecutorService threadPool;
	protected int threadPoolSize;
	protected volatile int maxQueueSize;
	protected volatile boolean isActive;
	private volatile int threadId;


	public AbstractController() {
		threadPoolSize = -1;
		maxQueueSize = -1;
		isActive = false;
		threadId = 1;
	}


	@Override
	public void enqueueRequest(IRequest request) throws Exception {
		if (requestQueue.size() >= maxQueueSize) {
			throw new Exception("Full queue");
		}

		requestQueue.add(request);
	}


	@Override
	public void init(Object o) {
		if (isActive) {
			throw new IllegalArgumentException("Object is already initialized. Destroy it first!");
		}
		if (threadPoolSize < 1) {
			throw new IllegalArgumentException("Illegal value for a thread pool size: " + threadPoolSize);
		}
		if (maxQueueSize < 1) {
			throw new IllegalArgumentException("Illegal value for max queue size: " + maxQueueSize);
		}

		Comparator<IRequest> requestComparator = new RequestComparator();
		requestQueue = new PriorityBlockingQueue<IRequest>(50, requestComparator);
		threadPool = Executors.newFixedThreadPool(threadPoolSize);
		isActive = true;
		initThreadPool();
		QAntTracer.info(this.getClass(), String.format("Controller started: %s -- Queue: %s/%s",
				this.getClass().getName(), getQueueSize(), getMaxQueueSize()));
	}


	@Override
	public void destroy(final Object o) {
		isActive = false;
		List<Runnable> leftOvers = threadPool.shutdownNow();
		QAntTracer.info(this.getClass(),
				"Controller stopping: " + this.getClass().getName() + ", Unprocessed tasks: " + leftOvers.size());
	}


	@Override
	public void handleMessage(final Object message) {
	}


	@Override
	public void run() {
		Thread.currentThread().setName(String.valueOf(this.getClass().getName()) + "-" + threadId++);
		while (isActive) {
			try {
				processRequest(requestQueue.take());
			} catch (InterruptedException e) {
				isActive = false;
				QAntTracer.warn(AbstractController.class, "Controller main loop was interrupted");
			} catch (Throwable t) {
				QAntTracer.error(AbstractController.class, "Runable fail!");
			}
		}
		QAntTracer.info(AbstractController.class, "Controller worker threads stopped: " + this.getClass().getName());
	}


	public abstract void processRequest(IRequest request) throws Exception;


	@Override
	public Object getId() {
		return id;
	}


	@Override
	public void setId(final Object id) {
		this.id = id;
	}


	@Override
	public String getName() {
		return this.name;
	}


	@Override
	public void setName(String name) {
		this.name = name;
	}


	@Override
	public int getThreadPoolSize() {
		return threadPoolSize;
	}


	@Override
	public void setThreadPoolSize(int threadPoolSize) {
		if (this.threadPoolSize < 1) {
			this.threadPoolSize = threadPoolSize;
		}
	}


	@Override
	public int getQueueSize() {
		return requestQueue.size();
	}


	@Override
	public int getMaxQueueSize() {
		return maxQueueSize;
	}


	@Override
	public void setMaxQueueSize(int maxQueueSize) {
		this.maxQueueSize = maxQueueSize;
	}


	protected void initThreadPool() {
		for (int j = 0; j < threadPoolSize; ++j) {
			threadPool.execute(this);
		}
	}
}
