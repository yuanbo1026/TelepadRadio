package com.technisat.radiotheque.metadataretriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

public class MetadataRetriverAsyncTask extends AsyncTask<String, Integer, String> {
	
	private String mUrl;
	private IMetadataCallback mCallback;

	public MetadataRetriverAsyncTask(String url, IMetadataCallback callback){
		mUrl = url;
		mCallback = callback;
	}
	
	@Override
	protected String doInBackground(String... params) {
//		FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
//		String metadata = null;
//		
//		try{					
//			mmr.setDataSource(mUrl);
//			metadata = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ICY_METADATA);
//		} catch(IllegalArgumentException e) {
//			//Log.e("nexxoo", "MetadataRetriver.GetDataSource reports error: "+e );
//		}
//		
//		mmr.release();
		
		IcyMetaDataRetriever it = null;
		String result = null;
		try {
			URL url = new URL(mUrl);
			if (url != null){
				it = new IcyMetaDataRetriever( url );
				if (!it.isError()){
					result = it.getArtist() + " - " + it.getTitle();
//					Log.d("Nexxoo", "Meta for " + mUrl + " is " + result);
				}
					
			}
			
		} catch (MalformedURLException e) {
//			Log.e("Nexxoo", "MalformedURLException: " + e.getMessage());
		} catch (IOException e) {
//			Log.e("Nexxoo", "IOException: " + e.getMessage());
		} catch (Exception e){
//			Log.e("Nexxoo", "Exception: " + e.getMessage());
		}
		
		it = null;
	
		return result;
		
//		return metadata;
	}
	
	
	
	@Override
	protected void onPostExecute(String result) {
//		if(result != null){
//			mCallback.onFinish(cutOutTitleAndArtist(result), mUrl);
//		} else {
//			mCallback.onFinish(result, mUrl);
//		}
		mCallback.onFinish(result, mUrl);
	}

}
