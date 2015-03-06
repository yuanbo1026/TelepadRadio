package com.technisat.radiotheque.Drawer;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.technisat.radiotheque.about.AboutActivity;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.impressum.ImpressumActivity;
import com.technisat.radiotheque.main.TelepadMainActivity;
import com.technisat.radiotheque.melden.MeldenActivity;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;
import com.technisat.telepadradiothek.R;

public class MyDrawer implements IMediaPlayerBroadcastListener {

	public static final int BUTTON_HOME = 0;
	public static final int BUTTON_HISTORY = 1;
	public static final int BUTTON_ABOUT = 2;
	public static final int BUTTON_PROBLEM = 3;
	public static final int BUTTON_SHARE = 4;
	public static final int BUTTON_IMPRINT = 5;

	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private TypedArray navMenuPressedIcons;

	private Activity mActivity;
	private ListView mDrawerList;
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private MediaPlayerBroadcastReceiver mMPBR;
	ArrayList<NavDrawerItem> navDrawerItems;
	NavDrawerListAdapter adapter;

	public MyDrawer(Activity activity) {
		mActivity = activity;
		init();
		mMPBR = new MediaPlayerBroadcastReceiver(mActivity);
		activity.registerReceiver(mMPBR.getReceiver(), mMPBR.getIntentFiler());
		mMPBR.addListener(this);
		mMPBR.requestUpdateFromService();
	}

	public void finalize() {
		if (mMPBR != null && mActivity != null)
			try {
				mActivity.unregisterReceiver(mMPBR.getReceiver());
			} catch (IllegalArgumentException e) {
				// nice to know but we dont care
			}
	}

	public ListView getDrawerList() {
		return mDrawerList;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public ActionBarDrawerToggle getDrawerToggle() {
		return mDrawerToggle;
	}

	public void init() {

		navMenuTitles = mActivity.getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = mActivity.getResources().obtainTypedArray(R.array.nav_drawer_icons);
		navMenuPressedIcons = mActivity.getResources().obtainTypedArray(R.array.nav_drawer_pressed_icons);
		
		navDrawerItems = new ArrayList<NavDrawerItem>();

		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1),navMenuPressedIcons.getResourceId(0, -1), BUTTON_HOME, true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1),navMenuPressedIcons.getResourceId(1, -1), BUTTON_HISTORY, true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1),navMenuPressedIcons.getResourceId(2, -1), BUTTON_ABOUT, true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1),navMenuPressedIcons.getResourceId(3, -1), BUTTON_PROBLEM, true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1),navMenuPressedIcons.getResourceId(4, -1), BUTTON_SHARE, true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1),navMenuPressedIcons.getResourceId(5, -1), BUTTON_IMPRINT, true));
		navMenuIcons.recycle();

		mDrawerList = (ListView) mActivity.findViewById(R.id.list_slidermenu);
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
//		mDrawerList.setOnItemSelectedListener(new ListView.OnItemSelectedListener(){
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//				Toast.makeText(mActivity, "Item "+position+" get  selected.", Toast.LENGTH_LONG).show();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//				Toast.makeText(mActivity, "Item  get  unselected.", Toast.LENGTH_LONG).show();
//			}
//			
//		});
		
		
		adapter = new NavDrawerListAdapter(mActivity.getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		mDrawerLayout = (DrawerLayout) mActivity
				.findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout,
				R.drawable.ic_launcher, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			/**
			 * Muss rein. Nur im Layout gravity="right" zu definieren reicht
			 * nicht aus
			 */
			@SuppressLint("RtlHardcoded")
			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				if (item != null && item.getItemId() == android.R.id.home) {
					if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
						mDrawerLayout.closeDrawer(Gravity.RIGHT);
					} else {
						mDrawerLayout.openDrawer(Gravity.RIGHT);
					}
				}
				return false;
			}

			public void onDrawerClosed(View view) {
			}

			public void onDrawerOpened(View drawerView) {
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		// TODO on item click listener
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			NavDrawerItem item = (NavDrawerItem) adapter.getItem(position);
			if (item != null) {
				displayView(item.getId());
			}
			
		}

	}

	@SuppressLint("RtlHardcoded")
	public void displayView(int id) {
		Intent intent;
		DatabaseHandler dbHandler = new DatabaseHandler(
				mActivity.getApplicationContext());
		List<Station> stationList;
		StationList sList;

		switch (id) {
		case BUTTON_HOME:
			if (!(mActivity instanceof TelepadMainActivity)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				intent = new Intent(mActivity, TelepadMainActivity.class);
				mActivity.startActivity(intent);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
			break;
		case BUTTON_HISTORY:
			mDrawerLayout.closeDrawer(Gravity.RIGHT);
			stationList = dbHandler.getAllHistoryStations();
			sList = new StationList(stationList);
			intent = new Intent(mActivity,
					TelepadDisplayStationListActivity.class);
			intent.setAction(mActivity
					.getString(R.string.radiothek_action_history));
			intent.putExtra(
					mActivity
							.getString(R.string.radiothek_bundle_stationlistparcelable),
					sList);
			mActivity.startActivity(intent);
			break;
		case BUTTON_ABOUT:
			if (!(mActivity instanceof AboutActivity)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				intent = new Intent(mActivity, AboutActivity.class);
				mActivity.startActivity(intent);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
			break;
		case BUTTON_PROBLEM:
			if (!(mActivity instanceof MeldenActivity)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				intent = new Intent(mActivity, MeldenActivity.class);
				mActivity.startActivity(intent);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
			break;
		case BUTTON_IMPRINT:
			if (!(mActivity instanceof ImpressumActivity)) {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
				intent = new Intent(mActivity, ImpressumActivity.class);
				mActivity.startActivity(intent);
			} else {
				mDrawerLayout.closeDrawer(Gravity.RIGHT);
			}
			break;
		}
	}

	@Override
	public void onStartedPlayingStation(Station station) {
	}

	@Override
	public void onStoppedPlayingStation(Station station) {
	}

	@Override
	public void onCurrentlyPlaying(Station station) {
	}

	@Override
	public void onErrorPlaying(Station station, int errorCode) {
		onStoppedPlayingStation(station);
	}

}