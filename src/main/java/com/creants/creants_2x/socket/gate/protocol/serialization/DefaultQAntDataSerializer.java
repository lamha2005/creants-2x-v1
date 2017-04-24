package com.creants.creants_2x.socket.gate.protocol.serialization;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.creants.creants_2x.socket.exception.QAntCodecException;
import com.creants.creants_2x.socket.gate.entities.QAntArray;
import com.creants.creants_2x.socket.gate.entities.QAntArrayLite;
import com.creants.creants_2x.socket.gate.entities.QAntDataType;
import com.creants.creants_2x.socket.gate.entities.QAntDataWrapper;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObjectLite;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author LamHM
 *         http://docs2x.smartfoxserver.com/api-docs/javadoc/server/index.html?com/smartfoxserver/v2/entities/data/class-use/SFSObject.html
 *         http://zfull.net/software/jetbrains-webstorm-9-full-cong-cu-bien-tap-html-css-va-javascript/
 *         http://checkman.io/blog/creating-a-javascript-library/
 *         http://docs2x.smartfoxserver.com/api-docs/cpp-doc/class_sfs2_x_1_1_entities_1_1_data_1_1_i_s_f_s_object.html
 *
 */
public class DefaultQAntDataSerializer implements IQAntDataSerializer {
	private static DefaultQAntDataSerializer instance;
	private static final int BUFFER_CHUNK_SIZE = 512;

	static {
		DefaultQAntDataSerializer.instance = new DefaultQAntDataSerializer();
	}


	public static DefaultQAntDataSerializer getInstance() {
		return DefaultQAntDataSerializer.instance;
	}


	private DefaultQAntDataSerializer() {
	}


	public int getUnsignedByte(byte b) {
		return 0xFF & b;
	}


	@Override
	public String array2json(List<Object> array) {
		return JSONArray.fromObject((Object) array).toString();
	}


	@Override
	public IQAntArray binary2array(byte[] data) {
		if (data.length < 3) {
			throw new IllegalStateException(
					"Can't decode an CASArray. Byte data is insufficient. Size: " + data.length + " bytes");
		}
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();
		return decodeCASArray(buffer);
	}


	private IQAntArray decodeCASArray(ByteBuffer buffer) {
		IQAntArray casArray = QAntArray.newInstance();
		byte headerBuffer = buffer.get();
		if (headerBuffer != QAntDataType.QANT_ARRAY.getTypeID()) {
			throw new IllegalStateException("Invalid CASDataType. Expected: " + QAntDataType.QANT_ARRAY.getTypeID()
					+ ", found: " + headerBuffer);
		}

		short size = buffer.getShort();
		if (size < 0) {
			throw new IllegalStateException("Can't decode CASArray. Size is negative = " + size);
		}
		try {
			for (int i = 0; i < size; ++i) {
				QAntDataWrapper decodedObject = decodeObject(buffer);
				if (decodedObject == null) {
					throw new IllegalStateException("Could not decode CASArray item at index: " + i);
				}
				casArray.add(decodedObject);
			}
		} catch (Exception codecError) {
			throw new IllegalArgumentException(codecError.getMessage());
		}
		return casArray;
	}


	@Override
	public IQAntObject binary2object(byte[] data) {
		if (data.length < 3) {
			throw new IllegalStateException(
					"Can't decode an CASObject. Byte data is insufficient. Size: " + data.length + " bytes");
		}
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.flip();
		return decodeQAntObject(buffer);
	}


	private IQAntObject decodeQAntObject(ByteBuffer buffer) {
		IQAntObject qAntObject = QAntObject.newInstance();
		byte headerBuffer = buffer.get();
		if (headerBuffer != QAntDataType.QANT_OBJECT.getTypeID()) {
			throw new IllegalStateException("Invalid CASDataType. Expected: " + QAntDataType.QANT_OBJECT.getTypeID()
					+ ", found: " + headerBuffer);
		}
		short size = buffer.getShort();
		if (size < 0) {
			throw new IllegalStateException("Can't decode QAntObject. Size is negative = " + size);
		}
		try {
			for (int i = 0; i < size; ++i) {
				short keySize = buffer.getShort();
				if (keySize < 0 || keySize > 255) {
					throw new IllegalStateException("Invalid CASObject key length. Found = " + keySize);
				}
				byte[] keyData = new byte[keySize];
				buffer.get(keyData, 0, keyData.length);
				String key = new String(keyData);
				QAntDataWrapper decodedObject = decodeObject(buffer);
				if (decodedObject == null) {
					throw new IllegalStateException("Could not decode value for key: " + keyData);
				}
				qAntObject.put(key, decodedObject);
			}
		} catch (Exception codecError) {
			throw new IllegalArgumentException(codecError.getMessage());
		}
		return qAntObject;
	}


