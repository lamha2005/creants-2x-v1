package com.creants.creants_2x.socket.gate.entities;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author LamHM
 *
 */
public interface IQAntObject {
	QAntDataWrapper get(final String key);


	boolean isNull(String key);


	boolean containsKey(String key);


	boolean removeElement(String key);


	void put(final String key, final QAntDataWrapper wrappedObject);


	Iterator<Map.Entry<String, QAntDataWrapper>> iterator();


	Set<String> getKeys();


	int size();


	byte[] toBinary();


	String toJson();


	String getDump();


	/**
	 * Lấy chi tiết dump theo cấu trúc của CreantsObject
	 */
	String getDump(boolean noFormat);


	String getHexDump();


	Boolean getBool(String key);


	Byte getByte(String key);


	Integer getUnsignedByte(String key);


	Short getShort(String key);


	Integer getInt(String key);


	Long getLong(String key);


	Float getFloat(String key);


	Double getDouble(String key);


	String getUtfString(String key);


	String getText(String key);


	Collection<Boolean> getBoolArray(String key);


	byte[] getByteArray(String key);


	Collection<Integer> getUnsignedByteArray(String key);


	Collection<Short> getShortArray(String key);


	Collection<Integer> getIntArray(String key);


	Collection<Long> getLongArray(String key);


	Collection<Float> getFloatArray(String key);


	Collection<Double> getDoubleArray(String key);


	Collection<String> getUtfStringArray(String key);


	IQAntArray getCASArray(String key);


	IQAntObject getQAntObject(String key);


	void putNull(String key);


	void putBool(String key, boolean value);


	void putByte(String key, byte value);


	void putShort(String key, short value);


	void putInt(String key, int value);


	void putLong(String key, long value);


	void putFloat(String key, float value);


	void putDouble(String key, double value);


	void putUtfString(String key, String value);


	void putText(String key, String value);


	void putBoolArray(String key, Collection<Boolean> value);


	void putByteArray(String key, byte[] value);


	void putShortArray(String key, Collection<Short> value);


	void putIntArray(String key, Collection<Integer> value);


	void putLongArray(String key, Collection<Long> value);


	void putFloatArray(String key, Collection<Float> value);


	void putDoubleArray(String key, Collection<Double> value);


	void putUtfStringArray(String key, Collection<String> value);


	void putQAntArray(String key, IQAntArray value);


	void putQAntObject(String key, IQAntObject value);

}
