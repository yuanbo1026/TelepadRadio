package com.technisat.radiotheque.station;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.genre.SquareImageView;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFullscreenFragment;
import com.technisat.radiotheque.stationdetail.TelepadStationDetailFullscreenFragment.OnStationDetailListener;

public class TelepadFullscreenStationActivity extends FragmentActivity
		implements OnStationDetailListener, IMediaPlayerBroadcastListener,
		OnClickListener {

	private SquareImageView mCover;
	private ProgressBar mSpinner;

	private MediaPlayerBroadcastReceiver mBroadcast;
	private TelepadStationDetailFullscreenFragment mStationDetailFragment;

	private Station mStation;
	private ImageLoader mImageLoader;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		Misc.initUIL(getApplicationContext());

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.telepad_activity_station_fullscreen,
				container);

		mContext = this;

		mImageLoader = ImageLoader.getInstance();

		Intent intent = getIntent();
		mStation = intent
				.getParcelableExtra(getString(R.string.radiothek_bundle_station));

		mStationDetailFragment = (TelepadStationDetailFullscreenFragment) getSupportFragmentManager()
				.findFragmentById(R.id.telepad_fg_stationdetail_fullscreen);

		mCover = (SquareImageView) findViewById(R.id.telepad_iv_station_fullscreen_cover);
		mCover.setOnClickListener(this);
		mSpinner = (ProgressBar) findViewById(R.id.telepad_pb_stationdetail_fragment_spinner_fullscreen);

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

		mBroadcast = new MediaPlayerBroadcastReceiver(this,
				mStationDetailFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(mBroadcast.getReceiver());
//		// stop mediaplayer when finish()
//		Intent i = new Intent(TelepadFullscreenStationActivity.this,
//				MediaPlayerService.class);
//		i.setAction(getString(R.string.radiothek_mediaplayerservice_stopstream));
//		i.putExtra(getString(R.string.radiothek_bundle_station), mStation);
//		startService(i);

	}

	/* Fragment Interface */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(TelepadFullscreenStationActivity.this,
				MediaPlayerService.class);
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
		}

	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()) {

		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {

	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
	}

	@Override
	public void onClick(View v) {
//		Intent i = new Intent(mContext, TelepadStationActivity.class);
//		i.putExtra(this.getString(R.string.radiothek_bundle_station), mStation);
//		mContext.startActivity(i);
		finish();

	}
}
