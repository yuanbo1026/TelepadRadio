package com.technisat.radiotheque.main;

public class MainMenuButton {
	
	private String mCaption;
	private int mIconResource;
	private int mId;
	
	public MainMenuButton(String caption, int iconres, int id){
		mCaption = caption;
		mIconResource = iconres;
		mId = id;
	}

	public String getCaption() {
		return mCaption;
	}

	public int getIconResource() {
		return mIconResource;
	}

	public int getId() {
		return mId;
	}
}