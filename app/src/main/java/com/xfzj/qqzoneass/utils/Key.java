package com.xfzj.qqzoneass.utils;

public class Key {

	public static int Refresh;


	/**
	 * @param args
	 */
	public static String g_tk(String str) {

		int hash = 5381;

		for (int i = 0, len = str.length(); i < len; ++i) {
			hash += (hash << 5) + (int) str.charAt(i);
		}
		return (hash & 0x7fffffff) + "";
	}

}
