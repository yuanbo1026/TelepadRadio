package com.technisat.radiotheque.genre;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.CountryList;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.GenreList;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.player.PlayerFragement;
import com.technisat.radiotheque.player.PlayerFragement.IPlayerFragment;
import com.technisat.radiotheque.service.IStationObserver;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.service.StationService;
import com.technisat.radiotheque.service.StationService.StationServiceBinder;

/**
 * Activity to Display GenreList OR CountryList
 * (class name might be confusing)
 */
public class GenreActivity extends FragmentActivity implements IStationObserver, IPlayerFragment, IMediaPlayerBroadcastListener {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	private GenreList mGenreList;
	private GenreListAdapter mGenreGridAdapter;
	
	private CountryList mCountryList;
	private CountryListAdapter mCountryGridAdapter;
	
	private StationService mStationService;
	private boolean mBound;
	
	private PlayerFragement mPlayerFragment;
	private MediaPlayerBroadcastReceiver mBroadcast;
	
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
    	inflater.inflate(R.layout.activity_genre, container);
		
		Log.d("nexxoo", "starte genreActivity");
		if (getIntent().getExtras() != null){
			GridView gridView = (GridView) findViewById(R.id.gv_activity_genre_grid);
			mGenreList = getIntent().getExtras().getParcelable(getString(R.string.radiothek_bundle_genreparceable));
			mCountryList = getIntent().getExtras().getParcelable(getString(R.string.radiothek_bundle_countryparceable));
			
			if(mGenreList != null){
				mGenreGridAdapter = new GenreListAdapter(this, mGenreList.getGenreList());
				gridView.setAdapter(mGenreGridAdapter);
			}else{
				if(mCountryList != null){
					mGenreGridAdapter = null;
					mCountryGridAdapter = new CountryListAdapter(this, mCountryList.getCountryList());
					gridView.setAdapter(mCountryGridAdapter);
				}
			}
		    gridView.setOnItemClickListener(onItemClickListener);
		}
		
		//Get fragment for player
		mPlayerFragment = (PlayerFragement) getSupportFragmentManager().findFragmentById(R.id.fg_genre_player);
		
		// Bind to LocalService
        Intent intent = new Intent(this, StationService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		mBroadcast = new MediaPlayerBroadcastReceiver(this, mPlayerFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        if (mMyDrawer != null)
			mMyDrawer.finalize();
        unregisterReceiver(mBroadcast.getReceiver());
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

	/** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            StationServiceBinder binder = (StationServiceBinder) service;
            mStationService = binder.getService();
            mStationService.addListener(GenreActivity.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {			
			
			if(mGenreGridAdapter != null){
				//we have a Genre
				Genre genre = (Genre) mGenreGridAdapter.getItem(position);
				if (genre.getsList() != null && genre.getsList().size() > 0){
					Intent i =  new Intent(GenreActivity.this, MediaPlayerService.class);
				    i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));			    
				    i.putExtra(getString(R.string.radiothek_bundle_station), genre.getsList().get(0));
				    startService(i);
				} else {
					Toast.makeText(GenreActivity.this, getString(R.string.genreactivity_text_nostationavailable), Toast.LENGTH_SHORT).show();
				}
				return;
			}
			
			if (mCountryGridAdapter != null){			
				//we have a Country
				Country country = (Country) mCountryGridAdapter.getItem(position);;
				if (country.getsList() != null && country.getsList().size() > 0){
					Intent i =  new Intent(GenreActivity.this, MediaPlayerService.class);
				    i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));			    
				    i.putExtra(getString(R.string.radiothek_bundle_station), country.getsList().get(0));
				    startService(i);
				} else {
					Toast.makeText(GenreActivity.this, getString(R.string.genreactivity_text_nostationavailable), Toast.LENGTH_SHORT).show();
				}
				return;
			}
		}
	};


	@Override
	public void onUpdateStation(Genre g, Station s) {
		if(mGenreGridAdapter != null){
			mGenreGridAdapter.updateStation(g, s);
		}
	}
	@Override
	public void onUpdateStation(Country c, Station s) {
		if(mCountryGridAdapter != null){
			mCountryGridAdapter.updateStation(c, s);
		}
	}

	/* Interface MediaPlayerService <-> Activity <-> PlayerFragment */
	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(GenreActivity.this, MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);
	}
	
	/* IMediaPlayerBroadcastListener */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mGenreList != null)
			for (Genre g : mGenreList.getGenreList()) {
				if (g.getsList() != null && g.getsList().size() > 0){
					Station s = g.getsList().get(0);
					if (s != null && s.getId() == station.getId()){
						//currently showing this station at position 0
						mGenreGridAdapter.showStationPauseButton(g, station);
					}
				}			
			}	
		
		if (mCountryList != null)
			for (Country c : mCountryList.getCountryList()) {
				if (c.getsList() != null && c.getsList().size() > 0){
					Station s = c.getsList().get(0);
					if (s != null && s.getId() == station.getId()){
						//currently showing this station at position 0
						mCountryGridAdapter.showStationPauseButton(c, station);
					}
				}			
			}	
	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mGenreList != null)
			for (Genre g : mGenreList.getGenreList()) {
				if (g.getsList() != null && g.getsList().size() > 0){
					Station s = g.getsList().get(0);
					if (s != null && s.getId() == station.getId()){
						//currently showing this station at position 0
						mGenreGridAdapter.hideStationPauseButton(g, station);
					}
				}			
			}
		
		if (mCountryList != null)
			for (Country c : mCountryList.getCountryList()) {
				if (c.getsList() != null && c.getsList().size() > 0){
					Station s = c.getsList().get(0);
					if (s != null && s.getId() == station.getId()){
						//currently showing this station at position 0
						mCountryGridAdapter.hideStationPauseButton(c, station);
					}
				}			
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