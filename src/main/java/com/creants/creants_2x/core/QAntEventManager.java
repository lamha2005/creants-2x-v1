package com.creants.creants_2x.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.core.util.executor.SmartExecutorConfig;
import com.creants.creants_2x.core.util.executor.SmartThreadPoolExecutor;

/**
 * @author LamHM
 *
 */
public class QAntEventManager extends BaseCoreService implements IQAntEventManager {
	private ThreadPoolExecutor threadPool;
	private final Map<QAntEventType, Set<IQAntEventListener>> listenersByEvent;
	private boolean inited;


	public QAntEventManager() {
		setName("QAntEventManager");
		inited = false;
		listenersByEvent = new ConcurrentHashMap<QAntEventType, Set<IQAntEventListener>>();
	}


	@Override
	public synchronized void init(Object o) {
		QAntTracer.info(this.getClass(), "- init QAntEventManager");
		if (!inited) {
			super.init(o);
			SmartExecutorConfig cfg = new SmartExecutorConfig();
			cfg.name = "Ext";
			threadPool = new SmartThreadPoolExecutor(cfg);
			inited = true;
		}
	}


	@Override
	public void addEventListener(QAntEventType type, IQAntEventListener listener) {
		Set<IQAntEventListener> listeners = listenersByEvent.get(type);
		if (listeners == null) {
			listeners = new CopyOnWriteArraySet<IQAntEventListener>();
			listenersByEvent.put(type, listeners);
		}
		listeners.add(listener);
	}


	@Override
	public boolean hasEventListener(QAntEventType type) {
		boolean found = false;
		Set<IQAntEventListener> listeners = listenersByEvent.get(type);
		if (listeners != null && listeners.size() > 0) {
			found = true;
		}

		return found;
	}


	@Override
	public void removeEventListener(QAntEventType type, IQAntEventListener listener) {
		Set<IQAntEventListener> listeners = listenersByEvent.get(type);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}


	@Override
	public void dispatchEvent(IQAntEvent event) {
		Set<IQAntEventListener> listeners = listenersByEvent.get(event.getType());
		if (listeners != null && listeners.size() > 0) {
			for (IQAntEventListener listener : listeners) {
				threadPool.execute(new QAntEventRunner(listener, event));
			}
		}
	}


	@Override
	public void destroy(Object o) {
		super.destroy(o);
		listenersByEvent.clear();
		QAntTracer.info(this.getClass(), String.valueOf(name) + " shut down.");
	}


	@Override
	public void setThreadPoolSize(int poolSize) {
		threadPool.setCorePoolSize(poolSize);
	}


	@Override
	public Executor getThreadPool() {
		return this.threadPool;
	}

	private static final class QAntEventRunner implements Runnable {
		private final IQAntEventListener listener;
		private final IQAntEvent event;


		public QAntEventRunner(IQAntEventListener listener, IQAntEvent event) {
			this.listener = listener;
			this.event = event;
		}


		@Override
		public void run() {
			try {
				listener.handleServerEvent(event);
			} catch (Throwable t) {
				QAntTracer.warn(QAntEventRunner.class,
						"Error handling event: " + event + " Listener: " + listener);
			}
		}
	}
}
