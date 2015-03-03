package com.technisat.radiotheque.entity;

public class CoverUrls {
	
	private String mCoverSmall = null;
	private String mCoverMedium = null;
	private String mCoverLarge = null;
	private String mBuyLink = null;
	
	public void setCoverSmall(String url){
		mCoverSmall = url;
	}
	
	public void setCoverMedium(String url){
		mCoverMedium = url;
	}
	
	public void setCoverLarge(String url){
		mCoverLarge = url;
	}
	
	public void setBuyLink(String link){
		mBuyLink = link;
	}
	
	public String getCoverSmall(){
		return mCoverSmall;
	}
	
	public String getCoverMedium(){
		return mCoverMedium;
	}
	
	public String getCoverLarge(){
		return mCoverLarge;
	}
	
	public String getBuyLink(){
		return mBuyLink;
	}

}
