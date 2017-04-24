package com.creants.creants_2x.socket.gate.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author LamHM
 *
 */
public class QAntArrayLite {
	private List<QAntDataWrapper> dataHolder;


	public QAntArrayLite() {
		dataHolder = new ArrayList<QAntDataWrapper>();
	}


	public static QAntArrayLite newInstance() {
		return new QAntArrayLite();
	}


	@SuppressWarnings("unchecked")
	public Collection<Boolean> getBoolArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Boolean>) wrapper.getObject() : null;
	}


	public Byte getByte(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Byte) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	public Collection<Double> getDoubleArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Double>) wrapper.getObject() : null;
	}


	public Float getFloat(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Float) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	public Collection<Float> getFloatArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Float>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	public Collection<Integer> getIntArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Integer>) wrapper.getObject() : null;
	}


	public Long getLong(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Long) wrapper.getObject() : null;
	}


	public Short getShort(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Short) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	public Collection<Short> getShortArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Short>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	public Collection<String> getUtfStringArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<String>) wrapper.getObject() : null;
	}
}
