package com.technisat.radiotheque.stationlist.search;

import java.util.List;

import com.technisat.radiotheque.entity.Station;

public interface ISearchCallback {
	public void onSearchDone(List<Station> stationList);
}
