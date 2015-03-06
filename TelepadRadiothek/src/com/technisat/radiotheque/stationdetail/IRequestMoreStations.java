package com.technisat.radiotheque.stationdetail;

import java.util.List;

import com.technisat.radiotheque.entity.Station;

public interface IRequestMoreStations {
	
	public void onGotMoreStations(List<Station> stationList);
}
