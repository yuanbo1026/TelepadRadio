package com.technisat.radiotheque.stationlist;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.vending.billing.IInAppBillingService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.IStationMetadataUpdate;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.tracking.TechniTracker;
import com.technisat.telepadradiothek.R;

/**
 * Handles the list layout part of that activity
 * 
 */
public class ItemListAdapter extends ArrayAdapter<Station> implements
		IStationMetadataUpdate {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DatabaseHandler mDBhandler;
	private Context mContext;
	private List<Station> mStationList;
	private boolean mIsFavList;
	private int mLayoutId;

	static final String TAG = "Radiotheque_In_App_Purchase";
	public IInAppBillingService mService = null;
	String price;

	static final String ITEM_SKU = "android.test.purchased";
	static final String RADIO_SKU = "radiotheque_purchase_all_stations";

	public ItemListAdapter(Context context, int textViewResourceId,
			List<Station> objects, boolean isFavList, int layoutId) {
		super(context, textViewResourceId, objects);

		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mDBhandler = new DatabaseHandler(context);
		mContext = context;
		mStationList = new ArrayList<Station>(objects);
		mIsFavList = isFavList;
		mLayoutId = layoutId;

		if (PreferenceManager.getDefaultSharedPreferences(mContext).contains(
				"price")) {
			context.bindService(new Intent(
					"com.android.vending.billing.InAppBillingService.BIND"),
					mServiceConn, Context.BIND_AUTO_CREATE);
		}
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Station s = getItem(position);

		s.updateMetaData();
		s.addMetadataListener(this);

		// check if station is fav
		if (mDBhandler != null) {
			s.setFav(mDBhandler.isFav(s));
		}

		Item item = null;
		View v = convertView;

		if (v == null) {
			v = mInflater.inflate(mLayoutId, parent, false);
			v.setBackgroundColor(mContext.getResources().getColor(
					R.color.RealWhite));

			if (mLayoutId == R.layout.stationlist_list_item) {
				item = new Item();
				item.ivFavIcon = (ImageView) v
						.findViewById(R.id.iv_stationlist_item_favicon);
				item.ivPauseButton = (ImageView) v
						.findViewById(R.id.iv_stationlist_item_pausebutton);
				item.ivStationLogo = (ImageView) v
						.findViewById(R.id.iv_stationlist_item_stationlogo);

				item.tvStationGenre = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationgenre);
				item.tvStationName = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationname);
				item.tvStationGenre.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.tvStationName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.pbSpinner = (ProgressBar) v
						.findViewById(R.id.pb_stationlist_item_spinner);

			} else {
				// grid layout : unused right now

				item = new Item();
				item.ivPauseButton = (ImageView) v
						.findViewById(R.id.iv_stationlist_item_pausebutton_grid);
				item.ivStationLogo = (ImageView) v
						.findViewById(R.id.iv_stationlist_item_stationlogo_grid);

				item.tvStationGenre = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationgenre_grid);
				item.tvStationName = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationname_grid);
				item.tvStationGenre.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.tvStationName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
				item.pbSpinner = (ProgressBar) v
						.findViewById(R.id.pb_stationlist_item_spinner_grid);
			}

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}

		/**
		 * This part is moved outside the if clause above, otherwise button
		 * clicks and things like that would work with a wrong item id, because
		 * they will not be updated correctly
		 */
		if (s != null) {
			final Station sFinal = s;

			if (mLayoutId == R.layout.stationlist_list_item) {
				item.ivFavIcon.setClickable(true);

				/** Handles the click on Favorite Icon */
				item.ivFavIcon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						toggleFavStatus(sFinal);
						if (mIsFavList && !sFinal.isFav()) {
							remove(sFinal);
						}
					}
				});
			}

			item.ivPauseButton.setClickable(true);
			item.ivPauseButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(mContext, MediaPlayerService.class);
					i.setAction(mContext
							.getString(R.string.radiothek_mediaplayerservice_stopstream));
					i.putExtra(mContext
							.getString(R.string.radiothek_bundle_station),
							sFinal);
					mContext.startService(i);
				}
			});

			if (Misc.isValidMetaData(s.getMetadata()))
				item.tvStationGenre.setText(s.getMetadata());
			else
				item.tvStationGenre.setText("");
			item.tvStationName.setText(s.getStationName());

			if (s.isPlaying()) {
				item.ivPauseButton.setVisibility(View.VISIBLE);
				v.setBackgroundColor(mContext.getResources().getColor(
						R.color.RadiothekHighlightColor));
			} else {
				item.ivPauseButton.setVisibility(View.GONE);
				v.setBackgroundColor(mContext.getResources().getColor(
						R.color.RealWhite));
			}

			if (s.hasStationLogoAvailable()) {

				if (mLayoutId == R.layout.stationlist_list_item) {
					// adapter for the list
					mImageLoader.displayImage(s.getStationLogoSmall(),
							item.ivStationLogo, new ImageLoaderSpinner(
									item.pbSpinner));
				} else {
					// adapter for the grid
					mImageLoader.displayImage(s.getStationLogoMedium(),
							item.ivStationLogo, new ImageLoaderSpinner(
									item.pbSpinner));
				}

			} else {
				mImageLoader.displayImage("", item.ivStationLogo);
				item.ivStationLogo
						.setImageResource(R.drawable.ic_default_station);
			}

			if (mLayoutId == R.layout.stationlist_list_item) {
				if (s.isFav()) {
					item.ivFavIcon.setImageResource(R.drawable.ic_player_fav);
				} else {
					item.ivFavIcon.setImageResource(R.drawable.ic_player_fav_h);
				}
			}

			/**
			 * added for changing purchase item background color 
			 * by b.yuan
			 */
			SharedPreferences prefs = mContext.getSharedPreferences("iab",
					Context.MODE_PRIVATE);
			// price image
			ImageView iv_buy = (ImageView) v
					.findViewById(R.id.iv_stationlist_item_buy);
			ImageView iv_cover = (ImageView) v
					.findViewById(R.id.iv_stationlist_item_pauseCoverImage);
			LinearLayout ll_buy_all = (LinearLayout) v
					.findViewById(R.id.purchase_info_linearlayout);

			// tv_stationlist_item_linearlayout
			ImageView iv_divide = (ImageView) v.findViewById(R.id.iv_divide);

			if (!TechniTracker.isBuildByTechniSat()) {
				if (!prefs.getBoolean("mIsPurchased", false)) {

					if (position > 5) {
						v.setBackgroundColor(mContext.getResources().getColor(
								R.color.IABItemBackground));
						item.ivFavIcon.setClickable(false);
						item.ivStationLogo.setClickable(false);
						iv_cover.setVisibility(View.VISIBLE);

						if (position == 6) {
							iv_buy.setVisibility(View.GONE);
							ll_buy_all.setVisibility(View.VISIBLE);

							iv_divide.setVisibility(View.GONE);
							iv_cover.setVisibility(View.GONE);
							item.ivFavIcon.setVisibility(View.GONE);
							item.ivStationLogo.setVisibility(View.GONE);
							item.tvStationGenre.setVisibility(View.GONE);
							item.tvStationName.setVisibility(View.GONE);
						} else {
							iv_buy.setVisibility(View.GONE);
							ll_buy_all.setVisibility(View.GONE);

							iv_divide.setVisibility(View.VISIBLE);
							iv_cover.setVisibility(View.VISIBLE);
							item.ivFavIcon.setVisibility(View.VISIBLE);
							item.ivStationLogo.setVisibility(View.VISIBLE);
							item.tvStationGenre.setVisibility(View.VISIBLE);
							item.tvStationName.setVisibility(View.VISIBLE);
						}

					} else {
						item.ivFavIcon.setVisibility(View.VISIBLE);
						item.ivStationLogo.setClickable(true);
						iv_buy.setVisibility(View.GONE);
						iv_cover.setVisibility(View.GONE);
						ll_buy_all.setVisibility(View.GONE);
					}
				} else {
					item.ivFavIcon.setVisibility(View.VISIBLE);
					item.ivFavIcon.setClickable(true);
					item.ivStationLogo.setClickable(true);
					iv_buy.setVisibility(View.GONE);
					iv_cover.setVisibility(View.GONE);
					ll_buy_all.setVisibility(View.GONE);
				}
			}

		}

		return v;
	}

	@Override
	public int getCount() {
		return mStationList.size();
	}

	@Override
	public Station getItem(int position) {
		return mStationList.get(position);
	}

	@Override
	public int getPosition(Station item) {
		return mStationList.indexOf(item);
	}

	@Override
	public void remove(Station object) {
		mStationList.remove(object);
		notifyDataSetChanged();
		super.remove(object);
	}

	private static class Item {
		TextView tvStationName;
		TextView tvStationGenre;
		ImageView ivStationLogo;
		ImageView ivPauseButton;
		ProgressBar pbSpinner;
		ImageView ivFavIcon;
	}

	public void toggleFavStatus(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setFav(!mDBhandler.isFav(s));
				mDBhandler.insertStation(s);
				break;
			}
		}
		notifyDataSetChanged();
	}

	public void showPauseButton(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setPlaying(true);
			}
		}
		notifyDataSetChanged();
	}

	public void hidePauseButton(Station station) {
		for (Station s : mStationList) {
			if (s.getId() == station.getId()) {
				s.setPlaying(false);
			}
		}
		notifyDataSetChanged();
	}

	/* Interface IStationMetaUpdate */

	@Override
	public void onMetadataUpdate(Station station) {
		notifyDataSetChanged();
	}
}