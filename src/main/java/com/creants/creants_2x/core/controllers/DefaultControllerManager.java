package com.creants.creants_2x.core.controllers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author LamHa
 *
 *         Tham khảo set controller BitSwarmEngine.configureControllers()
 */
public class DefaultControllerManager implements IControllerManager {
	protected String name;
	protected ConcurrentMap<Byte, IController> controllers;

	public DefaultControllerManager() {
		controllers = new ConcurrentHashMap<Byte, IController>();
		SystemController systemController = new SystemController();
		ExtensionController extensionController = new ExtensionController();

		systemController.setThreadPoolSize(1);
		systemController.setMaxQueueSize(50);
		extensionController.setThreadPoolSize(1);
		extensionController.setMaxQueueSize(50);

		controllers.put((byte) 0, systemController);
		controllers.put((byte) 1, extensionController);
	}

	// private void configureControllers() throws ClassNotFoundException,
	// InstantiationException, IllegalAccessException {
	// final List<ControllerConfig> cfgs =
	// this.configuration.getControllerConfigs();
	// for (final ControllerConfig controllerConfig : cfgs) {
	// final Class<?> controllerClass =
	// Class.forName(controllerConfig.getClassName());
	// final IController controller =
	// (IController)controllerClass.newInstance();
	// controller.setId(controllerConfig.getId());
	// controller.setThreadPoolSize(controllerConfig.getThreadPoolSize());
	// controller.setMaxQueueSize(controllerConfig.getMaxRequestQueueSize());
	// this.controllerManager.addController(controller.getId(), controller);
	// }
	// }

	@Override
	public void init(Object o) {
		startAllControllers();
	}

	@Override
	public void destroy(Object o) {
		shutDownAllControllers();
		controllers = null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void handleMessage(Object message) {
	}

	@Override
	public void addController(byte id, IController controller) {
		controllers.putIfAbsent(id, controller);
	}

	@Override
	public IController getControllerById(byte id) {
		return controllers.get(id);
	}

	@Override
	public void removeController(byte id) {
		controllers.remove(id);
	}

	private synchronized void shutDownAllControllers() {
		for (IController controller : controllers.values()) {
			controller.destroy(null);
		}
	}

	private synchronized void startAllControllers() {
		for (IController controller : controllers.values()) {
			controller.init(null);
		}
	}

}
