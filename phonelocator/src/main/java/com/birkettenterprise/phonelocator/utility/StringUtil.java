package com.birkettenterprise.phonelocator.utility;

public class StringUtil {
	public static boolean isNullOrWhiteSpace(
			final String string) {
		return string == null || string.length() == 0 || string.trim().length() == 0;
	}
}
