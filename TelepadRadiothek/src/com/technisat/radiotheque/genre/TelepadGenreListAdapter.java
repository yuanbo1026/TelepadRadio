package com.technisat.radiotheque.genre;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;
import com.technisat.telepadradiothek.R;

public class TelepadGenreListAdapter extends BaseAdapter {

	private List<Genre> mGenreItems = new ArrayList<Genre>();
	private Context mContext;
	// HorzGridView stuff
	private final int childLayoutResourceId = R.layout.telepad_genre_gridview_item;
	private int rows;// used with TwoWayGridView
	private int columnWidth;
	private int rowHeight;

	public int pageIndex = 0;
	public final static int VIEW_COUNT = 8;

	public TelepadGenreListAdapter(Context context, List<Genre> genreList) {
		mGenreItems = new ArrayList<Genre>(genreList);
		mContext = context;
		LayoutInflater.from(context);
		ImageLoader.getInstance();
		rows = 2;
		TelepadGenreActivity.gridView.setNumRows(rows);
		ViewTreeObserver vto = TelepadGenreActivity.gridView
				.getViewTreeObserver();
		OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				rowHeight = 250;
				columnWidth = 256;

				TelepadGenreActivity.gridView.setRowHeight(rowHeight);

				// Then remove the listener
				ViewTreeObserver vto = TelepadGenreActivity.gridView
						.getViewTreeObserver();

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					vto.removeOnGlobalLayoutListener(this);
				} else {
					vto.removeGlobalOnLayoutListener(this);
				}

			}
		};

		vto.addOnGlobalLayoutListener(onGlobalLayoutListener);
	}

	@Override
	public int getCount() {
		return mGenreItems.size();
		/**
		 * leave this paging issue behind
		 */
//		 int ori = VIEW_COUNT * pageIndex;
//
//         if (mGenreItems.size() - ori < VIEW_COUNT) {
//               return mGenreItems.size() - ori;
//         } else {
//               return VIEW_COUNT;
//         }
	}

	@Override
	public Object getItem(int i) {
//		Log.e("TelepadGenreListAdapter","Object getItem(int i);  pageIndex: "+pageIndex);
		return mGenreItems.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

//		final Genre g = (Genre) getItem(position+VIEW_COUNT * pageIndex);
		final Genre g = (Genre) getItem(position);
		ViewHandler handler;
//		Log.e("Telepad","getVIEW-->position: "+(position+VIEW_COUNT * pageIndex));
//		Log.e("TelepadGenreListAdapter","getItem(position) :"+position);
		

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(childLayoutResourceId, parent, false);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					columnWidth, rowHeight);

			handler = new ViewHandler();
			handler.ll = (LinearLayout) convertView
					.findViewById(R.id.telepad_genre_grid_item_ll);
			handler.ll.setLayoutParams(lp);
			handler.tv = (TextView) convertView
					.findViewById(R.id.telepad_genre_grid_item_tv);
			convertView.setTag(handler);

		} else {
			handler = (ViewHandler) convertView.getTag();
		}
		// "station_list_item_bg_colors"

		TypedArray colorArray = mContext.getResources().obtainTypedArray(
				R.array.genre_list_item_bg_colors);
		int indentify;
		if (position >= 8) {
			indentify = colorArray.getResourceId(position % 8, -1); // mContext.getResources().getIdentifier(colorArray[position
																	// % 8],
																	// "color",
																	// mContext.getPackageName());
		} else {
			indentify = colorArray.getResourceId(position, -1); // mContext.getResources().getIdentifier(colorArray[position],
																// "color",
																// mContext.getPackageName());
		}
		handler.ll.setBackgroundResource(indentify);
		colorArray.recycle();

		/**
		 * calculate position of the item, and set the color
		 */

		handler.tv.setText(g.getName());
		// handler.tv.setTextColor(R.drawable.general_text_color);

		handler.ll.setClickable(true);
		handler.ll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext,
						TelepadDisplayStationListActivity.class);
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

		return convertView;
	}

	private class ViewHandler {
		TextView tv;
		LinearLayout ll;
	}

}
