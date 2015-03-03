package com.technisat.radiotheque.stationlist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.vending.billing.IInAppBillingService;
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.broadcast.IMediaPlayerBroadcastListener;
import com.technisat.radiotheque.broadcast.MediaPlayerBroadcastReceiver;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.loadstations.ILoadStations;
import com.technisat.radiotheque.loadstations.LoadStationsOfCountryAsyncTask;
import com.technisat.radiotheque.loadstations.LoadStationsOfGenreAsyncTask;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.player.PlayerFragement;
import com.technisat.radiotheque.player.PlayerFragement.IPlayerFragment;
import com.technisat.radiotheque.radiodb.NexxooWebservice;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.stationlist.search.ISearchCallback;
import com.technisat.radiotheque.stationlist.search.SearchAsyncTask;
import com.technisat.radiotheque.tracking.TechniTracker;
import com.technisat.radiothek.R;

public class DisplayStationListActivity extends FragmentActivity implements
		IPlayerFragment, ISearchCallback, IMediaPlayerBroadcastListener,
		ILoadStations {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private ListView mGehoertListView;
	private ItemListAdapter mItemListAdapter;
	private List<Station> stationList;

	private PlayerFragement mPlayerFragment;
	private MediaPlayerBroadcastReceiver mBroadcast;

	private ProgressDialog mProgressBar;
	private boolean mIsFavList = false;

	private ViewSwitcher mViewSwitcher;
	// private GridView mGehoertGridView;
	private ItemListAdapter mItemGridAdapter;

	public static final int VIEW_LIST = 0;
	public static final int VIEW_GRID = 1;

	// The helper object
	IabHelper mHelper;

	static final String TAG = "Radiotheque_In_App_Purchase";
//	static final String ITEM_SKU = "test_item";
	static final String ITEM_SKU = "radiotheque_purchase_all_stations";
//	static final String ITEM_SKU = "android.test.purchased";
	private String price;

	private Context mContext;
	public IInAppBillingService mService = null;

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
		inflater.inflate(R.layout.activity_displaystationlist, container);

		mContext = this;

		/**
		 * public_key signed by Google developer console
		 */
		if (!TechniTracker.isBuildByTechniSat()) {

			String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmUeN8v6XI7UXwZuI7VnfoXi0/Gznv6k5htCNMWn2fCNFjOTYd08bHBQYUxlHE73CYIaKaQRUseOnq677i+SnmQGR1zXZEGnsn4XKHg4iD/gtcCH2akFZKXPA5n+j40vEuxvGK5AXtN+3pZyzWD+KFUOxwFav7lGtDw58kri6Afuccuwa6Q9+Vq7M5xihs2KZgvBnUlWdQDOMAkPEHcbmEf+Ghcm5vNBrjkyovMsYjWOA362iai9w7N0S4YjCPFnqVFjapbsH0+D6aamcuBV8oVEIoiyqTcdytMhOxXJzCKf5TQZ9o9w4UHPSIDGTV4jZ6JogyAcJRPeMEhTzLBRoHwIDAQAB";
			mHelper = new IabHelper(this, base64EncodedPublicKey);

			mHelper.enableDebugLogging(true);

			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {

					if (!result.isSuccess()) {
						Log.d(TAG, "In-app Billing setup failed: " + result);
						Toast.makeText(mContext,
								"Google Play Service  failed to start.",
								Toast.LENGTH_SHORT).show();
					} else {
						Log.e(TAG, "In-app Billing is set up OK");
					}

					// get an inventory of stuff we own.
					Log.e(TAG, "Setup successful. Querying inventory.");
					ArrayList<String> skuList = new ArrayList<String>();
					skuList.add(ITEM_SKU);
					mHelper.queryInventoryAsync(true, skuList,
							mGotInventoryListener);
					/**
					 * gather price from google play
					 */
					SharedPreferences prefs = getSharedPreferences("iab",Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					
					price = prefs.getString("price", null);
					Log.e(TAG, "get and save price into sharedpreference:"
							+ price);
					if (mService != null && price != null && price.isEmpty()) {
						// get the price
						skuList.add(ITEM_SKU);

						Bundle querySkus = new Bundle();
						querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

						Bundle skuDetails;
						try {
							skuDetails = mService.getSkuDetails(3,
									mContext.getPackageName(), "inapp", querySkus);
							int response = skuDetails.getInt("RESPONSE_CODE");
							if (response == 0) {
								ArrayList<String> responseList = skuDetails
										.getStringArrayList("DETAILS_LIST");

								for (String thisResponse : responseList) {
									JSONObject object = new JSONObject(thisResponse);
									price = object.getString("price").replaceAll("\\s", "");
									editor.putString("price", price);
									editor.commit();
									Log.e(TAG, "get and save price into sharedpreference:"
											+ price);
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}
			});
		} 

		Intent intent = getIntent();
		handleIntent(intent);

		mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewswitcher);
		mViewSwitcher.setDisplayedChild(VIEW_LIST);

		// Get fragment for player
		mPlayerFragment = (PlayerFragement) getSupportFragmentManager()
				.findFragmentById(R.id.fg_stationlist_player);

		mBroadcast = new MediaPlayerBroadcastReceiver(this, mPlayerFragment);
		mBroadcast.addListener(this);
		registerReceiver(mBroadcast.getReceiver(), mBroadcast.getIntentFiler());
		mBroadcast.requestUpdateFromService();
	}
	
	public ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.w("done", "connected");
			mService = IInAppBillingService.Stub.asInterface(service);
		}
	};
