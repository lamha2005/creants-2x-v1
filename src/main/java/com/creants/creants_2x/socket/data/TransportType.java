package com.creants.creants_2x.socket.data;

public enum TransportType {
	TCP("TCP", 0, "Tcp"), UDP("UDP", 1, "Udp"), BLUEBOX("BLUEBOX", 2, "BlueBox");

	String name;

	private TransportType(String s, int n, String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "(" + this.name + ")";
	}

	public static TransportType fromName(String name) {
		TransportType[] values;
		for (int length = (values = values()).length, i = 0; i < length; ++i) {
			final TransportType tt = values[i];
			if (tt.name.equalsIgnoreCase(name)) {
				return tt;
			}
		}
		throw new IllegalArgumentException("There is no TransportType definition for the requested type: " + name);
	}
}