	@Override
	public IQAntArray json2array(String jsonStr) {
		if (jsonStr.length() < 2) {
			throw new IllegalStateException(
					"Can't decode CASObject. JSON String is too short. Len: " + jsonStr.length());
		}
		JSONArray jsa = JSONArray.fromObject((Object) jsonStr);
		return decodeCASArray(jsa);
	}


	private IQAntArray decodeCASArray(JSONArray jsa) {
		IQAntArray casArray = (IQAntArray) QAntArrayLite.newInstance();
		for (Object value : jsa) {
			QAntDataWrapper decodedObject = decodeJsonObject(value);
			if (decodedObject == null) {
				throw new IllegalStateException("(json2sfarray) Could not decode value for object: " + value);
			}
			casArray.add(decodedObject);
		}
		return casArray;
	}


	@Override
	public IQAntObject json2object(String jsonStr) {
		if (jsonStr.length() < 2) {
			throw new IllegalStateException(
					"Can't decode CASObject. JSON String is too short. Len: " + jsonStr.length());
		}

		JSONObject jso = JSONObject.fromObject((Object) jsonStr);
		return decodeCASObject(jso);
	}


	private IQAntObject decodeCASObject(JSONObject jso) {
		IQAntObject CASObject = (IQAntObject) QAntObjectLite.newInstance();
		for (Object key : jso.keySet()) {
			Object value = jso.get(key);
			QAntDataWrapper decodedObject = decodeJsonObject(value);
			if (decodedObject == null) {
				throw new IllegalStateException("(json2CASobj) Could not decode value for key: " + key);
			}
			CASObject.put((String) key, decodedObject);
		}
		return CASObject;
	}


	private QAntDataWrapper decodeJsonObject(Object o) {
		if (o instanceof Integer) {
			return new QAntDataWrapper(QAntDataType.INT, o);
		}
		if (o instanceof Long) {
			return new QAntDataWrapper(QAntDataType.LONG, o);
		}
		if (o instanceof Double) {
			return new QAntDataWrapper(QAntDataType.DOUBLE, o);
		}
		if (o instanceof Boolean) {
			return new QAntDataWrapper(QAntDataType.BOOL, o);
		}
		if (o instanceof String) {
			String value = (String) o;
			QAntDataType type = QAntDataType.UTF_STRING;
			if (value.length() > 32767) {
				type = QAntDataType.TEXT;
			}
			return new QAntDataWrapper(type, o);
		}

		if (o instanceof JSONObject) {
			JSONObject jso = (JSONObject) o;
			if (jso.isNullObject()) {
				return new QAntDataWrapper(QAntDataType.NULL, null);
			}
			return new QAntDataWrapper(QAntDataType.QANT_OBJECT, decodeCASObject(jso));
		}

		if (o instanceof JSONArray) {
			return new QAntDataWrapper(QAntDataType.QANT_ARRAY, decodeCASArray((JSONArray) o));
		}

		throw new IllegalArgumentException(
				String.format("Unrecognized DataType while converting JSONObject 2 CASObject. Object: %s, Type: %s", o,
						(o == null) ? "null" : o.getClass()));

	}


