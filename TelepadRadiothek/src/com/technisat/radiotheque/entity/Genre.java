package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Genre implements Parcelable, IStationCoverUpdate, IStationMetadataUpdate {
	private long id;
	private String name;
	private Genre parent = null;
	private List<Station> sList;
	
	private List<IGenre> mIGeneList = new ArrayList<IGenre>();
	
	public Genre(long id, String name, Genre parent, List<Station> slist){
		setId(id);
		setName(name);
		setParent(parent);
		setsList(slist);
	}
	
	public Genre(long id, String name, List<Station> slist){
		setId(id);
		setName(name);
		setsList(slist);
	}
	
	public void listenToAllStations(){
		for(Station s : sList){
			s.addCoverListener(this);
			s.addMetadataListener(this);
		}
	}
	
	public void updateMetaDataForAllStations(){		
		for(Station s : sList){
			s.updateMetaData();
		}
	}
	
	public List<IGenre> getListenerList(){
		return mIGeneList;
	}
	
	public void addListener(IGenre iG){
		if (mIGeneList.indexOf(iG) == -1)
			mIGeneList.add(iG);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Genre getParent() {
		return parent;
	}

	public void setParent(Genre parent) {
		this.parent = parent;
	}

	public List<Station> getsList() {
		return sList;
	}

	public void setsList(List<Station> sList) {
		this.sList = sList;
	}
	
	// Parcelling part
	public Genre(Parcel in){

        this.id = in.readLong();
        this.name = in.readString();      
        this.parent = in.readParcelable(Genre.class.getClassLoader());
        
        this.sList = new ArrayList<Station>();
        in.readList(this.sList, Station.class.getClassLoader());
    }
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeLong(this.id);
		dest.writeString(this.name);
		dest.writeParcelable(this.parent, 0);
        dest.writeList(this.sList);
    }
    public static final Parcelable.Creator<Genre> CREATOR = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel in) {
            return new Genre(in); 
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

	@Override
	public void onMetadataUpdate(Station station) {
		for (IGenre listener : mIGeneList) {
			listener.onMetadataUpdate(Genre.this, station);
		}
	}

	@Override
	public void onNewCoverReceived(Station station) {
		for (IGenre listener : mIGeneList) {
			listener.onMetadataUpdate(Genre.this, station);
		}
	}
}