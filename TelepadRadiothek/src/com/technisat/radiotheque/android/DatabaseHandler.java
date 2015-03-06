package com.technisat.radiotheque.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.technisat.radiotheque.entity.Station;

/**
 * A class that creates an intern SQLite database to handle
 * history and favorite Stations the user listens to.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "stations.db";
	private final static int DATABASE_VERSION = 2;
	
	private final static String STATION_TABLE = "stations";
	
	private final static String STATION_ID ="_id";
	private final static String STATION_ISFAVORITE = "isFavorite";
	private final static String STATION_ISHISTORY = "isHistory";
	private final static String STATION_URL = "url";
	private final static String STATION_NAME = "name";
	private final static String STATION_LOGO_S = "logo_s";
	private final static String STATION_LOGO_M = "logo_m";
	private final static String STATION_LOGO_L = "logo_l";
	
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createDB ="CREATE TABLE IF NOT EXISTS "+ STATION_TABLE+" ("+
				STATION_ID +" INTEGER PRIMARY KEY, "+
				STATION_ISFAVORITE+" INTEGER DEFAULT 0, "+
				STATION_ISHISTORY+" INTEGER DEFAULT 0, " +
				STATION_URL + " TEXT NOT NULL," +
				STATION_NAME + " TEXT NOT NULL," +
				STATION_LOGO_S + " TEXT," +
				STATION_LOGO_M + " TEXT," +
				STATION_LOGO_L + " TEXT" +
				")";
		db.execSQL(createDB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ STATION_TABLE);
		onCreate(db);
	}
	
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+ STATION_TABLE);
		onCreate(db);
	}
	
	/**
	 * Determines wether a given Station should be added or updated into database
	 * @param station
	 */
	public void insertStation(Station station){
		if (isInDB(station)){
			updateStationInDB(station);
		} else {
			addStationToDB(station);
		}
	}
	
	/**
	 * Basically the isPlaying flag to true and calls {@link DatabaseHandler.insertStation}
	 * to add or update the given Station into database
	 * @param station
	 */
	public void addStationToHistory(Station station){
		Station s = station;
		s.setPlaying(true);
		insertStation(station);
	}
	
	/**
	 * Adds a new Station into database
	 * @param station
	 */
	private void addStationToDB(Station station){
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(STATION_ID, station.getId());		
		values.put(STATION_ISFAVORITE, station.isFav());
		values.put(STATION_ISHISTORY, station.isPlaying());
		values.put(STATION_URL, station.getStationUrl());
		values.put(STATION_NAME, station.getStationName());
		values.put(STATION_LOGO_S, station.getStationLogoSmall());
		values.put(STATION_LOGO_M, station.getStationLogoMedium());
		values.put(STATION_LOGO_L, station.getStationLogoLarge());
		
		db.insert(STATION_TABLE, null, values);
		db.close();
	}
	
	/**
	 * Updates a Station in the database by Station.ID
	 * @param station
	 * @return the number of rows affected
	 */
	private int updateStationInDB(Station station){
		int result = 0;
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(STATION_ID, station.getId());		
		values.put(STATION_ISFAVORITE, station.isFav());
		if (station.isPlaying()){
			values.put(STATION_ISHISTORY, "1");
		}
		
		result = db.update(STATION_TABLE, values, STATION_ID+"=?", new String[]{String.valueOf(station.getId())});
		db.close();
		
		return result;
	}
	
	/**
	 * 
	 * @param station
	 * @return if Station is in database
	 */
	public boolean isInDB(Station station){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(STATION_TABLE, null, STATION_ID+"=?", new String[] { String.valueOf(station.getId()) }, null,
				null, null);
		
		return cursor.moveToFirst();
		
	}
	
	/**
	 * 
	 * @param station
	 * @return if Station is set to favorite
	 */
	public boolean isFav(Station station){
		if (isInDB(station)){
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(STATION_TABLE, null, STATION_ID+"=?", new String[] { String.valueOf(station.getId()) }, null,
					null, null);
			int index = cursor.getColumnIndex(STATION_ISFAVORITE);
			if (cursor.moveToFirst())			
				return cursor.getInt(index) != 0;
			else
				return false;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param station
	 * @return if Station is set to history
	 */
	public boolean isHistory(Station station){
		if (isInDB(station)){
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(STATION_TABLE, null, STATION_ID+"=?", new String[] { String.valueOf(station.getId()) }, null,
					null, null);
			int index = cursor.getColumnIndex(STATION_ISHISTORY);
			if (cursor.moveToFirst())			
				return cursor.getInt(index) != 0;
			else
				return false;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return List with all Stations flaged as Favorite
	 */
	public List<Station> getAllFavoriteStations(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		
		cursor = db.query(STATION_TABLE, null, STATION_ISFAVORITE+"=?", new String[] { "1" }, null,
				null, null);
		
		List<Station> stationList = new ArrayList<Station>();
		cursor.moveToFirst();
		int idIndex = cursor.getColumnIndex(STATION_ID);
		int stationUrlIndex = cursor.getColumnIndex(STATION_URL);
		int stationNameIndex = cursor.getColumnIndex(STATION_NAME);
		int logoSIndex = cursor.getColumnIndex(STATION_LOGO_S);
		int logoMIndex = cursor.getColumnIndex(STATION_LOGO_M);
		int logoLIndex = cursor.getColumnIndex(STATION_LOGO_L);
		int favIndex = cursor.getColumnIndex(STATION_ISFAVORITE);
		
		if(cursor.getCount()!=0){
			do{
				Station station = new Station(
						cursor.getLong(idIndex),
						cursor.getString(stationUrlIndex),
						cursor.getString(stationNameIndex)
						);

				station.setStationLogoLarge(cursor.getString(logoLIndex));
				station.setStationLogoMedium(cursor.getString(logoMIndex));
				station.setStationLogoSmall(cursor.getString(logoSIndex));
				station.setFav(cursor.getInt(favIndex) != 0);
				
				stationList.add(station);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		this.close();
		
		return stationList;
	}
	
	public List<Station> getAllHistoryStations(){
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = null;
		
		cursor = db.query(STATION_TABLE, null, STATION_ISHISTORY+"=?", new String[] { "1" }, null,
				null, null);
		
		List<Station> stationList = new ArrayList<Station>();
		cursor.moveToFirst();
		int idIndex = cursor.getColumnIndex(STATION_ID);
		int stationUrlIndex = cursor.getColumnIndex(STATION_URL);
		int stationNameIndex = cursor.getColumnIndex(STATION_NAME);
		int logoSIndex = cursor.getColumnIndex(STATION_LOGO_S);
		int logoMIndex = cursor.getColumnIndex(STATION_LOGO_M);
		int logoLIndex = cursor.getColumnIndex(STATION_LOGO_L);
		int favIndex = cursor.getColumnIndex(STATION_ISFAVORITE);
		
		if(cursor.getCount()!=0){
			do{
				Station station = new Station(
						cursor.getLong(idIndex),
						cursor.getString(stationUrlIndex),
						cursor.getString(stationNameIndex)
						);

				station.setStationLogoLarge(cursor.getString(logoLIndex));
				station.setStationLogoMedium(cursor.getString(logoMIndex));
				station.setStationLogoSmall(cursor.getString(logoSIndex));
				station.setFav(cursor.getInt(favIndex) != 0);
				
				stationList.add(station);
			} while(cursor.moveToNext());
		}
		
		cursor.close();
		this.close();
		
		return stationList;
	}
}