	@Override
	public QAntObject resultSet2object(ResultSet rset) throws SQLException {
		ResultSetMetaData metaData = rset.getMetaData();
		QAntObject CASo = new QAntObject();
		if (rset.isBeforeFirst()) {
			rset.next();
		}
		for (int col = 1; col <= metaData.getColumnCount(); ++col) {
			String colName = metaData.getColumnLabel(col);
			int type = metaData.getColumnType(col);
			Object rawDataObj = rset.getObject(col);
			if (rawDataObj != null) {
				if (type == 0) {
					CASo.putNull(colName);
				} else if (type == 16) {
					CASo.putBool(colName, rset.getBoolean(col));
				} else if (type == 91) {
					CASo.putLong(colName, rset.getDate(col).getTime());
				} else if (type == 6 || type == 3 || type == 8 || type == 7) {
					CASo.putDouble(colName, rset.getDouble(col));
				} else if (type == 4 || type == -6 || type == 5) {
					CASo.putInt(colName, rset.getInt(col));
				} else if (type == -1 || type == 12 || type == 1) {
					CASo.putUtfString(colName, rset.getString(col));
				} else if (type == -9 || type == -16 || type == -15) {
					CASo.putUtfString(colName, rset.getNString(col));
				} else if (type == 93) {
					CASo.putLong(colName, rset.getTimestamp(col).getTime());
				} else if (type == -5) {
					CASo.putLong(colName, rset.getLong(col));
				} else if (type == -4) {
					byte[] binData = getBlobData(colName, rset.getBinaryStream(col));
					if (binData != null) {
						CASo.putByteArray(colName, binData);
					}
				} else if (type == 2004) {
					Blob blob = rset.getBlob(col);
					CASo.putByteArray(colName, blob.getBytes(0L, (int) blob.length()));
				} else {
					// logger.info("Skipping Unsupported SQL TYPE: " + type
					// + ", Column:" + colName);
				}
			}
		}
		return CASo;
	}


	private byte[] getBlobData(String colName, InputStream stream) {
		BufferedInputStream bis = new BufferedInputStream(stream);
		byte[] bytes = null;
		try {
			bytes = new byte[bis.available()];
			bis.read(bytes);
		} catch (IOException ex) {
			// logger.warn("CASObject serialize error. Failed reading BLOB
			// data for column: " + colName);
			return bytes;
		} finally {
			// IOUtils.closeQuietly((InputStream) bis);
		}
		// IOUtils.closeQuietly((InputStream) bis);
		return bytes;
	}


	@Override
	public QAntArray resultSet2array(ResultSet rset) throws SQLException {
		QAntArray CASa = new QAntArray();
		while (rset.next()) {
			CASa.addQAntObject(resultSet2object(rset));
		}
		return CASa;
	}


	@Override
	public byte[] object2binary(IQAntObject object) {
		ByteBuffer buffer = ByteBuffer.allocate(DefaultQAntDataSerializer.BUFFER_CHUNK_SIZE);
		buffer.put((byte) QAntDataType.QANT_OBJECT.getTypeID());
		buffer.putShort((short) object.size());
		return obj2bin(object, buffer);
	}


	private byte[] obj2bin(IQAntObject object, ByteBuffer buffer) {
		Set<String> keys = object.getKeys();
		for (String key : keys) {
			QAntDataWrapper wrapper = object.get(key);
			Object dataObj = wrapper.getObject();
			buffer = encodeQAntObjectKey(buffer, key);
			buffer = encodeObject(buffer, wrapper.getTypeId(), dataObj);
		}
		int pos = buffer.position();
		byte[] result = new byte[pos];
		buffer.flip();
		buffer.get(result, 0, pos);
		return result;
	}


	@Override
	public byte[] array2binary(IQAntArray array) {
		ByteBuffer buffer = ByteBuffer.allocate(DefaultQAntDataSerializer.BUFFER_CHUNK_SIZE);
		buffer.put((byte) QAntDataType.QANT_ARRAY.getTypeID());
		buffer.putShort((short) array.size());
		return arr2bin(array, buffer);
	}


	private byte[] arr2bin(IQAntArray array, ByteBuffer buffer) {
		Iterator<QAntDataWrapper> iter = array.iterator();
		while (iter.hasNext()) {
			QAntDataWrapper wrapper = (QAntDataWrapper) iter.next();
			buffer = encodeObject(buffer, wrapper.getTypeId(), wrapper.getObject());
		}

		int pos = buffer.position();
		byte[] result = new byte[pos];
		buffer.flip();
		buffer.get(result, 0, pos);
		return result;
	}