//	private void alert(String message) {
//		AlertDialog.Builder bld = new AlertDialog.Builder(this);
//		bld.setMessage(message);
//		bld.setNeutralButton("OK", null);
//		bld.create().show();
//	}

	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.e(TAG, "Query inventory finished.");
			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				// alert("Failed to query inventory: " + result);
//				Toast.makeText(mContext,
//						"Failed to gather product information.",
//						Toast.LENGTH_SHORT).show();
				return;
			} else {
				if (inventory != null) {
					// Log.d(TAG, "Query inventory was successful.");
					/**
					 * after query inventory
					 * 
					 * @author b.yuan
					 */
					Boolean mIsPurchased = inventory.hasPurchase(ITEM_SKU);
					SkuDetails sku_detail = inventory.getSkuDetails(ITEM_SKU);
					if (sku_detail != null) {
						 Log.e(TAG, "check if the user has purchased :"
						 + mIsPurchased + " Product price is "
						 + sku_detail.getPrice());
						SharedPreferences.Editor spe = getSharedPreferences("iab",Context.MODE_PRIVATE).edit();
						spe.putBoolean("mIsPurchased", mIsPurchased);
						spe.putString("price", sku_detail.getPrice());
				        spe.commit();
				        Log.e(TAG+" on IABSetup", "set mIsPurchased into SharedPreference mIsPurchased :"+mIsPurchased+" price :"+sku_detail.getPrice());
					}

				}
			}

		}
	};

	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			if (result.isFailure()) {
				// Handle error
				return;
			} else if (purchase.getSku().equals(ITEM_SKU)) {
				SharedPreferences prefs = getSharedPreferences("iab",
						Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putBoolean("mIsPurchased", true);
				editor.commit();
				mGehoertListView = (ListView) findViewById(R.id.lv_displaystationlist_list);
				mGehoertListView.invalidateViews();
			}

		}
	};

	public void changeToList(View view) {
		if (mViewSwitcher.getDisplayedChild() == 1) {
			mViewSwitcher.showPrevious();

//			ImageButton mBList = (ImageButton) findViewById(R.id.b_list);
//			mBList.setBackgroundResource(R.color.RealWhite);
//			mBList.setImageResource(R.drawable.ic_stationlist_blue_light);
//			ImageButton mBGrid = (ImageButton) findViewById(R.id.b_grid);
//			mBGrid.setBackgroundResource(R.color.RadiothekBlueLight);
//			mBGrid.setImageResource(R.drawable.ic_grid_white);

			mBroadcast.requestUpdateFromService();
		}
	}

	public void changeToGrid(View view) {
		if (mViewSwitcher.getDisplayedChild() == 0) {
			mViewSwitcher.showNext();

//			ImageButton mBList = (ImageButton) findViewById(R.id.b_list);
//			mBList.setBackgroundResource(R.color.RadiothekBlueLight);
//			mBList.setImageResource(R.drawable.ic_stationlist);
//			ImageButton mBGrid = (ImageButton) findViewById(R.id.b_grid);
//			mBGrid.setBackgroundResource(R.color.RealWhite);
//			mBGrid.setImageResource(R.drawable.ic_grid_blue_light);

			mBroadcast.requestUpdateFromService();
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
		/**
		 * set empty list view content
		 */
		TextView emptyListTV = (TextView) findViewById(android.R.id.empty);
		mGehoertListView = (ListView) findViewById(R.id.lv_displaystationlist_list);
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
			mProgressBar
					.setMessage(getString(R.string.displaystationlistactivity_text_searchingdb));
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
		else if (intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_action_morestationsofgenre))) {

			long genreId = intent.getLongExtra(
					getString(R.string.radiothek_bundle_long), 9);
			int count = intent.getIntExtra(
					getString(R.string.radiothek_bundle_int), 50);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar
					.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			LoadStationsOfGenreAsyncTask task = new LoadStationsOfGenreAsyncTask(
					genreId, count, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
				// TODO handle this in v2
			}
		}

		/** Handle MoreStationsOfCountry Action Intent */
		else if (intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_action_morestationsofcountry))) {

			String iso = "en";
			if (intent.hasExtra(getString(R.string.radiothek_bundle_string)))
				iso = intent
						.getStringExtra(getString(R.string.radiothek_bundle_string));
			int count = intent.getIntExtra(
					getString(R.string.radiothek_bundle_int), 50);

			mProgressBar = new ProgressDialog(this);
			mProgressBar.setCancelable(true);
			mProgressBar
					.setMessage(getString(R.string.displaystationlistactivity_text_loadstations));
			mProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressBar.show();

			LoadStationsOfCountryAsyncTask task = new LoadStationsOfCountryAsyncTask(
					iso, count, this);
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
				// TODO handle this in v2
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
		StationList sList = intent
				.getParcelableExtra(getString(R.string.radiothek_bundle_stationlistparcelable));
		if (sList != null && sList.getStationList() != null) {
			stationList = sList.getStationList();

			mItemListAdapter = new ItemListAdapter(this,
					R.layout.stationlist_list_item, stationList, mIsFavList,
					R.layout.stationlist_list_item);
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
					/**
					 * here is where purchase happens
					 */
					SharedPreferences prefs = mContext.getSharedPreferences("iab",
							Context.MODE_PRIVATE);
					if(!TechniTracker.isBuildByTechniSat()){
						if(!prefs.getBoolean("mIsPurchased", false)){
							if (position <= 5) {
								Intent i = new Intent(DisplayStationListActivity.this,
										MediaPlayerService.class);
								i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
								i.putExtra(
										getString(R.string.radiothek_bundle_station), s);
								startService(i);
							} else {
								mHelper.launchPurchaseFlow(
										DisplayStationListActivity.this, ITEM_SKU,
										10001, mPurchaseFinishedListener,
										"mypurchasetoken");
							}
						}else{
							Intent i = new Intent(DisplayStationListActivity.this,
									MediaPlayerService.class);
							i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
							i.putExtra(
									getString(R.string.radiothek_bundle_station), s);
							startService(i);
						}
						
					}else{
						Intent i = new Intent(DisplayStationListActivity.this,
								MediaPlayerService.class);
						i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
						i.putExtra(
								getString(R.string.radiothek_bundle_station), s);
						startService(i);
					}
						
					

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
		unregisterReceiver(mBroadcast.getReceiver());
		super.onDestroy();

		if (mHelper != null)
			mHelper.dispose();
		mHelper = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			Toast.makeText(this, "Purchase not successful", Toast.LENGTH_LONG)
					.show();
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
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
		Intent i = new Intent(DisplayStationListActivity.this,
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
			mItemListAdapter = new ItemListAdapter(this,
					R.layout.stationlist_list_item, stationList, false,
					R.layout.stationlist_list_item);
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