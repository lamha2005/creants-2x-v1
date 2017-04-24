// 
// Decompiled by Procyon v0.5.30
// 

package com.creants.creants_2x.socket.gate.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.creants.creants_2x.socket.gate.protocol.serialization.DefaultQAntDataSerializer;
import com.creants.creants_2x.socket.gate.protocol.serialization.DefaultObjectDumpFormatter;
import com.creants.creants_2x.socket.gate.protocol.serialization.IQAntDataSerializer;
import com.creants.creants_2x.socket.util.ByteUtils;

/**
 * @author LamHM
 *
 */
public class QAntArray implements IQAntArray {
	private IQAntDataSerializer serializer;
	private List<QAntDataWrapper> dataHolder;


	public QAntArray() {
		dataHolder = new ArrayList<QAntDataWrapper>();
		serializer = DefaultQAntDataSerializer.getInstance();
	}


	public static QAntArray newFromBinaryData(byte[] bytes) {
		return (QAntArray) DefaultQAntDataSerializer.getInstance().binary2array(bytes);
	}


	public static QAntArray newFromResultSet(ResultSet rset) throws SQLException {
		return DefaultQAntDataSerializer.getInstance().resultSet2array(rset);
	}


	public static QAntArray newFromJsonData(String jsonStr) {
		return (QAntArray) DefaultQAntDataSerializer.getInstance().json2array(jsonStr);
	}


	public static QAntArray newInstance() {
		return new QAntArray();
	}


	@Override
	public String getDump() {
		if (size() == 0) {
			return "[ Empty QAntArray ]";
		}
		return DefaultObjectDumpFormatter.prettyPrintDump(dump());
	}


	@Override
	public String getDump(boolean noFormat) {
		if (!noFormat) {
			return dump();
		}
		return getDump();
	}


