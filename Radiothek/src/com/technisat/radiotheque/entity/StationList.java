package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class StationList implements Parcelable{
	private List<Station> mStationList;
	
	public StationList(List<Station> stationList){
		setStationList(stationList);
	}
	
	public List<Station> getStationList() {
		return mStationList;
	}

	public void setStationList(List<Station> stationList) {
		this.mStationList = stationList;
	}
	
	public void logGenreList(){
		for (Station station : mStationList) {
			Log.d("Nexxoo", "Genre: " + station.getStationName());
		}
	}
	
	public void switchAllStationsPlayingOff(){
		for (Station s : mStationList) {
			s.setPlaying(false);
		}
	}
	
	// Parcelling part
	public StationList(Parcel in){
		this.mStationList = new ArrayList<Station>();
        in.readList(this.mStationList, Station.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.getStationList());
    }
    public static final Parcelable.Creator<StationList> CREATOR = new Parcelable.Creator<StationList>() {
        public StationList createFromParcel(Parcel in) {
            return new StationList(in); 
        }

        public StationList[] newArray(int size) {
            return new StationList[size];
        }
    };
}