package com.technisat.radiotheque.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.genre.SquareImageView;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment.OnStationDetailListener;

public class TelepadStationActivity extends FragmentActivity implements
		OnStationDetailListener, IMediaPlayerBroadcastListener {

	private SquareImageView mCover;
	private ProgressBar mSpinner;

	private MediaPlayerBroadcastReceiver mBroadcast;
	private TelepadStationDetailFragment mStationDetailFragment;

	private Station mStation;
	private ImageLoader mImageLoader;
	private Handler mHandler;
	private Runnable mRunnable;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		Misc.initUIL(getApplicationContext());

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.telepad_activity_station, container);
		
		mContext = this;

		mImageLoader = ImageLoader.getInstance();

		Intent intent = getIntent();
		mStation = intent
				.getParcelableExtra(getString(R.string.radiothek_bundle_station));

		mStationDetailFragment = (TelepadStationDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.telepad_fg_stationdetail);

		mCover = (SquareImageView) findViewById(R.id.telepad_iv_station_cover);
		mSpinner = (ProgressBar) findViewById(R.id.telepad_pb_stationdetail_fragment_spinner);

		if (mStation != null && mCover != null && mSpinner != null) {
			if (mStation.hasCoverAvailable()) {
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover,
						new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover,
						new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}
		
		mHandler = new Handler();
        mRunnable = new Runnable() {
            public void run() {
//            	Toast.makeText(mContext, "After 2 second start up the Fullscreen activity ", Toast.LENGTH_LONG).show();
            	Intent i = new Intent(mContext, TelepadFullscreenStationActivity.class);
            	i.putExtra(mContext.getString(R.string.radiothek_bundle_station), mStation);
				mContext.startActivity(i);	
            }
        };

		

		mBroadcast = new MediaPlayerBroadcastReceiver(this,
				mStationDetailFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();
		Log.d("TelepadRadiotheque", "TelepadStationActivity.onCreate()->finished");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcast.getReceiver());
		
		mHandler.removeCallbacks(mRunnable);
		//stop mediaplayer when finish()
		Intent i = new Intent(TelepadStationActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_stopstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), mStation);
		startService(i);
	}

	/* Fragment Interface */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(TelepadStationActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);
	}

	@Override
	public void onNewStation(Station station) {
		mStation = station;
		if (mStation != null && mCover != null && mSpinner != null) {
			if (mStation.hasCoverAvailable()) {
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover,
						new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover,
						new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}
	}

	/* Broadcast Interface */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()) {
			mStationDetailFragment.setPlayButton(true);
		}
		Log.d("TelepadStationActivity", "callback->on Started PlayingStation");
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, 3000);

	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()) {
			mStationDetailFragment.setPlayButton(false);
			Log.d("TelepadStationActivity", "callback->on Stopped PlayingStation");
		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {
		Log.d("TelepadStationActivity", "callback->on Currently Playing");
	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
	}
}
