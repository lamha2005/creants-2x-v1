package com.creants.creants_2x.core.extension;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.annotations.MultiHandler;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.extension.filter.FilterAction;
import com.creants.creants_2x.core.extension.filter.IFilterChain;
import com.creants.creants_2x.core.extension.filter.QAntExtensionFilter;
import com.creants.creants_2x.core.extension.filter.QAntExtensionFilterChain;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public abstract class QAntExtension extends BaseQAntExtension {
	public static final String MULTIHANDLER_REQUEST_ID = "__[[REQUEST_ID]]__";
	private final IHandlerFactory handlerFactory;
	private final IFilterChain filterChain;

	public QAntExtension() {
		this.handlerFactory = new QAntHandlerFactory(this);
		this.filterChain = new QAntExtensionFilterChain(this);
	}

	@Override
	public void destroy() {
		this.handlerFactory.clearAll();
		this.filterChain.destroy();
		this.removeEventsForListener(this);
	}

	protected void addRequestHandler(String requestId, Class<?> theClass) {
		if (!IClientRequestHandler.class.isAssignableFrom(theClass)) {
			throw new QAntRuntimeException(
					String.format("Provided Request Handler does not implement IClientRequestHandler: %s, Cmd: %s",
							theClass, requestId));
		}

		handlerFactory.addHandler(requestId, theClass);
	}

	protected void addRequestHandler(String requestId, IClientRequestHandler requestHandler) {
		handlerFactory.addHandler(requestId, requestHandler);
	}

	protected void addEventHandler(QAntEventType eventType, Class<?> theClass) {
		if (!IServerEventHandler.class.isAssignableFrom(theClass)) {
			throw new QAntRuntimeException(
					String.format("Provided Event Handler does not implement IServerEventHandler: %s, Cmd: %s",
							theClass, eventType.toString()));
		}

		addEventListener(eventType, this);
		handlerFactory.addHandler(eventType.toString(), theClass);
	}

	protected void addEventHandler(QAntEventType eventType, IServerEventHandler handler) {
		this.addEventListener(eventType, this);
		this.handlerFactory.addHandler(eventType.toString(), handler);
	}

	protected void removeRequestHandler(String requestId) {
		this.handlerFactory.removeHandler(requestId);
	}

	protected void removeEventHandler(QAntEventType eventType) {
		this.removeEventListener(eventType, this);
		this.handlerFactory.removeHandler(eventType.toString());
	}

	protected void clearAllHandlers() {
		this.handlerFactory.clearAll();
	}

	@Override
	public void handleClientRequest(String requestId, QAntUser sender, IQAntObject params) {
		if (filterChain.size() > 0 && filterChain.runRequestInChain(requestId, sender, params) == FilterAction.HALT) {
			return;
		}
		try {
			IClientRequestHandler handler = (IClientRequestHandler) handlerFactory.findHandler(requestId);
			if (handler == null) {
				throw new QAntRuntimeException("Request handler not found: '" + requestId
						+ "'. Make sure the handler is registered in your extension using addRequestHandler()");
			}

			if (handler.getClass().isAnnotationPresent(MultiHandler.class)) {
				final String[] requestNameTokens = requestId.split("\\.");
				params.putUtfString("__[[REQUEST_ID]]__", requestNameTokens[requestNameTokens.length - 1]);
			}

			handler.handleClientRequest(sender, params);
		} catch (InstantiationException err) {
			QAntTracer.warn(this.getClass(), "Cannot instantiate handler class: " + err);
		} catch (IllegalAccessException err2) {
			QAntTracer.warn(this.getClass(), "Illegal access for handler class: " + err2);
		}
	}

	@Override
	public void handleServerEvent(IQAntEvent event) throws Exception {
		String handlerId = event.getType().toString();
		if (filterChain.size() > 0 && filterChain.runEventInChain(event) == FilterAction.HALT) {
			return;
		}

		try {
			IServerEventHandler handler = (IServerEventHandler) handlerFactory.findHandler(handlerId);
			if (handler == null) {
				if (this.getLevel() == ExtensionLevel.ROOM
						&& this.getParentZone().getRoomById(this.getParentRoom().getId()) == null) {
					return;
				}
				throw new QAntRuntimeException("Event handler not found: '" + handlerId
						+ "'. Make sure the handler is registered in your extension using addEventHandler()");
			} else {
				Thread.currentThread().setContextClassLoader(handler.getClass().getClassLoader());
				handler.handleServerEvent(event);
			}
		} catch (InstantiationException err) {
			QAntTracer.warn(this.getClass(), "Cannot instantiate handler class: " + err);
		} catch (IllegalAccessException err2) {
			QAntTracer.warn(this.getClass(), "Illegal access for handler class: " + err2);
		}
	}

	public final void addFilter(String filterName, QAntExtensionFilter filter) {
		filterChain.addFilter(filterName, filter);
	}

	public void removeFilter(final String filterName) {
		filterChain.remove(filterName);
	}

	public void clearFilters() {
		filterChain.destroy();
	}
}