	@Override
	public String object2json(Map<String, Object> map) {
		return JSONObject.fromObject((Object) map).toString();
	}


	public void flattenObject(Map<String, Object> map, QAntObject casObj) {
		for (Iterator<Map.Entry<String, QAntDataWrapper>> it = casObj.iterator(); it.hasNext();) {
			Map.Entry<String, QAntDataWrapper> entry = (Map.Entry<String, QAntDataWrapper>) it.next();

			String key = (String) entry.getKey();
			QAntDataWrapper value = (QAntDataWrapper) entry.getValue();
			if (value.getTypeId() == QAntDataType.QANT_OBJECT) {
				Map<String, Object> newMap = new HashMap<String, Object>();

				map.put(key, newMap);

				flattenObject(newMap, (QAntObject) value.getObject());
			} else if (value.getTypeId() == QAntDataType.QANT_ARRAY) {
				List<Object> newList = new ArrayList<Object>();
				map.put(key, newList);
				flattenArray(newList, (QAntArray) value.getObject());
			} else {
				map.put(key, value.getObject());
			}
		}
	}


	public void flattenArray(List<Object> array, QAntArray casArray) {
		for (Iterator<QAntDataWrapper> it = casArray.iterator(); it.hasNext();) {
			QAntDataWrapper value = (QAntDataWrapper) it.next();
			if (value.getTypeId() == QAntDataType.QANT_OBJECT) {
				Map<String, Object> newMap = new HashMap<String, Object>();
				array.add(newMap);
				flattenObject(newMap, (QAntObject) value.getObject());
			} else if (value.getTypeId() == QAntDataType.QANT_ARRAY) {
				List<Object> newList = new ArrayList<Object>();
				array.add(newList);
				flattenArray(newList, (QAntArray) value.getObject());
			} else {
				array.add(value.getObject());
			}
		}
	}


	private QAntDataWrapper decodeObject(ByteBuffer buffer) throws QAntCodecException {
		QAntDataWrapper decodedObject = null;
		byte headerByte = buffer.get();
		if (headerByte == QAntDataType.NULL.getTypeID()) {
			decodedObject = binDecode_NULL(buffer);
		} else if (headerByte == QAntDataType.BOOL.getTypeID()) {
			decodedObject = binDecode_BOOL(buffer);
		} else if (headerByte == QAntDataType.BOOL_ARRAY.getTypeID()) {
			decodedObject = binDecode_BOOL_ARRAY(buffer);
		} else if (headerByte == QAntDataType.BYTE.getTypeID()) {
			decodedObject = binDecode_BYTE(buffer);
		} else if (headerByte == QAntDataType.BYTE_ARRAY.getTypeID()) {
			decodedObject = binDecode_BYTE_ARRAY(buffer);
		} else if (headerByte == QAntDataType.SHORT.getTypeID()) {
			decodedObject = binDecode_SHORT(buffer);
		} else if (headerByte == QAntDataType.SHORT_ARRAY.getTypeID()) {
			decodedObject = binDecode_SHORT_ARRAY(buffer);
		} else if (headerByte == QAntDataType.INT.getTypeID()) {
			decodedObject = binDecode_INT(buffer);
		} else if (headerByte == QAntDataType.INT_ARRAY.getTypeID()) {
			decodedObject = binDecode_INT_ARRAY(buffer);
		} else if (headerByte == QAntDataType.LONG.getTypeID()) {
			decodedObject = binDecode_LONG(buffer);
		} else if (headerByte == QAntDataType.LONG_ARRAY.getTypeID()) {
			decodedObject = binDecode_LONG_ARRAY(buffer);
		} else if (headerByte == QAntDataType.FLOAT.getTypeID()) {
			decodedObject = binDecode_FLOAT(buffer);
		} else if (headerByte == QAntDataType.FLOAT_ARRAY.getTypeID()) {
			decodedObject = binDecode_FLOAT_ARRAY(buffer);
		} else if (headerByte == QAntDataType.DOUBLE.getTypeID()) {
			decodedObject = binDecode_DOUBLE(buffer);
		} else if (headerByte == QAntDataType.DOUBLE_ARRAY.getTypeID()) {
			decodedObject = binDecode_DOUBLE_ARRAY(buffer);
		} else if (headerByte == QAntDataType.UTF_STRING.getTypeID()) {
			decodedObject = binDecode_UTF_STRING(buffer);
		} else if (headerByte == QAntDataType.TEXT.getTypeID()) {
			decodedObject = binDecode_TEXT(buffer);
		} else if (headerByte == QAntDataType.UTF_STRING_ARRAY.getTypeID()) {
			decodedObject = binDecode_UTF_STRING_ARRAY(buffer);
		} else if (headerByte == QAntDataType.QANT_ARRAY.getTypeID()) {
			buffer.position(buffer.position() - 1);
			decodedObject = new QAntDataWrapper(QAntDataType.QANT_ARRAY, decodeCASArray(buffer));
		} else {
			if (headerByte != QAntDataType.QANT_OBJECT.getTypeID()) {
				throw new QAntCodecException("Unknow CASDataType ID: " + headerByte);
			}
			buffer.position(buffer.position() - 1);
			IQAntObject CASObj = decodeQAntObject(buffer);
			QAntDataType type = QAntDataType.QANT_OBJECT;
			Object finalCASObj = CASObj;
			// if (CASObj.containsKey(CLASS_MARKER_KEY) &&
			// CASObj.containsKey(CLASS_FIELDS_KEY)) {
			// type = CASDataType.CLASS;
			// finalCASObj = CAS2pojo(CASObj);
			// }
			decodedObject = new QAntDataWrapper(type, finalCASObj);
		}

		return decodedObject;
	}


