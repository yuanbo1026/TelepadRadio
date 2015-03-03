package com.technisat.radiotheque.loadstations;

import java.util.List;

import com.technisat.radiotheque.entity.Station;

public interface ILoadStations {
	
	public void onMoreStationsLoaded(List<Station> stationList);

}
