package com.technisat.radiotheque.tracking;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.technisat.radiotheque.android.Misc;

public class TechniTracker {
	
	private static final BigInteger SNR_MIN_TECHNIPAD_10G = new BigInteger(
			"10071945888");
	private static final BigInteger SNR_MIN_TECHNIPAD_MINI = new BigInteger(
			"10005235691");

	private static final String PREF_SALT = "salt";

	public static boolean isDeviceWithApp(Context ctx) {
		String serial = getSerialNumberAsString();

		if (serial != null && isBuildByTechni()) {
			try {
				BigInteger serialInt = new BigInteger(serial);
				return serialInt.compareTo(SNR_MIN_TECHNIPAD_10G) == 1
						|| serialInt.compareTo(SNR_MIN_TECHNIPAD_MINI) == 1;
			} catch (NumberFormatException e) {
				// serial number is not a number
				return false;
			}

		}

		return false;
	}

	
	private static boolean isBuildByTechni() {
		return Build.MODEL != null
				&& (Build.MODEL.equals("TPad_10") 
						 || Build.MODEL.equals("TPad"))
						 || Build.MODEL.equals("TechniPad 10")
						 || Build.MODEL.equals("TechniPad_7T")
						 || Build.MODEL.equals("TPhone4")
						 || Build.MODEL.equals("TPhone5")
						 || Build.MODEL.equals("AQIFON_4")
						 || Build.MODEL.equals("AQIPAD_7")
						 || Build.MODEL.equals("AQIPAD_7G")
						 || Build.MODEL.equals("VPad_10G");
	}
	
	
	/**
	 * check if the device is build by TechniSat
	 * 
	 * @return true or false
	 */
	public static boolean isBuildByTechniSat() {
		return Build.MODEL != null
				&& (Build.MODEL.equals("TPad_10") 
				 || Build.MODEL.equals("TPad"))
				 || Build.MODEL.equals("TechniPad 10")
				 || Build.MODEL.equals("TechniPad_7T")
				 || Build.MODEL.equals("TPhone4")
				 || Build.MODEL.equals("TPhone5")
				 || Build.MODEL.equals("AQIFON_4")
				 || Build.MODEL.equals("AQIPAD_7")
				 || Build.MODEL.equals("AQIPAD_7G")
				 || Build.MODEL.equals("VPad_10G");
	}
	
	public static boolean isPad(){
		return Build.MODEL != null
				&& (Build.MODEL.equals("TPad_10") 
				 || Build.MODEL.equals("TPad"))
				 || Build.MODEL.equals("TechniPad 10")
				 || Build.MODEL.equals("TechniPad_7T")
				 || Build.MODEL.equals("AQIPAD_7")
				 || Build.MODEL.equals("AQIPAD_7G")
				 || Build.MODEL.equals("VPad_10G");
	}

	public static String getHashedDeviceSerial(Context ctx) {
		String serial = getSerialNumberAsString();
		String salt = getSalt(ctx);

		String both = serial.concat(salt);

		MessageDigest digest = null;
		String hash;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			digest.update(both.getBytes());

			hash = bytesToHexString(digest.digest());

			return hash;
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	private static String getSalt(Context ctx) {
		SharedPreferences prefs = ctx.getSharedPreferences("radiotheque.pref",
				Context.MODE_PRIVATE);

		String salt = prefs.getString(PREF_SALT, null);
		if (salt == null) {
			salt = Integer.toString(Misc.getRandomNumber(Integer.MAX_VALUE));
			prefs.edit().putString(PREF_SALT, salt).commit();
		}

		return salt;

	}

	private static String getSerialNumberAsString() {
		return Build.SERIAL;
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
	/**
	 * check WiFi status
	 * @param ctx
	 * @return
	 */
	public static boolean wifiConnected(Context ctx){
		ConnectivityManager connManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (mWifi.isConnected()) {
		    return true;
		}
		else
			return false;
	}

}
