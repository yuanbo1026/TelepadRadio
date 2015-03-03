package com.technisat.radiotheque.service;

import java.io.IOException;
import java.util.HashMap;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.service.StationService.StationServiceBinder;

//TODO implement onBuffering Interface of MediaPlayer
//TODO check why some streams don't work

public class MediaPlayerService extends Service implements IStationObserver {

	private MediaPlayer mMediaPlayer = null;
	private Station mCurrentStation = null;
	
	private StationService mStationService;
	private boolean mBound;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		// Bind to LocalService
        Intent bindIntent = new Intent(this, StationService.class);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
		
		if (mMediaPlayer == null)
			mMediaPlayer = createNewPlayer();
		
		if (intent != null && intent.getAction() != null){
			if (intent.getAction().equals(getString(R.string.radiothek_mediaplayerservice_playstream))){						
				Station station = intent.getExtras().getParcelable(getString(R.string.radiothek_bundle_station));
				//check if we already play that station	
				if (mCurrentStation != null && station != null && (mCurrentStation.getId() == station.getId()) ){
					//we have the same station, so check if it is playing, if so pause, else restart
					if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
						mMediaPlayer.pause();
						mCurrentStation.setPlaying(false);
						sendBroadcastPausedPlaying();
					} else {
						mMediaPlayer.start();
						mCurrentStation.setPlaying(true);
						sendBroadcastPlayingStarted();
					}
				} else {
					//new station, we switch
					if (station != null){
						if (mMediaPlayer.isPlaying()){
							mCurrentStation.setPlaying(false);							
							mMediaPlayer.stop();
						}
						
						if (mCurrentStation != null)
							sendBroadcastPausedPlaying();						
							
//						mMediaPlayer = createNewPlayer();
						mCurrentStation = station;
						mCurrentStation.setPlaying(true);
						mCurrentStation.updateMetaData();
						
						if (mCurrentStation != null)
							playStation();
					}
				}
			}
			
			if (intent.getAction().equals(getString(R.string.radiothek_mediaplayerservice_stopstream))){
				Station station = null;
				if (intent.hasExtra( getString(R.string.radiothek_bundle_station) ))
					station = intent.getExtras().getParcelable(getString(R.string.radiothek_bundle_station));
				
				if (station != null && mCurrentStation != null){
					//we got the data
					if (station.getId() == mCurrentStation.getId()){
						mMediaPlayer.pause();
						sendBroadcastPausedPlaying();
					} else {
						//request to pause\stop the playback but we dont play that station currently
						//send broadcast so that maybe the caller gets to know we changed station
						sendBroadcastCurrentStation();
					}
				} 
				
				if (mMediaPlayer != null && mMediaPlayer.isPlaying()){
					//request was stop playing no matter what
					mMediaPlayer.pause();
					sendBroadcastPausedPlaying();
				}
			}
			
