package com.creants.creants_2x.core.extension.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.creants.creants_2x.core.IQAntEvent;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.exception.QAntRuntimeException;
import com.creants.creants_2x.core.extension.QAntExtension;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHa
 *
 */
public class QAntExtensionFilterChain implements IFilterChain {
	private final Collection<QAntExtensionFilter> filters;
	private final QAntExtension parentExtension;

	public QAntExtensionFilterChain(QAntExtension parentExtension) {
		this.parentExtension = parentExtension;
		this.filters = new ConcurrentLinkedQueue<QAntExtensionFilter>();
	}

	@Override
	public void addFilter(String filterName, QAntExtensionFilter filter) {
		if (filters.contains(filter)) {
			throw new QAntRuntimeException(
					"A filter with the same name already exists: " + filterName + ", Ext: " + this.parentExtension);
		}
		filter.setName(filterName);
		filter.init(parentExtension);
		filters.add(filter);
	}

	@Override
	public void remove(String filterName) {
		Iterator<QAntExtensionFilter> it = this.filters.iterator();
		while (it.hasNext()) {
			QAntExtensionFilter filter = it.next();
			if (filter.getName().equals(filterName)) {
				it.remove();
				break;
			}
		}
	}

	@Override
	public FilterAction runEventInChain(IQAntEvent event) throws QAntException {
		FilterAction filterAction = FilterAction.CONTINUE;
		for (QAntExtensionFilter filter : this.filters) {
			try {
				filterAction = filter.handleServerEvent(event);
				if (filterAction == FilterAction.HALT) {
					break;
				}
				continue;
			} catch (QAntException sfsEx) {
				throw sfsEx;
			} catch (Exception e) {
				QAntTracer.warn(this.getClass(),
						String.format("Exception in FilterChain execution:%s --- Filter: %s, Event: %s, Ext: %s",
								e.toString(), filter.getName(), event, this.parentExtension));
			}
		}
		return filterAction;
	}

	@Override
	public FilterAction runRequestInChain(String requestId, QAntUser sender, IQAntObject params) {
		FilterAction filterAction = FilterAction.CONTINUE;
		for (QAntExtensionFilter filter : this.filters) {
			try {
				filterAction = filter.handleClientRequest(requestId, sender, params);
				if (filterAction == FilterAction.HALT) {
					break;
				}
				continue;
			} catch (Exception e) {
				QAntTracer.warn(this.getClass(),
						String.format("Exception in FilterChain execution:%s --- Filter: %s, Req: %s, Ext: %s",
								e.toString(), filter.getName(), requestId, this.parentExtension));
			}
		}

		return filterAction;
	}

	@Override
	public int size() {
		return filters.size();
	}

	@Override
	public void destroy() {
		for (QAntExtensionFilter filter : this.filters) {
			filter.destroy();
		}
		this.filters.clear();
	}
}
