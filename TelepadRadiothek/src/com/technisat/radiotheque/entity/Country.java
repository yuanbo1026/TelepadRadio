package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Country implements Parcelable, IStationCoverUpdate, IStationMetadataUpdate {
	
	private long mId;
	private String mCountry = null;
	private String mIso = null;
	
	private List<Station> mStationList;
	private List<ICountry> mICountryList = new ArrayList<ICountry>();
	
	public Country(long id, String country, String iso){
		mId = id;
		mCountry = country;
		mIso = iso;
	}
	
	//Getter & Setter
	public long getId(){
		return mId;
	}
	
	public String getCountry(){
		return mCountry;
	}
	
	public String getIsoCode(){
		return mIso;
	}
	
	public List<Station> getsList() {
		return mStationList;
	}

	public void setStationList(List<Station> sList) {
		mStationList = sList;
	}

	// Parcelling part
		public Country(Parcel in){
	        mId = in.readLong();
	        mCountry = in.readString();      
	        mIso = in.readString();
	        mStationList = new ArrayList<Station>();
	        in.readList(mStationList, Station.class.getClassLoader());
	    }
		
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
	    public void writeToParcel(Parcel dest, int flags) {			
			dest.writeLong(mId);
			dest.writeString(mCountry);
			dest.writeString(mIso);
			dest.writeList(mStationList);
	    }
	    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {
	        public Country createFromParcel(Parcel in) {
	            return new Country(in); 
	        }

	        public Country[] newArray(int size) {
	            return new Country[size];
	        }
	    };
	    
	    public void listenToAllStations(){
			for(Station s : mStationList){
				s.addCoverListener(this);
				s.addMetadataListener(this);
			}
		}
		
		public void updateMetaDataForAllStations(){		
			for(Station s : mStationList){
				s.updateMetaData();
			}
		}
	    
	    public List<ICountry> getListenerList(){
			return mICountryList;
		}
		
		public void addListener(ICountry iG){
			if (mICountryList.indexOf(iG) == -1)
				mICountryList.add(iG);
		}
	    
		@Override
		public void onMetadataUpdate(Station station) {
			for (ICountry listener : mICountryList) {
				listener.onMetadataUpdate(Country.this, station);
			}
		}

		@Override
		public void onNewCoverReceived(Station station) {
			for (ICountry listener : mICountryList) {
				listener.onMetadataUpdate(Country.this, station);
			}
		}
}
