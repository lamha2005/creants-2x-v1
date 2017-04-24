package com.creants.creants_2x.socket.gate.entities;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Lamhm
 *
 */
public interface IQAntArray {
	boolean contains(Object obj);


	Iterator<QAntDataWrapper> iterator();


	Object getElementAt(int index);


	QAntDataWrapper get(int index);


	void removeElementAt(int index);


	int size();


	byte[] toBinary();


	String toJson();


	String getHexDump();


	String getDump();


	String getDump(boolean noFormat);


	void addNull();


	void addBool(boolean value);


	void addByte(byte value);


	void addShort(short value);


	void addInt(int value);


	void addLong(long value);


	void addFloat(float value);


	void addDouble(double value);


	void addUtfString(String value);


	void addText(String value);


	void addBoolArray(Collection<Boolean> value);


	void addByteArray(byte[] value);


	void addShortArray(Collection<Short> value);


	void addIntArray(Collection<Integer> value);


	void addLongArray(Collection<Long> value);


	void addFloatArray(Collection<Float> value);


	void addDoubleArray(Collection<Double> value);


	void addUtfStringArray(Collection<String> value);


	void addQAntArray(IQAntArray value);


	void addQAntObject(IQAntObject value);


	void add(QAntDataWrapper wrappedObject);


	boolean isNull(int index);


	Boolean getBool(int index);


	Byte getByte(int index);


	Integer getUnsignedByte(int index);


	Short getShort(int index);


	Integer getInt(int index);


	Long getLong(int index);


	Float getFloat(int index);


	Double getDouble(int index);


	String getUtfString(int index);


	String getText(int index);


	Collection<Boolean> getBoolArray(int index);


	byte[] getByteArray(int index);


	Collection<Integer> getUnsignedByteArray(int index);


	Collection<Short> getShortArray(int index);


	Collection<Integer> getIntArray(int index);


	Collection<Long> getLongArray(int index);


	Collection<Float> getFloatArray(int index);


	Collection<Double> getDoubleArray(int index);


	Collection<String> getUtfStringArray(int index);


	IQAntArray getQAntArray(int index);


	IQAntObject getQAntObject(int index);
}
