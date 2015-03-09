package com.technisat.radiotheque.genre;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.jess.ui.TwoWayAbsListView;
import com.jess.ui.TwoWayAbsListView.OnScrollListener;
import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.CountryList;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.GenreList;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.service.MediaPlayerService;

public class TelepadGenreActivity extends FragmentActivity implements
		com.jess.ui.TwoWayAdapterView.OnItemClickListener {

	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;

	private GenreList mGenreList;
	private TelepadGenreListAdapter mGenreGridAdapter;

	private CountryList mCountryList;
	private CountryListAdapter mCountryGridAdapter;

	public static TwoWayGridView gridView;

	private boolean mIsScrollingUp;
	private int mLastFirstVisibleItem = 0;

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
		inflater.inflate(R.layout.activity_telepad_genre, container);

		Log.d("nexxoo", "starte telepad_genreActivity");
		if (getIntent().getExtras() != null) {
			gridView = (TwoWayGridView) findViewById(R.id.telepad_genre_gridview);
			mGenreList = getIntent().getExtras().getParcelable(
					getString(R.string.radiothek_bundle_genreparceable));
			mCountryList = getIntent().getExtras().getParcelable(
					getString(R.string.radiothek_bundle_countryparceable));

			if (mGenreList != null) {
				mGenreGridAdapter = new TelepadGenreListAdapter(this,
						mGenreList.getGenreList());
				gridView.setAdapter(mGenreGridAdapter);
			} else {
				if (mCountryList != null) {
					mGenreGridAdapter = null;
					mCountryGridAdapter = new CountryListAdapter(this,
							mCountryList.getCountryList());
					gridView.setAdapter(mCountryGridAdapter);
				}
			}
			gridView.setOnItemClickListener(this);
			gridView.setOnScrollListener(new OnScrollListener() {
				private int currentVisibleItemCount;
				private int currentScrollState;
				private int currentFirstVisibleItem;
				private int totalItem;

				private void isScrollCompleted(boolean isScrollUp) {
					if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
							&& this.currentScrollState == SCROLL_STATE_IDLE) {
						/** To do code here */
						if (isScrollUp)
							mGenreGridAdapter.pageIndex++;
						else
							mGenreGridAdapter.pageIndex--;
						Log.e("Telepad",
								"OnScrollListener mGenreGridAdapter.pageIndex :"
										+ mGenreGridAdapter.pageIndex);
						mGenreGridAdapter.notifyDataSetChanged();

					}

				}

				@Override
				public void onScrollStateChanged(TwoWayAbsListView view,
						int scrollState) {
					
					if (view.getId() == gridView.getId()) {
				        final int currentFirstVisibleItem = gridView.getFirstVisiblePosition();

				        if (currentFirstVisibleItem > mLastFirstVisibleItem) {
				            mIsScrollingUp = false;
				        } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
				            mIsScrollingUp = true;
				        }

				        mLastFirstVisibleItem = currentFirstVisibleItem;
				    }
					
					
//					this.currentScrollState = scrollState;
//					if (scrollState == 0)// scrolling stopped
//					{
//						if (view.getId() == gridView.getId()) {
//							final int currentFirstVisibleItem = gridView
//									.getFirstVisiblePosition();
//							if (currentFirstVisibleItem > mLastFirstVisibleItem) {
//								mIsScrollingUp = true;
//								Log.e("a", "scrolling down...");
//							} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
//								mIsScrollingUp = false;
//								Log.e("a", "scrolling up...");
//							}else if(currentFirstVisibleItem == mLastFirstVisibleItem){
//								return;
//							}
//							
//							 mLastFirstVisibleItem = currentFirstVisibleItem;
//
//						}
//						this.isScrollCompleted(mIsScrollingUp);
//					}

				}

				@Override
				public void onScroll(TwoWayAbsListView view,
						int firstVisibleItem, int visibleItemCount,
						int totalItemCount) {
					this.currentFirstVisibleItem = firstVisibleItem;
					this.currentVisibleItemCount = visibleItemCount;
					this.totalItem = totalItemCount;

				}
			});
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMyDrawer != null)
			mMyDrawer.finalize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_new, menu);

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

	// private OnItemClickListener onItemClickListener = new
	// OnItemClickListener() {
	//
	// @Override
	// public void onItemClick(AdapterView<?> parent, View v, int position,
	// long id) {
	//
	// if(mGenreGridAdapter != null){
	// //we have a Genre
	// Genre genre = (Genre) mGenreGridAdapter.getItem(position);
	// if (genre.getsList() != null && genre.getsList().size() > 0){
	// Intent i = new Intent(TelepadGenreActivity.this,
	// MediaPlayerService.class);
	// i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
	// i.putExtra(getString(R.string.radiothek_bundle_station),
	// genre.getsList().get(0));
	// startService(i);
	// } else {
	// Toast.makeText(TelepadGenreActivity.this,
	// getString(R.string.genreactivity_text_nostationavailable),
	// Toast.LENGTH_SHORT).show();
	// }
	// return;
	// }
	//
	// if (mCountryGridAdapter != null){
	// //we have a Country
	// Country country = (Country) mCountryGridAdapter.getItem(position);;
	// if (country.getsList() != null && country.getsList().size() > 0){
	// Intent i = new Intent(TelepadGenreActivity.this,
	// MediaPlayerService.class);
	// i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
	// i.putExtra(getString(R.string.radiothek_bundle_station),
	// country.getsList().get(0));
	// startService(i);
	// } else {
	// Toast.makeText(TelepadGenreActivity.this,
	// getString(R.string.genreactivity_text_nostationavailable),
	// Toast.LENGTH_SHORT).show();
	// }
	// return;
	// }
	// }
	// };

	@Override
	public void onItemClick(TwoWayAdapterView<?> parent, View view,
			int position, long id) {
		if (mGenreGridAdapter != null) {
			// we have a Genre
			Genre genre = (Genre) mGenreGridAdapter.getItem(position);
			if (genre.getsList() != null && genre.getsList().size() > 0) {
				Intent i = new Intent(TelepadGenreActivity.this,
						MediaPlayerService.class);
				i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
				i.putExtra(getString(R.string.radiothek_bundle_station), genre
						.getsList().get(0));
				startService(i);
			} else {
				Toast.makeText(
						TelepadGenreActivity.this,
						getString(R.string.genreactivity_text_nostationavailable),
						Toast.LENGTH_SHORT).show();
			}
			return;
		}

		if (mCountryGridAdapter != null) {
			// we have a Country
			Country country = (Country) mCountryGridAdapter.getItem(position);
			;
			if (country.getsList() != null && country.getsList().size() > 0) {
				Intent i = new Intent(TelepadGenreActivity.this,
						MediaPlayerService.class);
				i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
				i.putExtra(getString(R.string.radiothek_bundle_station),
						country.getsList().get(0));
				startService(i);
			} else {
				Toast.makeText(
						TelepadGenreActivity.this,
						getString(R.string.genreactivity_text_nostationavailable),
						Toast.LENGTH_SHORT).show();
			}
			return;
		}

	}

}
