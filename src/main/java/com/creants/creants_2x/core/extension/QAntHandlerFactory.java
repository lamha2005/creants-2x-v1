package com.creants.creants_2x.core.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.core.annotations.Instantiation;
import com.creants.creants_2x.core.annotations.MultiHandler;

/**
 * @author LamHa
 *
 */
@Instantiation(Instantiation.InstantiationMode.NEW_INSTANCE)
public class QAntHandlerFactory implements IHandlerFactory {
	private static final String DOT_SEPARATOR = ".";
	private final Map<String, Class<?>> handlers;
	private final Map<String, Object> cachedHandlers;
	private final QAntExtension parentExtension;

	public QAntHandlerFactory(QAntExtension parentExtension) {
		this.handlers = new ConcurrentHashMap<String, Class<?>>();
		this.cachedHandlers = new ConcurrentHashMap<String, Object>();
		this.parentExtension = parentExtension;
	}

	@Override
	public void addHandler(String handlerKey, Class<?> handlerClass) {
		handlers.put(handlerKey, handlerClass);
	}

	@Override
	public void addHandler(String handlerKey, Object requestHandler) {
		setHandlerParentExtension(requestHandler);
		cachedHandlers.put(handlerKey, requestHandler);
	}

	@Override
	public synchronized void clearAll() {
		handlers.clear();
		cachedHandlers.clear();
	}

	@Override
	public synchronized void removeHandler(final String handlerKey) {
		handlers.remove(handlerKey);
		if (cachedHandlers.containsKey(handlerKey)) {
			cachedHandlers.remove(handlerKey);
		}
	}

	@Override
	public Object findHandler(String key) throws InstantiationException, IllegalAccessException {
		Object handler = this.getHandlerInstance(key);
		if (handler == null) {
			int lastDotPos = key.lastIndexOf(DOT_SEPARATOR);
			if (lastDotPos > 0) {
				key = key.substring(0, lastDotPos);
			}
			handler = getHandlerInstance(key);
			if (handler != null && !handler.getClass().isAnnotationPresent(MultiHandler.class)) {
				handler = null;
			}
		}
		return handler;
	}

	private Object getHandlerInstance(String key) throws InstantiationException, IllegalAccessException {
		Object handler = this.cachedHandlers.get(key);
		if (handler != null) {
			return handler;
		}

		Class<?> handlerClass = handlers.get(key);
		if (handlerClass == null) {
			return null;
		}

		handler = handlerClass.newInstance();
		setHandlerParentExtension(handler);
		if (handlerClass.isAnnotationPresent(Instantiation.class)) {
			Instantiation instAnnotation = handlerClass.getAnnotation(Instantiation.class);
			if (instAnnotation.value() == Instantiation.InstantiationMode.SINGLE_INSTANCE) {
				cachedHandlers.put(key, handler);
			}
		}

		return handler;
	}

	private void setHandlerParentExtension(Object handler) {
		if (handler instanceof IClientRequestHandler) {
			((IClientRequestHandler) handler).setParentExtension(this.parentExtension);
		} else if (handler instanceof IServerEventHandler) {
			((IServerEventHandler) handler).setParentExtension(this.parentExtension);
		}
	}
}
