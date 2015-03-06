package com.technisat.radiotheque.stationlist.search;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.radiodb.JSONParser;
import com.technisat.radiotheque.radiodb.NexxooWebservice;

public class SearchAsyncTask extends AsyncTask<String, Integer, Boolean> {
	
	private String mQuery;
	private ISearchCallback mCallback;
	private List<Station> mResultList;
	
	public SearchAsyncTask(Context ctx, String query, ISearchCallback callback){
		mCallback = callback;
		mQuery = query;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		if (mQuery == null)
			return false;
		
		String json = NexxooWebservice.getStationsBySearchQuery(mQuery);
		
		mResultList = JSONParser.parseJsonToStationList(json);
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result && mCallback != null && mResultList != null){
			mCallback.onSearchDone(mResultList);
		}
	}

}
