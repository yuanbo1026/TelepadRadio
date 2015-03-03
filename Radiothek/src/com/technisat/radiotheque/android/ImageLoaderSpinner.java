package com.technisat.radiotheque.android;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class ImageLoaderSpinner implements ImageLoadingListener {
	
	private final ProgressBar mSpinner;
	
	public ImageLoaderSpinner(ProgressBar pb){
		mSpinner = pb;
	}
	
	@Override
    public void onLoadingStarted(String imageUri, View view) {
		mSpinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
    	mSpinner.setVisibility(View.GONE);    	
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
    	mSpinner.setVisibility(View.GONE);
    }

	@Override
	public void onLoadingCancelled(String imageUri, View view) {
		mSpinner.setVisibility(View.GONE);
	}

}
