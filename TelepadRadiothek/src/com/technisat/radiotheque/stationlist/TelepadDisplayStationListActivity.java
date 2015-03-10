package com.technisat.radiotheque.stationlist;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.loadstations.ILoadStations;
import com.technisat.radiotheque.loadstations.LoadStationsOfCountryAsyncTask;
import com.technisat.radiotheque.loadstations.LoadStationsOfGenreAsyncTask;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.player.PlayerFragement.IPlayerFragment;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.TelepadStationActivity;
import com.technisat.radiotheque.stationlist.search.ISearchCallback;
import com.technisat.radiotheque.stationlist.search.SearchAsyncTask;

public class TelepadDisplayStationListActivity extends FragmentActivity
		implements IPlayerFragment, ISearchCallback,
		IMediaPlayerBroadcastListener, ILoadStations {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private ListView mGehoertListView;
	private TelepadItemListAdapter mItemListAdapter;
	private List<Station> stationList;

	private ProgressDialog mProgressBar;
	private boolean mIsFavList = false;

	private ViewSwitcher mViewSwitcher;
	// private GridView mGehoertGridView;
	private ItemListAdapter mItemGridAdapter;

	public static final int VIEW_LIST = 0;
	public static final int VIEW_GRID = 1;

	private Context mContext;
	private StationList sList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);

		Misc.initUIL(getApplicationContext());
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();
		mContext = this;

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.telepad_activity_displaystationlist,
				container);

		Intent intent = getIntent();
		handleIntent(intent);

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewswitcher);
		mViewSwitcher.setDisplayedChild(VIEW_LIST);

		// Get fragment for player
		// mPlayerFragment = (PlayerFragement)
		// getSupportFragmentManager().findFragmentById(R.id.fg_genre_player);
		// mBroadcast = new MediaPlayerBroadcastReceiver(this, mPlayerFragment);
		// mBroadcast.addListener(this);
		// registerReceiver(mBroadcast.getReceiver(),
		// mBroadcast.getIntentFiler());
		// mBroadcast.requestUpdateFromService();
	}

	public void changeToList(View view) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			mViewSwitcher.showPrevious();

			// ImageButton mBList = (ImageButton) findViewById(R.id.b_list);
			// mBList.setBackgroundResource(R.color.RealWhite);
			// mBList.setImageResource(R.drawable.ic_stationlist_blue_light);
			// ImageButton mBGrid = (ImageButton) findViewById(R.id.b_grid);
			// mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
			// mBGrid.setImageResource(R.drawable.ic_grid_white);

			// mBroadcast.requestUpdateFromService();
		}
	}

	public void changeToGrid(View view) {
		if (mViewSwitcher.getDisplayedChild() == 0) {
			mViewSwitcher.showNext();

			// ImageButton mBList = (ImageButton) findViewById(R.id.b_list);
			// mBList.setBackgroundResource(R.color.RadiothekBlueLight);
			// mBList.setImageResource(R.drawable.ic_stationlist);
			// ImageButton mBGrid = (ImageButton) findViewById(R.id.b_grid);
			// mBGrid.setBackgroundResource(R.color.RealWhite);
			// mBGrid.setImageResource(R.drawable.ic_grid_blue_light);

			// mBroadcast.requestUpdateFromService();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	/**
	 * Checks if the Intent got an Action and basically initiates the
	 * {@link ItemListAdapter} and fills the {@link ListView} with Data
	 * 
	 * @param intent
	 */
	private void handleIntent(Intent intent) {
		TextView emptyListTV = (TextView) findViewById(android.R.id.empty);
		mGehoertListView = (ListView) findViewById(R.id.telepad_lv_displaystationlist_list);
		// mGehoertGridView = (GridView)
		// findViewById(R.id.lv_displaystationlist_grid);

		if (intent == null)
			return;

		/** Handle Search Action Intent */
		if (intent.getAction() != null
				&& intent.getAction().equals(Intent.ACTION_SEARCH)) {

			emptyListTV
					.setText(getString(R.string.radiothek_displaystationlist_text_search));
			mGehoertListView.setEmptyView(emptyListTV);
			// mGehoertGridView.setEmptyView(emptyListTV);

			// we got called as search result activity
			String query = intent.getStringExtra(SearchManager.QUERY);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_searchingdb));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			SearchAsyncTask task = new SearchAsyncTask(this, query, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
				// handle this in v2
			}

		}

		/** Handle MoreStationsOfGenre Action Intent */
		else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_morestationsofgenre))) {

			long genreId = intent.getLongExtra(getString(R.string.radiothek_bundle_long), 9);
			int count = intent.getIntExtra(getString(R.string.radiothek_bundle_int), 50);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			LoadStationsOfGenreAsyncTask task = new LoadStationsOfGenreAsyncTask(
					genreId, count, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
			}
		}

		/** Handle MoreStationsOfCountry Action Intent */
		else if (intent.getAction() != null && intent.getAction().equals(getString(R.string.radiothek_action_morestationsofcountry))) {

			String iso = "en";
			if (intent.hasExtra(getString(R.string.radiothek_bundle_string)))
				iso = intent.getStringExtra(getString(R.string.radiothek_bundle_string));
			int count = intent.getIntExtra(getString(R.string.radiothek_bundle_int), 50);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			LoadStationsOfCountryAsyncTask task = new LoadStationsOfCountryAsyncTask(
					iso, count, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
			}
		}

		/**
		 * This is done so that the Adapter knows if he should delete Station in
		 * his View if it`s favorite flag is set to false and that the ListView
		 * can display the correct text if the View is empty
		 */
		else if (intent.getAction() != null
				&& intent.getAction().equals(
						getString(R.string.radiothek_action_favorite))) {
			mIsFavList = true;
			emptyListTV
					.setText(getString(R.string.radiothek_displaystationlist_text_favorite));
			mGehoertListView.setEmptyView(emptyListTV);
			// mGehoertGridView.setEmptyView(emptyListTV);
		}

		/**
		 * This is done so that the ListView can display the correct text if the
		 * View is empty
		 */
		else if (intent.getAction() != null
				&& intent.getAction().equals(
						getString(R.string.radiothek_action_history))) {
			emptyListTV
					.setText(getString(R.string.radiothek_displaystationlist_text_history));
			mGehoertListView.setEmptyView(emptyListTV);
			// mGehoertGridView.setEmptyView(emptyListTV);
		}

		/** Sets ItemListAdapter and the ListView */
		sList = intent
				.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
		if (sList != null && sList.getStationList() != null) {
			stationList = sList.getStationList();

			mItemListAdapter = new TelepadItemListAdapter(this,
					R.layout.telepad_stationlist_list_item, stationList,
					mIsFavList, R.layout.telepad_stationlist_list_item);
			mGehoertListView.setAdapter(mItemListAdapter);
			// mItemGridAdapter = new ItemListAdapter(this,
			// R.layout.stationlist_list_item, stationList, mIsFavList,
			// R.layout.stationlist_grid_item);
			// mGehoertGridView.setAdapter(mItemGridAdapter);
		}
		mGehoertListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mGehoertListView.setOnItemClickListener(new OnItemClickListener() {

			/** Handles ItemClick and starts/stops player */
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Station s = mItemListAdapter.getItem(position);
				if (s != null) {
					// TODO item OnClick event
					Intent i = new Intent(TelepadDisplayStationListActivity.this,MediaPlayerService.class);
					i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
					i.putExtra(getString(R.string.radiothek_bundle_station), s);
					startService(i);
					
					int index = sList.getStationList().indexOf(s);
					Log.e("TelepadDisplayStationListActivity", "The playing station is the No."+index+" in the Station List.");

					Intent intent = new Intent(TelepadDisplayStationListActivity.this,TelepadStationActivity.class);
					intent.putExtra(getString(R.string.radiothek_bundle_station), s); 
					intent.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
					startActivity(intent);
				}
			}
		});

		// mGehoertGridView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// mGehoertGridView.setOnItemClickListener(new OnItemClickListener() {
		//
		// /** Handles ItemClick and starts/stops player */
		// @Override
		// public void onItemClick(AdapterView<?> parent, View v, int position,
		// long id) {
		// Station s = mItemGridAdapter.getItem(position);
		// if (s != null){
		//
		// Intent i = new Intent(DisplayStationListActivity.this,
		// MediaPlayerService.class);
		// i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		// i.putExtra(getString(R.string.radiothek_bundle_station), s);
		// startService(i);
		// }
		// }
		// });
	}

	@Override
	protected void onDestroy() {
		if (mMyDrawer != null)
			mMyDrawer.finalize();
		// unregisterReceiver(mBroadcast.getReceiver());
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_new, menu);

		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));

		return true;
	}

	@SuppressLint("RtlHardcoded")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_drawer:
			if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
				mDrawerLayout.openDrawer(Gravity.RIGHT);

			} else {
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

	/* Interfaces */

	@Override
	public void onTogglePlay(Station station) {
		Intent i = new Intent(TelepadDisplayStationListActivity.this,
				MediaPlayerService.class);
		i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
		i.putExtra(getString(R.string.radiothek_bundle_station), station);
		startService(i);
	}

	/* ISearchCallback */

	@Override
	public void onSearchDone(List<Station> stationList) {
		if (mProgressBar != null && mProgressBar.isShowing()) {
			Intent i = new Intent(this, DisplayStationListActivity.class);
			i.putExtra(
					getString(R.string.radiothek_bundle_stationlistparcelable),
					new StationList(stationList));
			handleIntent(i);
			mProgressBar.hide();
		}
	}

	/* IMediaPlayerBroadcastListener */

	@Override
	public void onStartedPlayingStation(Station station) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			if (mItemGridAdapter != null) {
				mItemGridAdapter.showPauseButton(station);
			}
		} else {
			if (mItemListAdapter != null) {
				mItemListAdapter.showPauseButton(station);
			}
		}
	}

	@Override
	public void onStoppedPlayingStation(Station station) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			if (mItemGridAdapter != null) {
				mItemGridAdapter.hidePauseButton(station);
			}
		} else {
			if (mItemListAdapter != null) {
				mItemListAdapter.hidePauseButton(station);
			}
		}
	}

	@Override
	public void onCurrentlyPlaying(Station station) {
	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
	}

	/* Interface load more stations */

	@Override
	public void onMoreStationsLoaded(List<Station> stationList) {
		if (stationList != null) {
			mItemListAdapter = new TelepadItemListAdapter(this,
					R.layout.telepad_stationlist_list_item, stationList, false,
					R.layout.telepad_stationlist_list_item);
			mGehoertListView.setAdapter(mItemListAdapter);
			// mItemGridAdapter = new ItemListAdapter(this,
			// R.layout.stationlist_list_item, stationList, false,
			// R.layout.stationlist_grid_item);
			// mGehoertGridView.setAdapter(mItemGridAdapter);
			if (mProgressBar != null && mProgressBar.isShowing())
				mProgressBar.hide();
		}
	}
}