	@SuppressWarnings("unchecked")
	private ByteBuffer encodeObject(ByteBuffer buffer, QAntDataType typeId, Object object) {
		switch (typeId) {
			case NULL: {
				buffer = binEncode_NULL(buffer);
				break;
			}
			case BOOL: {
				buffer = binEncode_BOOL(buffer, (Boolean) object);
				break;
			}
			case BYTE: {
				buffer = binEncode_BYTE(buffer, (Byte) object);
				break;
			}
			case SHORT: {
				buffer = binEncode_SHORT(buffer, (Short) object);
				break;
			}
			case INT: {
				buffer = binEncode_INT(buffer, (Integer) object);
				break;
			}
			case LONG: {
				buffer = binEncode_LONG(buffer, (Long) object);
				break;
			}
			case FLOAT: {
				buffer = binEncode_FLOAT(buffer, (Float) object);
				break;
			}
			case DOUBLE: {
				buffer = binEncode_DOUBLE(buffer, (Double) object);
				break;
			}
			case UTF_STRING: {
				buffer = binEncode_UTF_STRING(buffer, (String) object);
				break;
			}
			case TEXT: {
				buffer = binEncode_TEXT(buffer, (String) object);
				break;
			}
			case BOOL_ARRAY: {
				buffer = binEncode_BOOL_ARRAY(buffer, (Collection<Boolean>) object);
				break;
			}
			case BYTE_ARRAY: {
				buffer = binEncode_BYTE_ARRAY(buffer, (byte[]) object);
				break;
			}
			case SHORT_ARRAY: {
				buffer = binEncode_SHORT_ARRAY(buffer, (Collection<Short>) object);
				break;
			}
			case INT_ARRAY: {
				buffer = binEncode_INT_ARRAY(buffer, (Collection<Integer>) object);
				break;
			}
			case LONG_ARRAY: {
				buffer = binEncode_LONG_ARRAY(buffer, (Collection<Long>) object);
				break;
			}
			case FLOAT_ARRAY: {
				buffer = binEncode_FLOAT_ARRAY(buffer, (Collection<Float>) object);
				break;
			}
			case DOUBLE_ARRAY: {
				buffer = binEncode_DOUBLE_ARRAY(buffer, (Collection<Double>) object);
				break;
			}
			case UTF_STRING_ARRAY: {
				buffer = binEncode_UTF_STRING_ARRAY(buffer, (Collection<String>) object);
				break;
			}
			case QANT_ARRAY: {
				buffer = addData(buffer, array2binary((IQAntArray) object));
				break;
			}
			case QANT_OBJECT: {
				buffer = addData(buffer, object2binary((IQAntObject) object));
				break;
			}
			default: {
				throw new IllegalArgumentException("Unrecognized type in CASObject serialization: " + typeId);
			}
		}
		return buffer;
	}


