package com.creants.creants_2x.socket.util;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Class convert các kiểu dữ liệu.
 * 
 * @author LamHa
 *
 */
public class ConverterUtil {
	public static String defaultEncoding = "UTF-8";

	public static Byte convertBytes2Byte(byte... data) {
		if (data == null || data.length < 1) {
			return null;
		}

		return new Byte(data[0]);
	}

	public static String convertBytes2String(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new String(data);
	}

	public static Boolean convertBytes2Boolean(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new Boolean(data[0] != 0);
	}

	public static Character convertBytes2Character(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new Character((char) (((data[0] & 0xFF) << 8) + ((data[1] & 0xFF) << 0)));
	}

	public static Short convertBytes2Short(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new Short((short) (((data[0] & 0xFF) << 8) + ((data[1] & 0xFF) << 0)));
	}

	public static Integer convertBytes2Integer(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new Integer(((data[0] & 0xFF) << 24) + ((data[1] & 0xFF) << 16) + ((data[2] & 0xFF) << 8)
				+ ((data[3] & 0xFF) << 0));
	}

	public static Long convertBytes2Long(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		return new Long((data[0] << 56) + ((data[1] & 0xFF) << 48) + ((data[2] & 0xFF) << 40) + ((data[3] & 0xFF) << 32)
				+ ((data[4] & 0xFF) << 24) + ((data[5] & 0xFF) << 16) + ((data[6] & 0xFF) << 8)
				+ ((data[7] & 0xFF) << 0));
	}

	public static Float convertBytes2Float(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		int result = ((data[0] & 0xFF) << 24) + ((data[1] & 0xFF) << 16) + ((data[2] & 0xFF) << 8)
				+ ((data[3] & 0xFF) << 0);
		return new Float(Float.intBitsToFloat(result));
	}

	public static Double convertBytes2Double(byte... data) {
		if (data == null || data.length == 0) {
			return null;
		}

		long result = (data[0] << 56) + ((data[1] & 0xFF) << 48) + ((data[2] & 0xFF) << 40) + ((data[3] & 0xFF) << 32)
				+ ((data[4] & 0xFF) << 24) + ((data[5] & 0xFF) << 16) + ((data[6] & 0xFF) << 8)
				+ ((data[7] & 0xFF) << 0);
		return new Double(Double.longBitsToDouble(result));
	}

