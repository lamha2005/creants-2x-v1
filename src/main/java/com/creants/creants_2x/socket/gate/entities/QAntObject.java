// 
// Decompiled by Procyon v0.5.30
// 

package com.creants.creants_2x.socket.gate.entities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.creants.creants_2x.socket.gate.protocol.serialization.DefaultQAntDataSerializer;
import com.creants.creants_2x.socket.gate.protocol.serialization.DefaultObjectDumpFormatter;
import com.creants.creants_2x.socket.gate.protocol.serialization.IQAntDataSerializer;
import com.creants.creants_2x.socket.util.ByteUtils;

/**
 * @author LamHM
 *
 */
public class QAntObject implements IQAntObject {
	private Map<String, QAntDataWrapper> dataHolder;
	private IQAntDataSerializer serializer;


	public static QAntObject newFromBinaryData(byte[] bytes) {
		return (QAntObject) DefaultQAntDataSerializer.getInstance().binary2object(bytes);
	}


	public static IQAntObject newFromJsonData(String jsonStr) {
		return DefaultQAntDataSerializer.getInstance().json2object(jsonStr);
	}


	public static QAntObject newFromResultSet(ResultSet rset) throws SQLException {
		return DefaultQAntDataSerializer.getInstance().resultSet2object(rset);
	}


	public static QAntObject newInstance() {
		return new QAntObject();
	}


	public QAntObject() {
		dataHolder = new ConcurrentHashMap<String, QAntDataWrapper>();
		serializer = DefaultQAntDataSerializer.getInstance();
	}


	@Override
	public Iterator<Map.Entry<String, QAntDataWrapper>> iterator() {
		return dataHolder.entrySet().iterator();
	}


	@Override
	public boolean containsKey(final String key) {
		return dataHolder.containsKey(key);
	}


	@Override
	public boolean removeElement(final String key) {
		return dataHolder.remove(key) != null;
	}


	@Override
	public int size() {
		return dataHolder.size();
	}


	@Override
	public byte[] toBinary() {
		return serializer.object2binary(this);
	}


	@Override
	public String toJson() {
		return serializer.object2json(flatten());
	}


	@Override
	public String getDump() {
		if (size() == 0) {
			return "[ Empty CASObject ]";
		}
		return DefaultObjectDumpFormatter.prettyPrintDump(dump());
	}


	@Override
	public String getDump(final boolean noFormat) {
		if (!noFormat) {
			return dump();
		}
		return getDump();
	}