	private QAntDataWrapper binDecode_NULL(ByteBuffer buffer) {
		return new QAntDataWrapper(QAntDataType.NULL, null);
	}


	private QAntDataWrapper binDecode_BOOL(ByteBuffer buffer) throws QAntCodecException {
		byte boolByte = buffer.get();
		Boolean bool = null;
		if (boolByte == 0) {
			bool = new Boolean(false);
		} else {
			if (boolByte != 1) {
				throw new QAntCodecException("Error decoding Bool type. Illegal value: " + bool);
			}
			bool = new Boolean(true);
		}
		return new QAntDataWrapper(QAntDataType.BOOL, bool);
	}


	private QAntDataWrapper binDecode_BYTE(ByteBuffer buffer) {
		byte boolByte = buffer.get();
		return new QAntDataWrapper(QAntDataType.BYTE, boolByte);
	}


	private QAntDataWrapper binDecode_SHORT(ByteBuffer buffer) {
		short shortValue = buffer.getShort();
		return new QAntDataWrapper(QAntDataType.SHORT, shortValue);
	}


	private QAntDataWrapper binDecode_INT(ByteBuffer buffer) {
		int intValue = buffer.getInt();
		return new QAntDataWrapper(QAntDataType.INT, intValue);
	}


	private QAntDataWrapper binDecode_LONG(ByteBuffer buffer) {
		long longValue = buffer.getLong();
		return new QAntDataWrapper(QAntDataType.LONG, longValue);
	}


	private QAntDataWrapper binDecode_FLOAT(ByteBuffer buffer) {
		float floatValue = buffer.getFloat();
		return new QAntDataWrapper(QAntDataType.FLOAT, floatValue);
	}


	private QAntDataWrapper binDecode_DOUBLE(ByteBuffer buffer) {
		double doubleValue = buffer.getDouble();
		return new QAntDataWrapper(QAntDataType.DOUBLE, doubleValue);
	}


	private QAntDataWrapper binDecode_UTF_STRING(ByteBuffer buffer) throws QAntCodecException {
		short strLen = buffer.getShort();
		if (strLen < 0) {
			throw new QAntCodecException("Error decoding UtfString. Negative size: " + strLen);
		}
		byte[] strData = new byte[strLen];
		buffer.get(strData, 0, strLen);
		String decodedString = new String(strData);
		return new QAntDataWrapper(QAntDataType.UTF_STRING, decodedString);
	}


	private QAntDataWrapper binDecode_TEXT(ByteBuffer buffer) throws QAntCodecException {
		int strLen = buffer.getInt();
		if (strLen < 0) {
			throw new QAntCodecException("Error decoding UtfString. Negative size: " + strLen);
		}
		byte[] strData = new byte[strLen];
		buffer.get(strData, 0, strLen);
		String decodedString = new String(strData);
		return new QAntDataWrapper(QAntDataType.TEXT, decodedString);
	}


