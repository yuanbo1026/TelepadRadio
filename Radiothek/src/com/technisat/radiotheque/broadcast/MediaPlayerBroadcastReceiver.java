package com.technisat.radiotheque.broadcast;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.player.PlayerFragement;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.service.error.MediaPlayerErrors;
import com.technisat.radiotheque.stationdetail.StationDetailFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFullscreenFragment;

public class MediaPlayerBroadcastReceiver {
	
	private PlayerFragement mPlayerFragment;
	private StationDetailFragment mStationDetailFragment;
	private TelepadStationDetailFragment mTelepadStationDetailFragment;
	private TelepadStationDetailFullscreenFragment mTelepadStationDetailFullscreenFragment;
	private Context mContext;
	private List<IMediaPlayerBroadcastListener> mListener = new ArrayList<IMediaPlayerBroadcastListener>();
	
	private BroadcastReceiver mMessageReceiver;	
	
	public MediaPlayerBroadcastReceiver(Context ctx, PlayerFragement frag){
		this(ctx);
		mPlayerFragment = frag;
	}
	
	public MediaPlayerBroadcastReceiver(Context ctx, StationDetailFragment frag){
		this(ctx);
		mStationDetailFragment = frag;
	}
	
	public MediaPlayerBroadcastReceiver(Context ctx, TelepadStationDetailFragment frag){
		this(ctx);
		mTelepadStationDetailFragment = frag;
	}
	
	public MediaPlayerBroadcastReceiver(Context ctx, TelepadStationDetailFullscreenFragment frag){
		this(ctx);
		mTelepadStationDetailFullscreenFragment = frag;
	}
	
	public MediaPlayerBroadcastReceiver(Context ctx){
		mContext = ctx;
				
		
		mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
		    if (intent != null && intent.getAction() != null){
		    	Station station = intent.getParcelableExtra(mContext.getString(R.string.radiothek_bundle_station));
		    	
		    	if (station != null){	
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.setStation(station);
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setNewStation(station);
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setNewStation(station);
		    		if (mTelepadStationDetailFullscreenFragment != null)
		    			mTelepadStationDetailFullscreenFragment.setNewStation(station);
		    	}
		    	
		    	if (intent.getAction().equals(mContext.getString( R.string.radiothek_mediaplayerservice_startedplaying) )){
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.updateUI(true);
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setPlayButton(true);
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setPlayButton(true);
//		    		if (mTelepadStationDetailFullscreenFragment != null)
//		    			mTelepadStationDetailFullscreenFragment.setPlayButton(true);
		    		Log.d("MediaPlayerBroadcastReceiver", "mListener->onStartedPlayingStation");
		    		for (IMediaPlayerBroadcastListener l : mListener) {
						l.onStartedPlayingStation(station);
					}
		    		
		    		return;
		    	}
		    	
		    	if (intent.getAction().equals(mContext.getString( R.string.radiothek_mediaplayerservice_pausedplaying) )){
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.updateUI(false);
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setPlayButton(false);
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setPlayButton(false);
//		    		if (mTelepadStationDetailFullscreenFragment != null)
//		    			mTelepadStationDetailFullscreenFragment.setPlayButton(false);
		    		
		    		for (IMediaPlayerBroadcastListener l : mListener) {
						l.onStoppedPlayingStation(station);
					}
		    		
		    		return;
		    	}
		    	
		    	if (intent.getAction().equals(mContext.getString( R.string.radiothek_mediaplayerservice_broadcastmetadata) )){
		    		boolean isPlaying = intent.getBooleanExtra(mContext.getString(R.string.radiothek_bundle_boolean), false);
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.updateUI(isPlaying);
		    		
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setPlayButton(isPlaying);
		    		
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setPlayButton(isPlaying);
		    		
//		    		if (mTelepadStationDetailFullscreenFragment != null)
//		    			mTelepadStationDetailFullscreenFragment.setPlayButton(isPlaying);
		    		Log.d("MediaPlayerBroadcastReceiver", "mListener->onCurrentlyPlaying");
		    		if(isPlaying){
			    		for (IMediaPlayerBroadcastListener l : mListener) {
							l.onCurrentlyPlaying(station);
						}
		    		}
		    		
		    		return;		    		
		    	}
		    	
		    	if (intent.getAction().equals(mContext.getString( R.string.radiothek_mediaplayerservice_prepareplaying) )){
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.setLoading();
		    		
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setLoading();
		    		
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setLoading();
		    		
		    		if (mTelepadStationDetailFullscreenFragment != null)
		    			mTelepadStationDetailFullscreenFragment.setLoading();
		    		
		    		return;		    		
		    	}
		    	
		    	if (intent.getAction().equals(mContext.getString( R.string.radiothek_mediaplayerservice_errorplaying) )){
		    		int error = intent.getIntExtra(mContext.getString(R.string.radiothek_bundle_int), 1);
		    		
		    		if (mPlayerFragment != null)
		    			mPlayerFragment.setErrorMessage(MediaPlayerErrors.getErrorMessageByErrorCode(mContext, error));
		    		
		    		if (mStationDetailFragment != null)
		    			mStationDetailFragment.setErrorMessage(MediaPlayerErrors.getErrorMessageByErrorCode(mContext, error));
		    		
		    		if (mTelepadStationDetailFragment != null)
		    			mTelepadStationDetailFragment.setErrorMessage(MediaPlayerErrors.getErrorMessageByErrorCode(mContext, error));
		    		
		    		if (mTelepadStationDetailFullscreenFragment != null)
		    			mTelepadStationDetailFullscreenFragment.setErrorMessage(MediaPlayerErrors.getErrorMessageByErrorCode(mContext, error));
		    		
		    		for (IMediaPlayerBroadcastListener l : mListener) {
						l.onErrorPlaying(station, error);
					}
		    	}
		    }
		  }
		};
	}
	
	public void addListener(IMediaPlayerBroadcastListener listener){
		if (mListener.indexOf(listener) == -1)
			mListener.add(listener);
	}
	
	public void requestUpdateFromService(){
        Intent i = new Intent(mContext, MediaPlayerService.class);
        i.setAction(mContext.getString(R.string.radiothek_mediaplayerservice_requestcurrentstation));
        mContext.startService(i);
	}
	
	public BroadcastReceiver getReceiver(){
		return mMessageReceiver;
	}
	
	public IntentFilter getIntentFiler(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(mContext.getString( R.string.radiothek_mediaplayerservice_pausedplaying ));
		filter.addAction(mContext.getString( R.string.radiothek_mediaplayerservice_startedplaying ));
		filter.addAction(mContext.getString( R.string.radiothek_mediaplayerservice_broadcastmetadata ));
		filter.addAction(mContext.getString( R.string.radiothek_mediaplayerservice_prepareplaying ));
		filter.addAction(mContext.getString( R.string.radiothek_mediaplayerservice_errorplaying ));
		return filter;
	}
}