			if (intent.getAction().equals(getString(R.string.radiothek_mediaplayerservice_requestcurrentstation))){
				sendBroadcastCurrentStation();
			}
		}
		
		return START_NOT_STICKY;
	}
	
	
	
	@Override
	public void onDestroy() {
		if (mMediaPlayer != null){
//			mMediaPlayer.release();
			mMediaPlayer.stop();
		}
		if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
		super.onDestroy();
	}

	private void sendBroadcastPlayingStarted(){
		Log.d("MediaPlayerService", "sendBroadcast->started playing");
		Intent broadcast = new Intent();
		broadcast.setAction(getString(R.string.radiothek_mediaplayerservice_startedplaying));
		broadcast.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		sendBroadcast(broadcast);
		
		//inform StationService
		Intent service = new Intent(this, StationService.class);
		service.setAction(getString(R.string.radiothek_stationservice_startedstation));
		service.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		startService(service);
	}
	
	private void sendBroadcastPausedPlaying(){
		Log.d("MediaPlayerService", "sendBroadcast->Paused Playing");
		Intent broadcast = new Intent();
		broadcast.setAction(getString(R.string.radiothek_mediaplayerservice_pausedplaying));
		broadcast.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		mCurrentStation.stopUpdateMetaData();
		sendBroadcast(broadcast);
		
		//inform StationService
		Intent service = new Intent(this, StationService.class);
		service.setAction(getString(R.string.radiothek_stationservice_stoppedstation));
		service.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		startService(service);
	}
	
	private void sendBroadcastCurrentStation(){
		Intent broadcast = new Intent();
		broadcast.setAction(getString(R.string.radiothek_mediaplayerservice_broadcastmetadata));
		broadcast.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		broadcast.putExtra(getString(R.string.radiothek_bundle_boolean), mMediaPlayer != null && mMediaPlayer.isPlaying());
		sendBroadcast(broadcast);
	}
	
	private void sendBroadcastPreparePlaying(){
		Intent broadcast = new Intent();
		broadcast.setAction(getString(R.string.radiothek_mediaplayerservice_prepareplaying));
		sendBroadcast(broadcast);
	}
	
	private void sendBroadcastErrorStation(int errorCode){
		Intent broadcast = new Intent();
		broadcast.setAction(getString(R.string.radiothek_mediaplayerservice_errorplaying));
		broadcast.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
		broadcast.putExtra(getString(R.string.radiothek_bundle_int), errorCode);
		sendBroadcast(broadcast);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
		// no binding
	}
	
	private MediaPlayer createNewPlayer(){
		MediaPlayer player = new MediaPlayer();
		player.setOnPreparedListener(onPreparedListener);
		player.setOnInfoListener(onInfoListener);
		player.setOnErrorListener(onErrorListener);
		return player;
	}
	
	//internal stuff
	private void playStation(){
		if (mCurrentStation != null && mMediaPlayer != null){
			try {
				sendBroadcastPreparePlaying();
				mMediaPlayer.setDataSource( this, Uri.parse(mCurrentStation.getStationUrl()), new HashMap<String, String>() );
				mMediaPlayer.prepareAsync();
			} catch (IllegalArgumentException e) {
				Log.e("Nexxoo", "IllegalArgumentException: " + e.getMessage());
			} catch (SecurityException e) {
				Log.e("Nexxoo", "SecurityException: " + e.getMessage());
			} catch (IllegalStateException e) {
				Log.e("Nexxoo", "IllegalStateException: " + e.getMessage());
			} catch (IOException e) {
				Log.e("Nexxoo", "IOException: " + e.getMessage());
			}
		}
	}
	
	//Listener for media player
	
	private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener(){

		@Override
		public void onPrepared(MediaPlayer mp) {
			//TODO check if all this makes sense!!!!!!
			if (mp != null && mMediaPlayer == mp){
				mMediaPlayer.start();
				sendBroadcastPlayingStarted();
			}
			DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
			if (mCurrentStation != null){
				mCurrentStation.setPlaying(true);
				dbHandler.insertStation(mCurrentStation);
			}
		}
	};
	
	private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
		
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				Log.d("Nexxoo", "Buffering started");
				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				Log.d("Nexxoo", "Buffering finished");
				break;
			case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
				Log.d("Nexxoo", "New meta in mediaplayer");
				break;
			}
			return true;
		}
	};
	
	private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
		
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			Log.e("Nexxoo", "ERROR: " + what);
			//TODO send broadcast that stream could not be started
			//asx/mms throw what == 1 (means unknown, yeah)
			if (mp == mMediaPlayer){
				sendBroadcastErrorStation(what);
				mCurrentStation = null;
				mMediaPlayer = null;
			}
			
			return true;
		}
	};
	
	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            StationServiceBinder binder = (StationServiceBinder) service;
            mStationService = binder.getService();
            mStationService.addListener(MediaPlayerService.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
    
    /* Interface IStationObserver */

	@Override
	public void onUpdateStation(Genre g, Station s) {
		if (mCurrentStation != null && s != null && mCurrentStation.getId() == s.getId()){
			mCurrentStation.setMetadata(s.getMetadata());
			mCurrentStation.setBuyLinkAmazon(s.getBuyLinkAmazon());
			mCurrentStation.setCoverUrlLarge(s.getCoverUrlLarge());
			mCurrentStation.setCoverUrlMedium(s.getCoverUrlMedium());
			mCurrentStation.setCoverUrlSmall(s.getCoverUrlSmall());
			sendBroadcastCurrentStation();
		}
	}



	@Override
	public void onUpdateStation(Country c, Station s) {
		if (mCurrentStation != null && s != null && mCurrentStation.getId() == s.getId()){
			mCurrentStation.setMetadata(s.getMetadata());
			mCurrentStation.setBuyLinkAmazon(s.getBuyLinkAmazon());
			mCurrentStation.setCoverUrlLarge(s.getCoverUrlLarge());
			mCurrentStation.setCoverUrlMedium(s.getCoverUrlMedium());
			mCurrentStation.setCoverUrlSmall(s.getCoverUrlSmall());
			sendBroadcastCurrentStation();
		}
	}
}
