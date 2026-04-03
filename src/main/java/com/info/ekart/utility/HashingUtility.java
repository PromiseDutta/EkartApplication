package com.info.ekart.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This is a utility class used to convert a string into its corresponding
 * hashed format so that we can store the hashed data and not the actual data
 * 
 * This technique is used for storing the secured information about user like
 * password, pin, CVV.
 * 
 * @author ETA_JAVA
 *
 */
public class HashingUtility {

	/**
	 * This method is to hash the given data using a hashing algorithm.
	 * 
	 * @param data
	 * 
	 * @return hashed data
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public static String getHashValue(String data) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(data.getBytes());

		byte[] byteData = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {

			String hex = Integer.toHexString(0xff & byteData[i]);

			if (hex.length() == 1)
				hexString.append('0');

			hexString.append(hex);
		}

		return hexString.toString();
	}
}