package com.technisat.radiotheque.service;

import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.Station;

public interface IStationObserver {
	
	public void onUpdateStation(Genre g, Station s);
	public void onUpdateStation(Country c, Station s);
}