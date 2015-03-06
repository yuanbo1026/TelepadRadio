package com.technisat.radiotheque.loadstations;

import java.util.List;

import android.os.AsyncTask;

import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;

public class LoadStationsOfGenreAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private ILoadStations mCallback;
	private long mGenreId;
	private int mCount;
	private List<Station> mStationList;
	
	public LoadStationsOfGenreAsyncTask(long genreId, int count, ILoadStations callback){
		mCallback = callback;
		mGenreId = genreId;
		mCount = count;

	}

	@Override
	protected Boolean doInBackground(String... params) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		String json = NexxooWebservice.getStationsByGenre(mGenreId, mCount);
		
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
