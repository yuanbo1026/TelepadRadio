//package com.technisat.radiotheque.main;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.SearchManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.widget.DrawerLayout;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.GridView;
//import android.widget.RelativeLayout;
//import android.widget.SearchView;
//
//import com.technisat.radiothek.R;
//import com.technisat.radiotheque.Drawer.MyDrawer;
//import com.technisat.radiotheque.about.AboutActivity;
//import com.technisat.radiotheque.android.DatabaseHandler;
//import com.technisat.radiotheque.android.Misc;
//import com.technisat.radiotheque.entity.Station;
//import com.technisat.radiotheque.entity.StationList;
//import com.technisat.radiotheque.menu.ActionBarMenuHandler;
//import com.technisat.radiotheque.service.StationService;
//import com.technisat.radiotheque.stationlist.DisplayStationListActivity;
//
///**
// * Activity to display Main Screen
// *
// */
//public class MainActivity extends Activity {
//	
//	private MyDrawer mMyDrawer;
//	private DrawerLayout mDrawerLayout;
//	
//	private GridView mGrid;
//	private GridAdapter mGridAdapter;
//	
//	private final static int BUTTON_HISTORY = 0;
//	private final static int BUTTON_GENRE = 1;
//	private final static int BUTTON_COUNTRY = 2;
//	private final static int BUTTON_FAVORITE = 3;
//	private final static int BUTTON_SHARE = 4;
//	private final static int BUTTON_ABOUT = 5;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		
//		setContentView(R.layout.drawer_layout_container);
//		
//		mMyDrawer = new MyDrawer(this);
//        mDrawerLayout = mMyDrawer.getDrawerLayout();
//        
//        LayoutInflater inflater = getLayoutInflater();
//        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
//    	inflater.inflate(R.layout.activity_main, container);
//    	mGrid = (GridView) findViewById( R.id.gv_main_grid );
//		if (mGrid != null){
//			
//			List<MainMenuButton> buttons = new ArrayList<MainMenuButton>();
//			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_history), R.drawable.ic_history, BUTTON_HISTORY));			
//			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_genre), R.drawable.ic_genre, BUTTON_GENRE));			
//			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_country), R.drawable.ic_country, BUTTON_COUNTRY));
//			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_favs), R.drawable.ic_favs, BUTTON_FAVORITE));
////			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_Teilen), R.drawable.ic_share, BUTTON_SHARE));
////			buttons.add(new MainMenuButton(getString(R.string.mainactivity_button_about), R.drawable.ic_about, BUTTON_ABOUT));
//			
//			mGridAdapter = new GridAdapter(this, R.id.gv_main_grid, buttons);
//			mGrid.setAdapter(mGridAdapter);
//			
//			/**  Creates onItemClickListener for all Menu Buttons in Main Screen */
//			mGrid.setOnItemClickListener(new OnItemClickListener() {
//				
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					MainMenuButton button = mGridAdapter.getItem(position);
//					DatabaseHandler dbHandler = new DatabaseHandler(getApplicationContext());
//					List<Station> stationList;
//					StationList sList;
//					Intent i;
//					switch (button.getId()) {
//					case BUTTON_HISTORY:
//						stationList = dbHandler.getAllHistoryStations();
//						sList = new StationList(stationList);
//						i = new Intent(MainActivity.this, DisplayStationListActivity.class);
//						i.setAction(getString(R.string.radiothek_action_history));
//						i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
//						startActivity(i);
//						break;
//					case BUTTON_GENRE:
//						i = new Intent(MainActivity.this, StationService.class);
//						i.setAction(getString(R.string.radiothek_stationservice_startgenreactivity));
//						startService(i);
//						break;
//					case BUTTON_COUNTRY:
//						i = new Intent(MainActivity.this, StationService.class);
//						i.setAction(getString(R.string.radiothek_stationservice_startcountryactivity));
//						startService(i);
//						break;
//					case BUTTON_FAVORITE:
//						stationList = dbHandler.getAllFavoriteStations();
//						sList = new StationList(stationList);
//						i = new Intent(MainActivity.this, DisplayStationListActivity.class);
//						i.setAction(getString(R.string.radiothek_action_favorite));
//						i.putExtra(getString(R.string.radiothek_bundle_stationlistparcelable), sList);
//						startActivity(i);
//						break;
//					case BUTTON_SHARE:
//						i = new Intent(android.content.Intent.ACTION_SEND);
//	                    i.setType("text/plain");
//	                    i.putExtra(android.content.Intent.EXTRA_TEXT, Misc.getRandomShareText(MainActivity.this));
//	                    startActivity(Intent.createChooser(i, getString(R.string.mainactivity_text_sharevia)));
//						break;
//					case BUTTON_ABOUT:
//						i = new Intent(MainActivity.this, AboutActivity.class);
//						startActivity(i);
//						break;
//					}					
//				}
//			});
//		}
//	}
//	
//	private void alert(String message) {
//		AlertDialog.Builder bld = new AlertDialog.Builder(this);
//		bld.setMessage(message);
//		bld.setNeutralButton("OK", null);
//		bld.create().show();
//	}
//	
//	
//	@Override
//	protected void onDestroy() {
//		if (mMyDrawer != null)
//			mMyDrawer.finalize();
//		super.onDestroy();
//	}
//
//
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_new, menu);
//		
//		// Associate searchable configuration with the SearchView
//	    SearchManager searchManager =
//	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//	    SearchView searchView =
//	            (SearchView) menu.findItem(R.id.search).getActionView();
//	    searchView.setSearchableInfo(
//	            searchManager.getSearchableInfo(getComponentName()));
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar actions click
//        switch (item.getItemId()) {
//        case R.id.action_drawer:
//        	if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//        		mDrawerLayout.openDrawer(Gravity.RIGHT);
//        		
//        	}else{
//        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
//        	}
//            return true;
//        default:
//        	if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
//        	}
//        	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
//        }
//	}
//	
//	/**
//	 * Handles Banner Click and starts Website with URI
//	 * @param view
//	 */
//	public void bannerClick(View view){
//		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.techniplus.de"));
//		startActivity(browserIntent);
//	}
//}