package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CountryList implements Parcelable {
	
	private List<Country> mCountryList;

	public CountryList(List<Country> countryList){
		mCountryList = countryList;
	}
	
	public void logGenreList(){
		for (Country c : mCountryList) {
			Log.d("Nexxoo", "Country: " + c.getCountry());
		}
	}
	
	public List<Country> getCountryList(){
		return mCountryList;
	}
	
	// Parcelling part
	public CountryList(Parcel in){
		mCountryList = new ArrayList<Country>();
        in.readList(mCountryList, Country.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(mCountryList);
    }
	
    public static final Parcelable.Creator<CountryList> CREATOR = new Parcelable.Creator<CountryList>() {
        public CountryList createFromParcel(Parcel in) {
            return new CountryList(in); 
        }

        public CountryList[] newArray(int size) {
            return new CountryList[size];
        }
    };
}
