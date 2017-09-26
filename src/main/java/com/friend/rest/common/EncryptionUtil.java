package com.friend.rest.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class EncryptionUtil {
	private static final int SALT_BYTES = 20;

	public static String hashMessage(String message) {
		String saltedMessage = message;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hash = md.digest(saltedMessage.getBytes());

			StringBuffer sb = new StringBuffer();
			for (byte b : hash) {
				sb.append(String.format("%02x", new Object[] { Byte.valueOf(b) }));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
		}
		throw new IllegalStateException("No SHA-256 in MessageDiagest.");
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = Integer.toHexString(bts[i] & 0xFF);
			if (tmp.length() == 1) {
				des = des + "0";
			}
			des = des + tmp;
		}
		return des;
	}

	public static String sha256(String psd) throws Exception {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(psd.getBytes("GBK"));
		String strDes = bytes2Hex(md.digest());
		return strDes;
	}
}
