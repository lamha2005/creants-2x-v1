package com.creants.creants_2x.socket.util;

import java.nio.ByteBuffer;

/**
 * @author LamHM
 *
 */
public class ByteUtils {
	private static final int HEX_BYTES_PER_LINE = 16;
	private static final char TAB = '\t';
	private static final String NEW_LINE;
	private static final char DOT = '.';

	static {
		NEW_LINE = System.getProperty("line.separator");
	}

	public static byte[] resizeByteArray(final byte[] source, final int pos, final int size) {
		final byte[] tmpArray = new byte[size];
		System.arraycopy(source, pos, tmpArray, 0, size);
		return tmpArray;
	}

	public static String fullHexDump(final ByteBuffer buffer, final int bytesPerLine) {
		return fullHexDump(buffer.array(), bytesPerLine);
	}

	public static String fullHexDump(final ByteBuffer buffer) {
		return fullHexDump(buffer.array(), HEX_BYTES_PER_LINE);
	}

	public static String fullHexDump(final byte[] buffer) {
		return fullHexDump(buffer, HEX_BYTES_PER_LINE);
	}

	public static String fullHexDump(final byte[] buffer, final int bytesPerLine) {
		final StringBuilder sb = new StringBuilder("Binary size: ").append(buffer.length).append("\n");
		final StringBuilder hexLine = new StringBuilder();
		final StringBuilder chrLine = new StringBuilder();
		int index = 0;
		int count = 0;
		do {
			final byte currByte = buffer[index];
			final String hexByte = Integer.toHexString(currByte & 0xFF);
			if (hexByte.length() == 1) {
				hexLine.append("0");
			}
			hexLine.append(hexByte.toUpperCase()).append(" ");
			final char currChar = (currByte >= 33 && currByte <= 126) ? ((char) currByte) : DOT;
			chrLine.append(currChar);
			if (++count == bytesPerLine) {
				count = 0;
				sb.append((CharSequence) hexLine).append(TAB).append((CharSequence) chrLine).append(ByteUtils.NEW_LINE);
				hexLine.delete(0, hexLine.length());
				chrLine.delete(0, chrLine.length());
			}
		} while (++index < buffer.length);
		if (count != 0) {
			for (int j = bytesPerLine - count; j > 0; --j) {
				hexLine.append("   ");
				chrLine.append(" ");
			}
			sb.append((CharSequence) hexLine).append(TAB).append((CharSequence) chrLine).append(ByteUtils.NEW_LINE);
		}
		return sb.toString();
	}
}
