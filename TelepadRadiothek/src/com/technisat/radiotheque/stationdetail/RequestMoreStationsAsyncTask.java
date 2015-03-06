package com.technisat.radiotheque.stationdetail;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;

public class RequestMoreStationsAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private Station mStation;
	private IRequestMoreStations mCallback;
	private List<Station> mResultList;
	
	public RequestMoreStationsAsyncTask(Context ctx, Station station, IRequestMoreStations callback){
		mCallback = callback;
		mStation = station;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		if (mStation == null)
			return false;
		
		String json = NexxooWebservice.getMoreStationsOfSameGenre(mStation.getId(), 10);
		
		mResultList = JSONParser.parseJsonToStationList(json);
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result && mCallback != null && mResultList != null){
			mCallback.onGotMoreStations(mResultList);
		}
	}
}