	public static byte[] convertByte2Bytes(Byte value) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot convert null value");
		}

		return new byte[] { value.byteValue() };
	}

	public static byte[] convertString2Bytes(String value) {
		if (value == null) {
			return null;
		}

		try {
			return value.getBytes(defaultEncoding);
		} catch (UnsupportedEncodingException e) {
			// TODO log
			e.printStackTrace();
		}

		return value.getBytes();
	}

	public static byte[] convertBoolean2Bytes(Boolean value) {
		if (value == null) {
			return null;
		}

		return new byte[] { (byte) (value.booleanValue() ? 1 : 0) };
	}

	public static byte[] convertCharacter2Bytes(Character value) {
		if (value == null) {
			return null;
		}

		int v = value.charValue();
		return new byte[] { (byte) (v >>> 8 & 0xFF), (byte) (v >>> 0 & 0xFF) };
	}

	public static byte[] convertShort2Bytes(Short value) {
		if (value == null) {
			return null;
		}

		short v = value.shortValue();
		return new byte[] { (byte) (v >>> 8 & 0xFF), (byte) (v >>> 0 & 0xFF) };
	}

	public static byte[] convertInteger2Bytes(Integer value) {
		if (value == null) {
			return null;
		}

		int v = value.intValue();
		return new byte[] { (byte) (v >>> 24 & 0xFF), (byte) (v >>> 16 & 0xFF), (byte) (v >>> 8 & 0xFF),
				(byte) (v >>> 0 & 0xFF) };
	}

	public static byte[] convertLong2Bytes(Long value) {
		if (value == null) {
			return null;
		}

		long v = value.longValue();
		return new byte[] { (byte) (v >>> 56), (byte) (v >>> 48), (byte) (v >>> 40), (byte) (v >>> 32),
				(byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) (v >>> 0) };
	}

	public static byte[] convertFloat2Bytes(Float value) {
		if (value == null) {
			return null;
		}

		int v = Float.floatToIntBits(value.floatValue());
		return new byte[] { (byte) (v >>> 24 & 0xFF), (byte) (v >>> 16 & 0xFF), (byte) (v >>> 8 & 0xFF),
				(byte) (v >>> 0 & 0xFF) };
	}

	public static byte[] convertDouble2Bytes(Double value) {
		if (value == null) {
			return null;
		}

		long v = Double.doubleToLongBits(value.doubleValue());
		return new byte[] { (byte) (v >>> 56), (byte) (v >>> 48), (byte) (v >>> 40), (byte) (v >>> 32),
				(byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) (v >>> 0) };
	}

	/**
	 * Convert object sang byte array. <br>
	 * Hạn chế dùng hàm này nếu có thể (Hit performance).
	 * 
	 * @param value
	 *            đối tượng cần convert
	 */
	public static final byte[] convertObject2Bytes(Object value) {
		if (value == null) {
			throw new IllegalArgumentException("Cannot convert null value");
		}

		if ((value instanceof byte[])) {
			return (byte[]) value;
		}

		if (value instanceof String) {
			try {
				return ((String) value).getBytes(defaultEncoding);
			} catch (UnsupportedEncodingException e) {
				// TODO log
				throw new IllegalArgumentException("UnsupportedEncodingException:" + value.getClass().getName());
			}
		}

		if ((value instanceof Boolean)) {
			return new byte[] { (byte) (((Boolean) value).booleanValue() ? 1 : 0) };
		}

		if ((value instanceof Byte)) {
			return new byte[] { ((Byte) value).byteValue() };
		}

		if ((value instanceof Short)) {
			short v = ((Short) value).shortValue();

			return new byte[] { (byte) (v >>> 8 & 0xFF), (byte) (v >>> 0 & 0xFF) };
		}

		if ((value instanceof Character)) {
			int v = ((Character) value).charValue();

			return new byte[] { (byte) (v >>> 8 & 0xFF), (byte) (v >>> 0 & 0xFF) };
		}

		if ((value instanceof Integer)) {
			int v = ((Integer) value).intValue();
			return new byte[] { (byte) (v >>> 24 & 0xFF), (byte) (v >>> 16 & 0xFF), (byte) (v >>> 8 & 0xFF),
					(byte) (v >>> 0 & 0xFF) };
		}

		if ((value instanceof Long)) {
			long v = ((Long) value).longValue();
			return new byte[] { (byte) (v >>> 56), (byte) (v >>> 48), (byte) (v >>> 40), (byte) (v >>> 32),
					(byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) (v >>> 0) };
		}

		if ((value instanceof Float)) {
			int v = Float.floatToIntBits(((Float) value).floatValue());
			return new byte[] { (byte) (v >>> 24 & 0xFF), (byte) (v >>> 16 & 0xFF), (byte) (v >>> 8 & 0xFF),
					(byte) (v >>> 0 & 0xFF) };
		}

		if ((value instanceof Double)) {
			long v = Double.doubleToLongBits(((Double) value).doubleValue());
			return new byte[] { (byte) (v >>> 56), (byte) (v >>> 48), (byte) (v >>> 40), (byte) (v >>> 32),
					(byte) (v >>> 24), (byte) (v >>> 16), (byte) (v >>> 8), (byte) (v >>> 0) };
		}

		throw new IllegalArgumentException("Unknown data type:" + value.getClass().getName());
	}

	/**
	 * Chỉ dành cho mục đích trace thông tin message
	 * 
	 * @param clazz
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static final <T> T convertBytes2Object(Class<T> clazz, byte... data) {
		if ((data == null) || (data.length == 0)) {
			return null;
		}
		try {
			if (clazz.equals(byte[].class)) {
				return (T) data;
			}
			if (clazz.equals(String.class)) {
				return (T) convertBytes2String(data);
			}
			if (clazz.equals(Boolean.class)) {
				return (T) convertBytes2Boolean(data);
			}
			if (clazz.equals(Byte.class)) {
				return (T) new Byte(data[0]);
			}
			if (clazz.equals(Short.class)) {
				return (T) convertBytes2Short(data);
			}
			if (clazz.equals(Character.class)) {
				return (T) convertBytes2Character(data);
			}
			if (clazz.equals(Integer.class)) {
				return (T) convertBytes2Integer(data);
			}
			if (clazz.equals(Long.class)) {
				return (T) convertBytes2Long(data);
			}
			if (clazz.equals(Float.class)) {
				return (T) convertBytes2Float(data);
			}
			if (clazz.equals(Double.class)) {
				return (T) convertBytes2Double(data);
			}
		} catch (RuntimeException e) {
			throw new IllegalArgumentException(e);
		}

		throw new IllegalArgumentException("Unsupport data type:" + clazz.getName());
	}

	
	public static void main(String[] args) {
//		System.out.println("hello world");
		System.out.println(Arrays.toString(ConverterUtil.convertLong2Bytes(-4567L)) );
//		System.out.println(ConverterUtil.convertBytes2Long(new byte[]{-1,-1,-1,-1,-1,-1,-8,48}));
	}
}
