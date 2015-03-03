package com.technisat.radiotheque.genre;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.stationlist.DisplayStationListActivity;
import com.technisat.radiotheque.tracking.TechniTracker;
import com.technisat.radiothek.R;

public class GenreListAdapter extends BaseAdapter {

	private List<Genre> mGenreItems = new ArrayList<Genre>();
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageLoader mImageLoader;

	public GenreListAdapter(Context context, List<Genre> genreList) {

		mGenreItems = new ArrayList<Genre>(genreList);
		mContext = context;

		mInflater = LayoutInflater.from(context);
		mImageLoader = ImageLoader.getInstance();
	}

	private int getGenreIndex(Genre g) {
		int genreIndex = -1;
		for (int i = 0; i < mGenreItems.size(); i++) {
			Genre genre = mGenreItems.get(i);
			if (genre.getId() == g.getId()) {
				genreIndex = i;
				break;
			}
		}

		return genreIndex;
	}

	public void hideStationPauseButton(Genre g, Station s) {
		int genreIndex = getGenreIndex(g);

		for (Station station : mGenreItems.get(genreIndex).getsList()) {
			if (station.getId() == s.getId()) {
				station.setPlaying(false);
			}
		}

		notifyDataSetChanged();
	}

	public void showStationPauseButton(Genre g, Station s) {
		int genreIndex = getGenreIndex(g);

		for (Station station : mGenreItems.get(genreIndex).getsList()) {
			if (station.getId() == s.getId()) {
				station.setPlaying(true);
			}
		}

		notifyDataSetChanged();
	}

	public void updateStation(Genre g, Station s) {
		int genreIndex = getGenreIndex(g);

		if (mGenreItems.size() > genreIndex && genreIndex != -1)
			for (Station station : mGenreItems.get(genreIndex).getsList()) {
				if (station.getId() == s.getId()) {
					station = s;

					if (station.hasCoverAvailable()) {
						mGenreItems.get(genreIndex).getsList().set(0, station);
					} else if (station.hasStationLogoAvailable()) {
						// check if item 0 already cover so we dont want to push
						// the "just have a logo" to the front
						if (!mGenreItems.get(genreIndex).getsList().get(0)
								.hasStationLogoAvailable())
							mGenreItems.get(genreIndex).getsList()
									.set(0, station);
					} else if (Misc.isValidMetaData(station.getMetadata())) {
						if (!mGenreItems.get(genreIndex).getsList().get(0)
								.hasStationLogoAvailable())
							mGenreItems.get(genreIndex).getsList()
									.set(0, station);
					}
					break;
				}
			}

		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mGenreItems.size();
	}

	@Override
	public Object getItem(int i) {
		return mGenreItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		return 1;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = view;
		ImageView iv_cover;

		final Genre g = (Genre) getItem(i);
		Station firstStation = null;
		if (g.getsList() != null && g.getsList().size() > 0)
			firstStation = g.getsList().get(0);
		Item item;

		if (v == null) {
			v = mInflater.inflate(R.layout.gridview_item, viewGroup, false);
			item = new Item();
			item.tvName = (TextView) v.findViewById(R.id.text);
			item.tvName.setTypeface(Misc
					.getCustomFont(mContext, Misc.FONT_BOLD));
			item.ivCover = (ImageView) v.findViewById(R.id.picture);
			item.ivPauseButton = (ImageView) v
					.findViewById(R.id.siv_gridview_pauseimage);
			item.pbSpinner = (ProgressBar) v
					.findViewById(R.id.pb_gridview_spinner);
			item.ivListButton = (ImageView) v
					.findViewById(R.id.iv_gridview_listbutton);
			

			v.setTag(item);
		} else {
			item = (Item) v.getTag();
		}
		iv_cover = (ImageView)v
				.findViewById(R.id.iv_cover_transparent);
		if (TechniTracker.isPad()) {
			LayoutParams params = item.tvName.getLayoutParams();
			params.height = params.height = mContext.getResources()
					.getDimensionPixelSize(R.dimen.grid_item_board_height);
			item.tvName.setLayoutParams(params);
			item.tvName.setTextSize(mContext.getResources().getDimension(R.dimen.textsize));

			LayoutParams params2 = item.ivListButton.getLayoutParams();
			params2.height = params.height = mContext.getResources()
					.getDimensionPixelSize(R.dimen.grid_item_board_height);
			item.ivListButton.setLayoutParams(params2);

			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.setMargins(0, 0, 0, mContext.getResources()
					.getDimensionPixelSize(R.dimen.grid_item_board_height));
			item.ivCover.setLayoutParams(lp);
			item.ivPauseButton.setLayoutParams(lp);
			
			iv_cover.setLayoutParams(lp);
			
		}

		item.ivListButton.setClickable(true);
		item.ivListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext,
						DisplayStationListActivity.class);
				i.putExtra(
						mContext.getString(R.string.radiothek_bundle_stationlistparcelable),
						new StationList(g.getsList()));

				i.setAction(mContext
						.getString(R.string.radiothek_action_morestationsofgenre));
				i.putExtra(mContext.getString(R.string.radiothek_bundle_long),
						g.getId());
				i.putExtra(mContext.getString(R.string.radiothek_bundle_int),
						50);

				mContext.startActivity(i);
			}
		});

		// item.tvName.setText(g.getName() + " - " +
		// g.getsList().get(0).getMetadata());
		item.tvName.setText(g.getName());

		if (firstStation != null) {
			// check if pause button visible
			if (firstStation.isPlaying()) {
				item.ivPauseButton.setVisibility(View.VISIBLE);
				iv_cover.setVisibility(View.VISIBLE);
			} else {
				item.ivPauseButton.setVisibility(View.GONE);
				iv_cover.setVisibility(View.GONE);
			}

			// check covers
			if (firstStation.hasCoverAvailable()) {
				mImageLoader.displayImage(g.getsList().get(0).getCoverUrl(),
						item.ivCover, new ImageLoaderSpinner(item.pbSpinner));
			} else {
				if (firstStation.hasStationLogoAvailable()) {
					mImageLoader.displayImage(g.getsList().get(0)
							.getStationLogoUrl(), item.ivCover,
							new ImageLoaderSpinner(item.pbSpinner));
				} else {
					mImageLoader.displayImage("", item.ivCover);
					item.ivCover
							.setImageResource(R.drawable.ic_default_station);
				}
			}
		}
		return v;
	}

	private static class Item {
		TextView tvName;
		ImageView ivCover;
		ImageView ivPauseButton;
		ProgressBar pbSpinner;
		ImageView ivListButton;
	}

}