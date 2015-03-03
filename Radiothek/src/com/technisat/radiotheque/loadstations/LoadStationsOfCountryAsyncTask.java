package com.technisat.radiotheque.loadstations;

import java.util.List;

import android.os.AsyncTask;

import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;

public class LoadStationsOfCountryAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private ILoadStations mCallback;
	private String mIso;
	private int mCount;
	private List<Station> mStationList;
	
	public LoadStationsOfCountryAsyncTask(String iso, int count, ILoadStations callback){
		mCallback = callback;
		mIso = iso;
		mCount = count;

	}

	@Override
	protected Boolean doInBackground(String... params) {
		String json = NexxooWebservice.getStationsByCountry(mIso, mCount);
		
		mStationList = JSONParser.parseJsonToStationList(json);
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mCallback != null && mStationList != null){
			mCallback.onMoreStationsLoaded(mStationList);
		}
	}
	
	

}
