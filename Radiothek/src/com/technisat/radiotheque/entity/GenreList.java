package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class GenreList implements Parcelable{
	private List<Genre> mGenreList;
	
	public GenreList(List<Genre> genreList){
		setGenreList(genreList);
	}
	
	public List<Genre> getGenreList() {
		return mGenreList;
	}

	public void setGenreList(List<Genre> genreList) {
		this.mGenreList = genreList;
	}
	
	public void logGenreList(){
		for (Genre genre : mGenreList) {
			Log.d("Nexxoo", "Genre: " + genre.getName());
			for (Station s : genre.getsList()) {
				Log.d("Nexxoo", "Station: " + s.getStationName());
			}
		}
	}
	
	
	
	// Parcelling part
	public GenreList(Parcel in){
		this.mGenreList = new ArrayList<Genre>();
        in.readList(this.mGenreList, Genre.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.getGenreList());
    }
    public static final Parcelable.Creator<GenreList> CREATOR = new Parcelable.Creator<GenreList>() {
        public GenreList createFromParcel(Parcel in) {
            return new GenreList(in); 
        }

        public GenreList[] newArray(int size) {
            return new GenreList[size];
        }
    };
}
