package com.technisat.radiotheque.stationlist;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.IStationMetadataUpdate;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.service.MediaPlayerService;
import com.technisat.radiotheque.station.StationActivity;

public class TelepadItemListAdapter extends ArrayAdapter<Station> implements
		IStationMetadataUpdate {

	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private DatabaseHandler mDBhandler;
	private Context mContext;
	private List<Station> mStationList;
	private boolean mIsFavList;
	private int mLayoutId;

	public TelepadItemListAdapter(Context context, int textViewResourceId,
			List<Station> objects, boolean isFavList, int layoutId) {
		super(context, textViewResourceId, objects);

		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
		mDBhandler = new DatabaseHandler(context);
		mContext = context;
		mStationList = new ArrayList<Station>(objects);
		mIsFavList = isFavList;
		mLayoutId = layoutId;

	}

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
					R.color.Grid_Item_country));

			if (mLayoutId == R.layout.telepad_stationlist_list_item) {
				item = new Item();
				
				item.ll = (LinearLayout)v.findViewById(R.id.telepad_tv_stationlist_item_linearlayout);
				item.ivFavIcon = (ImageView) v
						.findViewById(R.id.telepad_iv_stationlist_item_favicon);
				

				item.tvStationGenre = (TextView) v
						.findViewById(R.id.telepad_tv_stationlist_item_stationgenre);
				item.tvStationName = (TextView) v
						.findViewById(R.id.telepad_tv_stationlist_item_stationname);
				item.tvStationGenre.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.tvStationName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));

			} else {
				// grid layout : unused right now

				item = new Item();
				item.tvStationGenre = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationgenre_grid);
				item.tvStationName = (TextView) v
						.findViewById(R.id.tv_stationlist_item_stationname_grid);
				item.tvStationGenre.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_NORMAL));
				item.tvStationName.setTypeface(Misc.getCustomFont(mContext,
						Misc.FONT_BOLD));
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
			
//			item.ll.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View v) {
//					Intent i = new Intent(mContext, StationActivity.class);
//					i.putExtra(mContext.getString(R.string.radiothek_bundle_station), sFinal);
//					mContext.startActivity(i);					
//				}
//				
//			});

			if (mLayoutId == R.layout.telepad_stationlist_list_item) {
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

			if (Misc.isValidMetaData(s.getMetadata()))
				item.tvStationGenre.setText(s.getMetadata());
			else
				item.tvStationGenre.setText("");
			item.tvStationName.setText(s.getStationName());

			if (mLayoutId == R.layout.telepad_stationlist_list_item) {
				if (s.isFav()) {
					item.ivFavIcon.setImageResource(R.drawable.ic_player_fav);
				} else {
					item.ivFavIcon.setImageResource(R.drawable.ic_player_fav_h);
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
		ImageView ivFavIcon;
		LinearLayout ll;
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
