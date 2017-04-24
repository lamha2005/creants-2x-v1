package com.creants.creants_2x.socket.gate.entities;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LamHM
 *
 */
public class QAntObjectLite {
	private Map<String, QAntDataWrapper> dataHolder;


	public QAntObjectLite() {
		dataHolder = new ConcurrentHashMap<String, QAntDataWrapper>();
	}


	public static QAntObjectLite newInstance() {
		return new QAntObjectLite();
	}


	@SuppressWarnings("unchecked")
	public Collection<Boolean> getBoolArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Boolean>) o.getObject();
	}


	public Byte getByte(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Byte) o.getObject();
	}


	@SuppressWarnings("unchecked")
	public Collection<Double> getDoubleArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Double>) o.getObject();
	}


	public Float getFloat(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Float) o.getObject();
	}


	@SuppressWarnings("unchecked")
	public Collection<Float> getFloatArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Float>) o.getObject();
	}


	@SuppressWarnings("unchecked")
	public Collection<Integer> getIntArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Integer>) o.getObject();
	}


	public Long getLong(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Long) o.getObject();
	}


	public Short getShort(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Short) o.getObject();
	}


	@SuppressWarnings("unchecked")
	public Collection<Short> getShortArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Short>) o.getObject();
	}


	@SuppressWarnings("unchecked")
	public Collection<String> getUtfStringArray(String key) {
		QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<String>) o.getObject();
	}

}
