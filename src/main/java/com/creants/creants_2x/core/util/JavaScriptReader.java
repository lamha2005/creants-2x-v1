package com.creants.creants_2x.core.util;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * @author LamHM
 *
 */
public class JavaScriptReader {
	private static JavaScriptReader inst;
	private Invocable invocable;


	public static JavaScriptReader getInstance() {
		if (inst == null) {
			inst = new JavaScriptReader();
		}

		return inst;
	}


	private JavaScriptReader() {

	}


	public JavaScriptReader init(String jsFile) throws FileNotFoundException, ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		engine.eval("var i = 2");
		engine.eval(new FileReader(jsFile));
		invocable = (Invocable) engine;
		return this;
	}


	public Invocable getInvocable() {
		return invocable;
	}


	public static void main(String[] args) {
		try {
			Invocable invocable2 = JavaScriptReader.getInstance().init("resources/script.js").getInvocable();
			// Object invokeFunction = invocable2.invokeFunction("fun1", "Peter
			// Parker");
			// System.out.println(invokeFunction);
			System.out.println("************************");
			System.out.println(invocable2.invokeFunction("fun2", "test"));
			System.out.println(invocable2.invokeFunction("fun2", "test"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}