	private QAntDataWrapper binDecode_BOOL_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Boolean> array = new ArrayList<Boolean>();
		for (int j = 0; j < arraySize; ++j) {
			byte boolData = buffer.get();
			if (boolData == 0) {
				array.add(false);
			} else {
				if (boolData != 1) {
					throw new QAntCodecException("Error decoding BoolArray. Invalid bool value: " + boolData);
				}
				array.add(true);
			}
		}
		return new QAntDataWrapper(QAntDataType.BOOL_ARRAY, array);
	}


	private QAntDataWrapper binDecode_BYTE_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		int arraySize = buffer.getInt();
		if (arraySize < 0) {
			throw new QAntCodecException("Error decoding typed array size. Negative size: " + arraySize);
		}
		byte[] byteData = new byte[arraySize];
		buffer.get(byteData, 0, arraySize);
		return new QAntDataWrapper(QAntDataType.BYTE_ARRAY, byteData);
	}


	private QAntDataWrapper binDecode_SHORT_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Short> array = new ArrayList<Short>();
		for (int j = 0; j < arraySize; ++j) {
			short shortValue = buffer.getShort();
			array.add(shortValue);
		}
		return new QAntDataWrapper(QAntDataType.SHORT_ARRAY, array);
	}


	private QAntDataWrapper binDecode_INT_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Integer> array = new ArrayList<Integer>();
		for (int j = 0; j < arraySize; ++j) {
			int intValue = buffer.getInt();
			array.add(intValue);
		}

		return new QAntDataWrapper(QAntDataType.INT_ARRAY, array);
	}


	private QAntDataWrapper binDecode_LONG_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Long> array = new ArrayList<Long>();
		for (int j = 0; j < arraySize; ++j) {
			long longValue = buffer.getLong();
			array.add(longValue);
		}
		return new QAntDataWrapper(QAntDataType.LONG_ARRAY, array);
	}


	private QAntDataWrapper binDecode_FLOAT_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Float> array = new ArrayList<Float>();
		for (int j = 0; j < arraySize; ++j) {
			float floatValue = buffer.getFloat();
			array.add(floatValue);
		}
		return new QAntDataWrapper(QAntDataType.FLOAT_ARRAY, array);
	}


	private QAntDataWrapper binDecode_DOUBLE_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<Double> array = new ArrayList<Double>();
		for (int j = 0; j < arraySize; ++j) {
			double doubleValue = buffer.getDouble();
			array.add(doubleValue);
		}
		return new QAntDataWrapper(QAntDataType.DOUBLE_ARRAY, array);
	}


	private QAntDataWrapper binDecode_UTF_STRING_ARRAY(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = getTypeArraySize(buffer);
		List<String> array = new ArrayList<String>();
		for (int j = 0; j < arraySize; ++j) {
			short strLen = buffer.getShort();
			if (strLen < 0) {
				throw new QAntCodecException(
						"Error decoding UtfStringArray element. Element has negative size: " + strLen);
			}
			byte[] strData = new byte[strLen];
			buffer.get(strData, 0, strLen);
			array.add(new String(strData));
		}
		return new QAntDataWrapper(QAntDataType.UTF_STRING_ARRAY, array);
	}


	private short getTypeArraySize(ByteBuffer buffer) throws QAntCodecException {
		short arraySize = buffer.getShort();
		if (arraySize < 0) {
			throw new QAntCodecException("Error decoding typed array size. Negative size: " + arraySize);
		}
		return arraySize;
	}


	private ByteBuffer binEncode_NULL(ByteBuffer buffer) {
		return addData(buffer, new byte[1]);
	}


	private ByteBuffer binEncode_BOOL(ByteBuffer buffer, Boolean value) {
		if (value == null)
			return null;

		byte[] data = { (byte) QAntDataType.BOOL.getTypeID(), (byte) (value.booleanValue() ? 1 : 0) };
		return addData(buffer, data);
	}


	private ByteBuffer binEncode_BYTE(ByteBuffer buffer, Byte value) {
		byte[] data = { (byte) QAntDataType.BYTE.getTypeID(), value };
		return addData(buffer, data);
	}


	private ByteBuffer binEncode_SHORT(ByteBuffer buffer, Short value) {
		ByteBuffer buf = ByteBuffer.allocate(3);
		buf.put((byte) QAntDataType.SHORT.getTypeID());
		buf.putShort(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_INT(ByteBuffer buffer, Integer value) {
		ByteBuffer buf = ByteBuffer.allocate(5);
		buf.put((byte) QAntDataType.INT.getTypeID());
		buf.putInt(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_LONG(ByteBuffer buffer, Long value) {
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.put((byte) QAntDataType.LONG.getTypeID());
		buf.putLong(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_FLOAT(ByteBuffer buffer, Float value) {
		ByteBuffer buf = ByteBuffer.allocate(5);
		buf.put((byte) QAntDataType.FLOAT.getTypeID());
		buf.putFloat(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_DOUBLE(ByteBuffer buffer, Double value) {
		ByteBuffer buf = ByteBuffer.allocate(9);
		buf.put((byte) QAntDataType.DOUBLE.getTypeID());
		buf.putDouble(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_UTF_STRING(ByteBuffer buffer, String value) {
		byte[] stringBytes = value.getBytes();
		ByteBuffer buf = ByteBuffer.allocate(3 + stringBytes.length);
		buf.put((byte) QAntDataType.UTF_STRING.getTypeID());
		buf.putShort((short) stringBytes.length);
		buf.put(stringBytes);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_TEXT(ByteBuffer buffer, String value) {
		byte[] stringBytes = value.getBytes();
		ByteBuffer buf = ByteBuffer.allocate(5 + stringBytes.length);
		buf.put((byte) QAntDataType.TEXT.getTypeID());
		buf.putInt(stringBytes.length);
		buf.put(stringBytes);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_BOOL_ARRAY(ByteBuffer buffer, Collection<Boolean> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + value.size());
		buf.put((byte) QAntDataType.BOOL_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (boolean b : value) {
			buf.put((byte) (b ? 1 : 0));
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_BYTE_ARRAY(ByteBuffer buffer, byte[] value) {
		ByteBuffer buf = ByteBuffer.allocate(5 + value.length);
		buf.put((byte) QAntDataType.BYTE_ARRAY.getTypeID());
		buf.putInt(value.length);
		buf.put(value);
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_SHORT_ARRAY(ByteBuffer buffer, Collection<Short> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 2 * value.size());
		buf.put((byte) QAntDataType.SHORT_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (short item : value) {
			buf.putShort(item);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_INT_ARRAY(ByteBuffer buffer, Collection<Integer> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
		buf.put((byte) QAntDataType.INT_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (int item : value) {
			buf.putInt(item);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_LONG_ARRAY(ByteBuffer buffer, Collection<Long> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
		buf.put((byte) QAntDataType.LONG_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (long item : value) {
			buf.putLong(item);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_FLOAT_ARRAY(ByteBuffer buffer, Collection<Float> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 4 * value.size());
		buf.put((byte) QAntDataType.FLOAT_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (float item : value) {
			buf.putFloat(item);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_DOUBLE_ARRAY(ByteBuffer buffer, Collection<Double> value) {
		ByteBuffer buf = ByteBuffer.allocate(3 + 8 * value.size());
		buf.put((byte) QAntDataType.DOUBLE_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		for (double item : value) {
			buf.putDouble(item);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer binEncode_UTF_STRING_ARRAY(ByteBuffer buffer, Collection<String> value) {
		int stringDataLen = 0;
		byte[][] binStrings = new byte[value.size()][];
		int count = 0;
		for (String item : value) {
			byte[] binStr = item.getBytes();
			binStrings[count++] = binStr;
			stringDataLen += 2 + binStr.length;
		}
		ByteBuffer buf = ByteBuffer.allocate(3 + stringDataLen);
		buf.put((byte) QAntDataType.UTF_STRING_ARRAY.getTypeID());
		buf.putShort((short) value.size());
		byte[][] array;
		for (int length = (array = binStrings).length, i = 0; i < length; ++i) {
			byte[] binItem = array[i];
			buf.putShort((short) binItem.length);
			buf.put(binItem);
		}
		return addData(buffer, buf.array());
	}


	private ByteBuffer encodeQAntObjectKey(ByteBuffer buffer, String value) {
		ByteBuffer buf = ByteBuffer.allocate(2 + value.length());
		buf.putShort((short) value.length());
		buf.put(value.getBytes());
		return addData(buffer, buf.array());
	}


	private ByteBuffer addData(ByteBuffer buffer, byte[] newData) {
		if (buffer.remaining() < newData.length) {
			int newSize = DefaultQAntDataSerializer.BUFFER_CHUNK_SIZE;
			if (newSize < newData.length) {
				newSize = newData.length;
			}

			ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() + newSize);
			buffer.flip();
			newBuffer.put(buffer);
			buffer = newBuffer;
		}

		buffer.put(newData);
		return buffer;
	}

}
