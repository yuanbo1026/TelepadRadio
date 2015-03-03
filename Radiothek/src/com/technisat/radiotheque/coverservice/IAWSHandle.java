package com.technisat.radiotheque.coverservice;

import com.technisat.radiotheque.entity.CoverUrls;

public interface IAWSHandle {
	
	public void onFinishLoading(CoverUrls cu);
}