	private String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		Object objDump = null;
		for (QAntDataWrapper wrappedObject : dataHolder) {
			if (wrappedObject.getTypeId() == QAntDataType.QANT_OBJECT) {
				objDump = ((IQAntObject) wrappedObject.getObject()).getDump(false);
			} else if (wrappedObject.getTypeId() == QAntDataType.QANT_ARRAY) {
				objDump = ((IQAntArray) wrappedObject.getObject()).getDump(false);
			} else if (wrappedObject.getTypeId() == QAntDataType.BYTE_ARRAY) {
				objDump = DefaultObjectDumpFormatter.prettyPrintByteArray((byte[]) wrappedObject.getObject());
			} else {
				objDump = wrappedObject.getObject();
			}
			sb.append(" (").append(wrappedObject.getTypeId().name().toLowerCase()).append(") ").append(objDump)
					.append(';');
		}
		if (size() > 0) {
			sb.delete(sb.length() - 1, sb.length());
		}
		sb.append('}');
		return sb.toString();
	}


	@Override
	public String getHexDump() {
		return ByteUtils.fullHexDump(toBinary());
	}


	@Override
	public byte[] toBinary() {
		return serializer.array2binary(this);
	}


	@Override
	public String toJson() {
		return DefaultQAntDataSerializer.getInstance().array2json(flatten());
	}


	@Override
	public boolean isNull(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null && wrapper.getTypeId() == QAntDataType.NULL;
	}


	@Override
	public QAntDataWrapper get(int index) {
		return dataHolder.get(index);
	}


	@Override
	public Boolean getBool(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Boolean) wrapper.getObject()) : null;
	}


	@Override
	public Byte getByte(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Byte) wrapper.getObject()) : null;
	}


	@Override
	public Integer getUnsignedByte(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? DefaultQAntDataSerializer.getInstance().getUnsignedByte((byte) wrapper.getObject())
				: null;
	}


	@Override
	public Short getShort(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Short) wrapper.getObject()) : null;
	}


	@Override
	public Integer getInt(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Integer) wrapper.getObject()) : null;
	}


	@Override
	public Long getLong(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Long) wrapper.getObject()) : null;
	}


	@Override
	public Float getFloat(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Float) wrapper.getObject()) : null;
	}


	@Override
	public Double getDouble(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((Double) wrapper.getObject()) : null;
	}


	@Override
	public String getUtfString(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((String) wrapper.getObject()) : null;
	}


	@Override
	public String getText(int index) {
		return getUtfString(index);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Boolean> getBoolArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Boolean>) wrapper.getObject() : null;
	}


	@Override
	public byte[] getByteArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (byte[]) ((wrapper != null) ? wrapper.getObject() : null);
	}


	@Override
	public Collection<Integer> getUnsignedByteArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		if (wrapper == null) {
			return null;
		}
		DefaultQAntDataSerializer serializer = DefaultQAntDataSerializer.getInstance();
		Collection<Integer> intCollection = new ArrayList<Integer>();
		byte[] array;
		for (int length = (array = (byte[]) wrapper.getObject()).length, i = 0; i < length; ++i) {
			byte b = array[i];
			intCollection.add(serializer.getUnsignedByte(b));
		}
		return intCollection;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Short> getShortArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Short>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> getIntArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Integer>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> getLongArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Long>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Float> getFloatArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Float>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Double> getDoubleArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<Double>) wrapper.getObject() : null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> getUtfStringArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return wrapper != null ? (Collection<String>) wrapper.getObject() : null;
	}


	@Override
	public IQAntArray getQAntArray(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((IQAntArray) wrapper.getObject()) : null;
	}


	@Override
	public IQAntObject getQAntObject(int index) {
		QAntDataWrapper wrapper = dataHolder.get(index);
		return (wrapper != null) ? ((IQAntObject) wrapper.getObject()) : null;
	}


	@Override
	public void addBool(boolean value) {
		addObject(value, QAntDataType.BOOL);
	}


	@Override
	public void addBoolArray(Collection<Boolean> value) {
		addObject(value, QAntDataType.BOOL_ARRAY);
	}


	@Override
	public void addByte(byte value) {
		addObject(value, QAntDataType.BYTE);
	}


	@Override
	public void addByteArray(byte[] value) {
		addObject(value, QAntDataType.BYTE_ARRAY);
	}


	@Override
	public void addDouble(double value) {
		addObject(value, QAntDataType.DOUBLE);
	}


	@Override
	public void addDoubleArray(Collection<Double> value) {
		addObject(value, QAntDataType.DOUBLE_ARRAY);
	}


	@Override
	public void addFloat(float value) {
		addObject(value, QAntDataType.FLOAT);
	}


	@Override
	public void addFloatArray(Collection<Float> value) {
		addObject(value, QAntDataType.FLOAT_ARRAY);
	}


	@Override
	public void addInt(int value) {
		addObject(value, QAntDataType.INT);
	}


	@Override
	public void addIntArray(Collection<Integer> value) {
		addObject(value, QAntDataType.INT_ARRAY);
	}


	@Override
	public void addLong(long value) {
		addObject(value, QAntDataType.LONG);
	}


	@Override
	public void addLongArray(Collection<Long> value) {
		addObject(value, QAntDataType.LONG_ARRAY);
	}


	@Override
	public void addNull() {
		addObject(null, QAntDataType.NULL);
	}


	@Override
	public void addQAntArray(IQAntArray value) {
		addObject(value, QAntDataType.QANT_ARRAY);
	}


	@Override
	public void addQAntObject(IQAntObject value) {
		addObject(value, QAntDataType.QANT_OBJECT);
	}


	@Override
	public void addShort(short value) {
		addObject(value, QAntDataType.SHORT);
	}


	@Override
	public void addShortArray(Collection<Short> value) {
		addObject(value, QAntDataType.SHORT_ARRAY);
	}


	@Override
	public void addUtfString(String value) {
		addObject(value, QAntDataType.UTF_STRING);
	}


	@Override
	public void addText(String value) {
		addObject(value, QAntDataType.TEXT);
	}


	@Override
	public void addUtfStringArray(Collection<String> value) {
		addObject(value, QAntDataType.UTF_STRING_ARRAY);
	}


	@Override
	public void add(QAntDataWrapper wrappedObject) {
		dataHolder.add(wrappedObject);
	}


	@Override
	public boolean contains(Object obj) {
		if (obj instanceof IQAntArray || obj instanceof IQAntObject) {
			throw new UnsupportedOperationException("ICASArray and ICASObject are not supported by this method.");
		}
		boolean found = false;
		Iterator<QAntDataWrapper> iter = dataHolder.iterator();
		while (iter.hasNext()) {
			Object item = iter.next().getObject();
			if (item.equals(obj)) {
				found = true;
				break;
			}
		}
		return found;
	}


	@Override
	public Object getElementAt(int index) {
		Object item = null;
		QAntDataWrapper wrapper = dataHolder.get(index);
		if (wrapper != null) {
			item = wrapper.getObject();
		}
		return item;
	}


	@Override
	public Iterator<QAntDataWrapper> iterator() {
		return dataHolder.iterator();
	}


	@Override
	public void removeElementAt(int index) {
		dataHolder.remove(index);
	}


	@Override
	public int size() {
		return dataHolder.size();
	}


	@Override
	public String toString() {
		return "[CASArray, size: " + size() + "]";
	}


	private void addObject(Object value, QAntDataType typeId) {
		dataHolder.add(new QAntDataWrapper(typeId, value));
	}


	private List<Object> flatten() {
		List<Object> list = new ArrayList<Object>();
		DefaultQAntDataSerializer.getInstance().flattenArray(list, this);
		return list;
	}
}
