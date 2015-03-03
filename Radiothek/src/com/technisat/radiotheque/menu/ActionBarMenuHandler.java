package com.technisat.radiotheque.menu;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.genre.TelepadGenreActivity;
import com.technisat.radiotheque.service.StationService;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;

/**
 * Class to handle ActoinBar Icon Clicks
 *
 */
public class ActionBarMenuHandler {
	
	public static boolean handleActionBarItemClick(Activity activity, MenuItem item){
		Intent intent;
		DatabaseHandler dbHandler = new DatabaseHandler(activity.getApplicationContext());
		List<Station> stationList;
		StationList sList;
		switch (item.getItemId()) {
		case R.id.action_genres:
			if(!activity.getClass().equals(TelepadGenreActivity.class)){ // to reuse unnecessary Activities in Stack
				intent = new Intent(activity, StationService.class);
				intent.setAction(activity.getString(R.string.radiothek_stationservice_startgenreactivity));
				activity.startService(intent);
			}
			return true;
		case R.id.action_favorites:
			stationList = dbHandler.getAllFavoriteStations();
			sList = new StationList(stationList);
			intent = new Intent(activity, TelepadDisplayStationListActivity.class);
			intent.setAction(activity.getString(R.string.radiothek_action_favorite));
			intent.putExtra(activity.getString(R.string.radiothek_bundle_stationlistparcelable), sList);
			activity.startActivity(intent);
			return true;
		case android.R.id.home:
			activity.finish();
			return true;
		}
		return false;
	}
}