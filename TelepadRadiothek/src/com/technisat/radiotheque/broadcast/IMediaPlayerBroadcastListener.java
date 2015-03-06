package com.technisat.radiotheque.broadcast;

import com.technisat.radiotheque.entity.Station;

public interface IMediaPlayerBroadcastListener {
	
	public void onStartedPlayingStation(Station station);
	public void onStoppedPlayingStation(Station station);
	public void onCurrentlyPlaying(Station station);
	public void onErrorPlaying(Station station, int errorCode);

}
