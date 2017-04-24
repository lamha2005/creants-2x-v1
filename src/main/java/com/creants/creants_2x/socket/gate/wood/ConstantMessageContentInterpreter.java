package com.creants.creants_2x.socket.gate.wood;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.creants.creants_2x.socket.gate.IMessageContentInterpreter;
import com.creants.creants_2x.socket.util.ConverterUtil;

/**
 * Class interpreter các field của message.<br>
 * Hỗ trợ trace thông tin của message
 * 
 * @author LamHa
 *
 */
public class ConstantMessageContentInterpreter implements InvocationHandler, IMessageContentInterpreter {

	private Map<Short, ConstantMessageContentInterpreter.Key> keyInfoMap = new HashMap<Short, ConstantMessageContentInterpreter.Key>();
	private Map<Short, String> commandMap = new HashMap<Short, String>();

	/**
	 * Interpreter cho class
	 * 
	 * @param constantClass
	 *            class cần interpreter
	 */
	public ConstantMessageContentInterpreter(Class<?>... constantClasses) {
		interpreter(constantClasses);
	}

	private void interpreter(Class<?>... constantClasses) {
		for (Class<?> clazz : constantClasses) {
			Object constantObject = Proxy.newProxyInstance(getClass().getClassLoader(), clazz.getInterfaces(), this);
			Field[] fields = clazz.getDeclaredFields();
			Field[] arrayOfField1;
			int j = (arrayOfField1 = fields).length;
			for (int i = 0; i < j; i++) {
				Field field = arrayOfField1[i];
				if (field.getName().startsWith("KEY")) {
					extractKeyInfo(constantObject, field);
				} else if (field.getName().startsWith("COMMAND")) {
					extractCommandInfo(constantObject, field);
				} else if (field.getName().startsWith("ERROR")) {

				}
			}
		}
	}

	@Override
	public void addInToInterpreter(Class<?>... clazz) {
		interpreter(clazz);
	}

	/**
	 * Extrac thông tin key
	 * 
	 * @param field
	 */
	private void extractKeyInfo(Object constantObject, Field field) {
		try {
			String fieldName = field.getName();
			String name = omitFirstWord(fieldName);
			Class<?> clazz = Byte.class;
			if (fieldName.startsWith("KEYS")) {
				clazz = String.class;
			} else if (fieldName.startsWith("KEYB")) {
				clazz = byte[].class;
			} else if (fieldName.startsWith("KEYR")) {
				clazz = Short.class;
			} else if (fieldName.startsWith("KEYI")) {
				clazz = Integer.class;
			} else if (fieldName.startsWith("KEYL")) {
				clazz = Long.class;
			} else if (fieldName.startsWith("KEYD")) {
				clazz = Double.class;
			} else if (fieldName.startsWith("KEYF")) {
				clazz = Float.class;
			} else if (fieldName.startsWith("KEYBL")) {
				clazz = Boolean.class;
			}

			keyInfoMap.put((Short) field.get(constantObject), new Key(name, clazz));
		} catch (Exception localException) {
		}
	}

	/**
	 * Extract thông tin của command
	 * 
	 * @param field
	 */
	private void extractCommandInfo(Object constantObject, Field field) {
		try {
			String fieldName = field.getName();
			String name = omitFirstWord(fieldName);
			commandMap.put((Short) field.get(constantObject), name);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bỏ đi ký tự "_"
	 * 
	 * @param fieldName
	 * @return
	 */
	private String omitFirstWord(String fieldName) {
		String name = fieldName.substring(fieldName.indexOf('_') + 1).toLowerCase();
		name = name.replace("_", " ");
		return name;
	}

	@Override
	public String interpetActionInGameValue(Short key, byte[] value) {
		ConstantMessageContentInterpreter.Key keyz = this.keyInfoMap.get(key);
		Class<?> clazz;
		if (keyz == null) {
			clazz = byte[].class;
		} else {
			clazz = keyz.clazz;
		}

		Object result;
		try {
			result = ConverterUtil.convertBytes2Object(clazz, value);
		} catch (Exception e) {
			return "<invalid data type> " + new String(value);
		}

		if ((result instanceof byte[])) {
			result = Arrays.toString((byte[]) result);
		}

		if (result == null) {
			return "null ";
		}

		// FIXME tùy theo game mà action khác nhau
		Map<String, String> actions = new HashMap<String, String>();
		actions.put(String.valueOf(55), "ACTION_START_AFTER_COUNTDOWN");
		actions.put(String.valueOf(56), "ACTION_START_GAME");
		actions.put(String.valueOf(57), "ACTION_AUTO_ARRANGE");
		actions.put(String.valueOf(58), "ACTION_FINISH");
		actions.put(String.valueOf(59), "ACTION_END_GAME");
		actions.put(String.valueOf(60), "ACTION_QUIT_GAME");
		actions.put(String.valueOf(61), "ACTION_READY");
		actions.put(String.valueOf(62), "ACTION_RECONNECT");
		String actionId = result.toString();
		if (actions.get(actionId) != null) {
			actionId = "[" + actionId + "] " + omitFirstWord(actions.get(actionId));
		}
		return actionId;
	}

	@Override
	public String interpetValue(Short key, byte[] value) {
		ConstantMessageContentInterpreter.Key keyz = this.keyInfoMap.get(key);
		Class<?> clazz;
		if (keyz == null) {
			clazz = byte[].class;
		} else {
			clazz = keyz.clazz;
		}

		Object result;
		try {
			result = ConverterUtil.convertBytes2Object(clazz, value);
		} catch (Exception e) {
			return "<invalid data type> " + new String(value);
		}

		if ((result instanceof byte[])) {
			result = Arrays.toString((byte[]) result);
		}

		if (result == null) {
			return "null ";
		}

		return result.toString();
	}

	@Override
	public String interpretKey(Short key) {
		ConstantMessageContentInterpreter.Key keyz = keyInfoMap.get(key);
		String name;
		if (keyz == null) {
			name = "Unknown";
		} else {
			name = keyz.name;
		}

		return name;
	}

	@Override
	public String interpretCommand(Short commandId) {
		String name = commandMap.get(commandId);
		return name == null ? "Unknown Command" : name;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}

	private class Key {
		String name;
		Class<?> clazz;

		public Key(String name, Class<?> clazz) {
			this.name = name;
			this.clazz = clazz;
		}
	}

}
