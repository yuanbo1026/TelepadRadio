package com.technisat.radiotheque.android;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.technisat.radiotheque.entity.Station;

public class Logger {
	Station s;
	String data;
	
	public static void saveDataToLogFile(String data){
		
		String zielDatei = "Log.csv";
		File root = Environment.getExternalStorageDirectory();
		File myDir = new File(root.getAbsolutePath() + "/RadiothekLogs");
		myDir.mkdirs();
		File file = new File (myDir, zielDatei);
		
		BufferedWriter bW;
		try {
			bW = new BufferedWriter(new FileWriter(file, true));
			
			bW.write(data);
			bW.newLine();
			
			bW.flush();
			bW.close();
			
			Log.d("nexxoo","saved log in /Radiothek/log.csv");
		} catch (IOException e) {
		}
	}
	
	public static void saveStationToLogFile(Station station){
		String zielDatei = "Log.csv";
		File root = Environment.getExternalStorageDirectory();
		File myDir = new File(root.getAbsolutePath() + "/RadiothekLogs");
		myDir.mkdirs();
		File file = new File (myDir, zielDatei);
		
		BufferedWriter bW;
		try {
			bW = new BufferedWriter(new FileWriter(file, true));
			
			bW.write(station.getStationName()+";");
			bW.write(station.getStationUrl()+";");
			bW.write(station.getMetadata()+";");
			bW.write(station.getId()+";");
			bW.write(station.getStationLogoLarge()+";");
			bW.write(station.getCoverUrlSmall()+";");
			bW.write(station.getCoverUrlMedium()+";");
			bW.write(station.getCoverUrlLarge()+";");
			bW.write(station.getBuyLinkAmazon()+";");
			bW.write(station.getBuyLinkGoogle());
			bW.newLine();
			
            bW.flush();
            bW.close();
            
            Log.d("nexxoo","saved log in /Radiothek/log.csv");
		} catch (IOException e) {
		}
	}
}
