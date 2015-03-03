package com.technisat.radiotheque.http.post;

import java.util.List;

import org.apache.http.NameValuePair;

import android.os.AsyncTask;

public class NexxooHttpJsonRequestAsynTask extends AsyncTask<String, Integer, String>{

	private String mWebservice;
	private String mBasicAuth;
	private IWebServiceResponse mCallback;
	
	private int mTask;
	private String mToken;
	private List<NameValuePair> mAdditionalParams;
	
	public NexxooHttpJsonRequestAsynTask(String webservice, String basicAuth, 
			int task, String token, List<NameValuePair> additionalParams, 
			IWebServiceResponse callback){
		mWebservice = webservice;
		mCallback = callback;
		mBasicAuth = basicAuth;
		
		mTask = task;
		mToken = token;
		mAdditionalParams = additionalParams;
	}
	
	@Override
	protected String doInBackground(String... params) {		
		NexxooHttpJsonRequest request = new NexxooHttpJsonRequest(mBasicAuth);		
		return request.performJsonRequest(mWebservice, mTask, mToken, mAdditionalParams);
	}

	@Override
	protected void onPostExecute(String result) {
		mCallback.onReceivedResponse(result);
	}

}