	private String dump() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append('{');
		for (final String key : getKeys()) {
			final QAntDataWrapper wrapper = get(key);
			buffer.append("(").append(wrapper.getTypeId().name().toLowerCase()).append(") ").append(key).append(": ");
			if (wrapper.getTypeId() == QAntDataType.QANT_OBJECT) {
				buffer.append(((QAntObject) wrapper.getObject()).getDump(false));
			} else if (wrapper.getTypeId() == QAntDataType.QANT_ARRAY) {
				buffer.append(((QAntArray) wrapper.getObject()).getDump(false));
			} else if (wrapper.getTypeId() == QAntDataType.BYTE_ARRAY) {
				buffer.append(DefaultObjectDumpFormatter.prettyPrintByteArray((byte[]) wrapper.getObject()));
			} else {
				buffer.append(wrapper.getObject());
			}
			buffer.append(';');
		}
		buffer.append('}');
		return buffer.toString();
	}


	@Override
	public String getHexDump() {
		return ByteUtils.fullHexDump(toBinary());
	}


	@Override
	public boolean isNull(final String key) {
		final QAntDataWrapper wrapper = dataHolder.get(key);
		return wrapper != null && wrapper.getTypeId() == QAntDataType.NULL;
	}


	@Override
	public QAntDataWrapper get(final String key) {
		return dataHolder.get(key);
	}


	@Override
	public Boolean getBool(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Boolean) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Boolean> getBoolArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Boolean>) o.getObject();
	}


	@Override
	public Byte getByte(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Byte) o.getObject();
	}


	@Override
	public byte[] getByteArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (byte[]) o.getObject();
	}


	@Override
	public Double getDouble(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Double) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Double> getDoubleArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Double>) o.getObject();
	}


	@Override
	public Float getFloat(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Float) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Float> getFloatArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Float>) o.getObject();
	}


	@Override
	public Integer getInt(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Integer) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Integer> getIntArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Integer>) o.getObject();
	}


	@Override
	public Set<String> getKeys() {
		return dataHolder.keySet();
	}


	@Override
	public Long getLong(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Long) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Long> getLongArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Long>) o.getObject();
	}


	@Override
	public IQAntArray getCASArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (IQAntArray) o.getObject();
	}


	@Override
	public IQAntObject getQAntObject(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (IQAntObject) o.getObject();
	}


	@Override
	public Short getShort(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Short) o.getObject();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<Short> getShortArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<Short>) o.getObject();
	}


	@Override
	public Integer getUnsignedByte(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return DefaultQAntDataSerializer.getInstance().getUnsignedByte((byte) o.getObject());
	}


	@Override
	public Collection<Integer> getUnsignedByteArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		final DefaultQAntDataSerializer serializer = DefaultQAntDataSerializer.getInstance();
		final Collection<Integer> intCollection = new ArrayList<Integer>();
		byte[] array;
		for (int length = (array = (byte[]) o.getObject()).length, i = 0; i < length; ++i) {
			final byte b = array[i];
			intCollection.add(serializer.getUnsignedByte(b));
		}
		return intCollection;
	}


	@Override
	public String getUtfString(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (String) o.getObject();
	}


	@Override
	public String getText(final String key) {
		return getUtfString(key);
	}


	@SuppressWarnings("unchecked")
	@Override
	public Collection<String> getUtfStringArray(final String key) {
		final QAntDataWrapper o = dataHolder.get(key);
		if (o == null) {
			return null;
		}
		return (Collection<String>) o.getObject();
	}


	@Override
	public void putBool(final String key, final boolean value) {
		putObj(key, value, QAntDataType.BOOL);
	}


	@Override
	public void putBoolArray(final String key, final Collection<Boolean> value) {
		putObj(key, value, QAntDataType.BOOL_ARRAY);
	}


	@Override
	public void putByte(final String key, final byte value) {
		putObj(key, value, QAntDataType.BYTE);
	}


	@Override
	public void putByteArray(final String key, final byte[] value) {
		putObj(key, value, QAntDataType.BYTE_ARRAY);
	}


	@Override
	public void putDouble(final String key, final double value) {
		putObj(key, value, QAntDataType.DOUBLE);
	}


	@Override
	public void putDoubleArray(final String key, final Collection<Double> value) {
		putObj(key, value, QAntDataType.DOUBLE_ARRAY);
	}


	@Override
	public void putFloat(final String key, final float value) {
		putObj(key, value, QAntDataType.FLOAT);
	}


	@Override
	public void putFloatArray(final String key, final Collection<Float> value) {
		putObj(key, value, QAntDataType.FLOAT_ARRAY);
	}


	@Override
	public void putInt(final String key, final int value) {
		putObj(key, value, QAntDataType.INT);
	}


	@Override
	public void putIntArray(final String key, final Collection<Integer> value) {
		putObj(key, value, QAntDataType.INT_ARRAY);
	}


	@Override
	public void putLong(final String key, final long value) {
		putObj(key, value, QAntDataType.LONG);
	}


	@Override
	public void putLongArray(final String key, final Collection<Long> value) {
		putObj(key, value, QAntDataType.LONG_ARRAY);
	}


	@Override
	public void putNull(final String key) {
		dataHolder.put(key, new QAntDataWrapper(QAntDataType.NULL, null));
	}


	@Override
	public void putQAntArray(final String key, final IQAntArray value) {
		putObj(key, value, QAntDataType.QANT_ARRAY);
	}


	@Override
	public void putQAntObject(final String key, final IQAntObject value) {
		putObj(key, value, QAntDataType.QANT_OBJECT);
	}


	@Override
	public void putShort(final String key, final short value) {
		putObj(key, value, QAntDataType.SHORT);
	}


	@Override
	public void putShortArray(final String key, final Collection<Short> value) {
		putObj(key, value, QAntDataType.SHORT_ARRAY);
	}


	@Override
	public void putUtfString(final String key, final String value) {
		putObj(key, value, QAntDataType.UTF_STRING);
	}


	@Override
	public void putText(final String key, final String value) {
		putObj(key, value, QAntDataType.TEXT);
	}


	@Override
	public void putUtfStringArray(final String key, final Collection<String> value) {
		putObj(key, value, QAntDataType.UTF_STRING_ARRAY);
	}


	@Override
	public void put(String key, final QAntDataWrapper wrappedObject) {
		putObj(key, wrappedObject, null);
	}


	@Override
	public String toString() {
		return "[QAntObject, size: " + size() + "]";
	}


	private void putObj(String key, Object value, QAntDataType typeId) {
		if (key == null) {
			throw new IllegalArgumentException("CASObject requires a non-null key for a 'put' operation!");
		}
		if (key.length() > 255) {
			throw new IllegalArgumentException("CASObject keys must be less than 255 characters!");
		}
		if (value == null) {
			throw new IllegalArgumentException(
					"CASObject requires a non-null value! If you need to add a null use the putNull() method.");
		}
		if (value instanceof QAntDataWrapper) {
			dataHolder.put(key, (QAntDataWrapper) value);
		} else {
			dataHolder.put(key, new QAntDataWrapper(typeId, value));
		}
	}


	private Map<String, Object> flatten() {
		Map<String, Object> map = new HashMap<String, Object>();
		DefaultQAntDataSerializer.getInstance().flattenObject(map, this);
		return map;
	}


	public static void main(String[] args) {
		QAntObject object = QAntObject.newInstance();
		object.putByte("c", (byte) 0);
		object.putShort("a", (short) 1);

		QAntObject param = new QAntObject();
		param.putUtfString("_token",
				"eyJhbGciOiJIUzI1NiJ9.eyJpZCI6IjIyIiwiZXhwIjoxNDkzNDUwNTQ4LCJpc3MiOiJhdXRoMCIsInR0bCI6ODY0MDAwMDAwfQ.EzdTCrV-jLn9K4WXiWctLuxHDY4A-s_N9zhNB5fiFSA");
		object.putQAntObject("p", param);

		byte[] binary = object.toBinary();
		System.out.println(binary.length);
		System.out.println(Arrays.toString(binary));

		QAntObject decode = QAntObject.newFromBinaryData(binary);
		IQAntObject qAntObject = decode.getQAntObject("p");
		System.out.println(qAntObject.getUtfString("_token"));

	}
}
