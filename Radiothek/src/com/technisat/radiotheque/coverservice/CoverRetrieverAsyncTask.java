package com.technisat.radiotheque.coverservice;

import android.os.AsyncTask;
import android.util.Log;

import com.technisat.radiotheque.entity.CoverUrls;

public class CoverRetrieverAsyncTask extends AsyncTask<String, Integer, CoverUrls> {
	
	private IAWSHandle mCallback;
	private String mMeta;
	
	public CoverRetrieverAsyncTask(String meta, IAWSHandle callback){
		mCallback = callback;
		mMeta = meta;
	}
	
	@Override
	protected CoverUrls doInBackground(String... params) {
		AWS aws = new AWS();
		CoverUrls cu = aws.getCover(mMeta);
		if(cu == null){
			// TODO: call next Cover Service or return default Cover
			Log.d("Nexxoo", "we got no cover from amazon...");
		}
		
		return cu;
	}
	
	@Override
	protected void onPostExecute(CoverUrls result) {
		if (mCallback != null) { 
			mCallback.onFinishLoading(result);
		}
	}
}