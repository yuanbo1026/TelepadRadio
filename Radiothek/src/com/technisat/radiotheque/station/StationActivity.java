package com.technisat.radiotheque.station;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.genre.SquareImageView;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.stationdetail.StationDetailFragment;
import com.technisat.radiotheque.stationdetail.StationDetailFragment.OnStationDetailListener;

public class StationActivity extends FragmentActivity 
	implements OnStationDetailListener, IMediaPlayerBroadcastListener {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	private SquareImageView mCover;
	private ProgressBar mSpinner;
	
	private MediaPlayerBroadcastReceiver mBroadcast;
	private StationDetailFragment mStationDetailFragment;
	
	private Station mStation;
	private ImageLoader mImageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
		Misc.initUIL(getApplicationContext());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mMyDrawer = new MyDrawer(this);
        mDrawerLayout = mMyDrawer.getDrawerLayout();
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_station, container);
		
		mImageLoader = ImageLoader.getInstance();
		
		Intent intent = getIntent();
		mStation = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
		
		mStationDetailFragment = (StationDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fg_stationdetail);
		
		mCover = (SquareImageView) findViewById(R.id.iv_station_cover);
		mSpinner = (ProgressBar) findViewById(R.id.pb_station_spinner);
		
		if (mStation != null && mCover != null && mSpinner != null){
			if (mStation.hasCoverAvailable()){
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}

		mBroadcast = new MediaPlayerBroadcastReceiver(this, mStationDetailFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new, menu);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_drawer:
        	if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
        		mDrawerLayout.openDrawer(Gravity.RIGHT);
        		
        	}else{
        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
        	}
            return true;
        default:
        	if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
        	}
        	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
        }
	}
		
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMyDrawer != null)
			mMyDrawer.finalize();
        unregisterReceiver(mBroadcast.getReceiver());
    }

	/* Fragment Interface */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(StationActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);		
	}
	
	@Override
	public void onNewStation(Station station) {
		mStation = station;
		if (mStation != null && mCover != null && mSpinner != null){
			if (mStation.hasCoverAvailable()){
				mImageLoader.displayImage(mStation.getCoverUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else if (mStation.hasStationLogoAvailable()) {
				mImageLoader.displayImage(mStation.getStationLogoUrl(), mCover, new ImageLoaderSpinner(mSpinner));
			} else {
				mCover.setImageResource(R.drawable.ic_default_station);
			}
		}
	}
	
	/* Broadcast Interface */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()){
			mStationDetailFragment.setPlayButton(true);
		}
		
	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mStation != null && mStation.getId() == station.getId()){
			mStationDetailFragment.setPlayButton(false);
		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);	
	}
}