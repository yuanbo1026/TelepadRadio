package com.technisat.radiotheque.main;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.service.StationService;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;

public class TelepadMainActivity extends Activity implements
		com.jess.ui.TwoWayAdapterView.OnItemClickListener {
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private TelepadGridViewAdapter horzGridViewAdapter;
	private Context mContext;

	public static TwoWayGridView horzGridView;

	// Screen dims
	public final static int COLUMN_PORT = 0;
	public final static int COLUMN_LAND = 1;
	public static int column_selected;
	public static int[] displayWidth;
	public static int[] displayHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		mContext = this;

		mMyDrawer = new MyDrawer(this);
		mDrawerLayout = mMyDrawer.getDrawerLayout();

		LayoutInflater inflater = getLayoutInflater();
		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
		inflater.inflate(R.layout.activity_main_two_way_grid, container);

		horzGridView = (TwoWayGridView) findViewById(R.id.horz_gridview);
		horzGridView.setPadding(0, 0, 0, 0);
		List<DataObject> horzData = generateGridViewObjects();

		horzGridViewAdapter = new TelepadGridViewAdapter(mContext, horzData);

		horzGridView.setAdapter(horzGridViewAdapter);
		horzGridView.setOnItemClickListener(this);
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

	@Override
	protected void onDestroy() {
		if (mMyDrawer != null)
			mMyDrawer.finalize();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		return true;
	}

	private List<DataObject> generateGridViewObjects() {

		List<DataObject> allData = new ArrayList<DataObject>();
		DataObject history = new DataObject(getString(R.string.mainactivity_button_history),R.drawable.home_gridview_item_icon_gehoert, R.drawable.home_gridview_item_bg_color_gehoert);
		DataObject favs = new DataObject(getString(R.string.mainactivity_button_favs),R.drawable.home_gridview_item_icon_favs, R.drawable.home_gridview_item_bg_color_favs);
		DataObject genre = new DataObject(getString(R.string.mainactivity_button_genre),R.drawable.home_gridview_item_icon_genre, R.drawable.home_gridview_item_bg_color_genre);
		DataObject country = new DataObject(getString(R.string.mainactivity_button_country),R.drawable.home_gridview_item_icon_country, R.drawable.home_gridview_item_bg_color_country);
		allData.add(history);
		allData.add(favs);
		allData.add(genre);
		allData.add(country);
		return allData;
	}

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view,
			int position, long id) {
		DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
		List<Station> stationList;
		StationList sList;
		Intent i;
		switch (position) {
		case 0:
			stationList = dbHandler.getAllHistoryStations();
			sList = new StationList(stationList);
			i = new Intent(TelepadMainActivity.this,TelepadDisplayStationListActivity.class);
			i.setAction(getString(R.string.radiothek_action_history));
			i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable),sList);
			startActivity(i);
			break;
		case 1:
			i = new Intent(TelepadMainActivity.this, StationService.class);
			i.setAction(getString(R.string.radiothek_stationservice_startgenreactivity));
			startService(i);
			break;
		case 2:
			i = new Intent(TelepadMainActivity.this, StationService.class);
			i.setAction(getString(R.string.radiothek_stationservice_startcountryactivity));
			startService(i);
			break;
		case 3:
			stationList = dbHandler.getAllFavoriteStations();
			sList = new StationList(stationList);
			i = new Intent(TelepadMainActivity.this,TelepadDisplayStationListActivity.class);
			i.setAction(getString(R.string.radiothek_action_favorite));
			i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable),
					sList);
			startActivity(i);
			break;
		}

	}

}
