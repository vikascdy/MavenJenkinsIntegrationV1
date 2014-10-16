package com.edifecs.epp.security.utils;

public class SecurityUtil {
	
	/**
	 * convert a hex string to a byte array
	 * @param s a hex string
	 * @return the byte array
	 */
	public static byte[] hexStringToByteArray(String s) throws IllegalArgumentException{
		if (s == null) return null;
		int len = s.length();
		if ((len & 1) != 0) throw new IllegalArgumentException();
		s = s.toUpperCase();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return b;
	}

	/**
	 * convert a byte array to a hex string
	 * @param b the byte array
	 * @return the hex string
	 */
	public static String byteArrayToHexString(byte[] b) {
		if (b == null) return null;
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}
	
}
