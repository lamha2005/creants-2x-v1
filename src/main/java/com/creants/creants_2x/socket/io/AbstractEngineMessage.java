package com.creants.creants_2x.socket.io;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author LamHa
 *
 */
public class AbstractEngineMessage {
	protected short id;
	protected IQAntObject content;
	protected Map<String, Object> attributes;

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public IQAntObject getContent() {
		return content;
	}

	public void setContent(IQAntObject content) {
		this.content = content;
	}

	public Object getAttribute(final String key) {
		Object attr = null;
		if (attributes != null) {
			attr = attributes.get(key);
		}
		return attr;
	}

	public void setAttribute(final String key, final Object attribute) {
		if (attributes == null) {
			attributes = new ConcurrentHashMap<String, Object>();
		}
		this.attributes.put(key, attribute);
	}
}
