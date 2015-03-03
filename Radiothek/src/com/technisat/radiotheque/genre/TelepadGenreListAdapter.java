package com.technisat.radiotheque.genre;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.technisat.radiothek.R;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.stationlist.TelepadDisplayStationListActivity;

public class TelepadGenreListAdapter extends BaseAdapter {

	private List<Genre> mGenreItems = new ArrayList<Genre>();
	private Context mContext;
	// HorzGridView stuff
	private final int childLayoutResourceId = R.layout.telepad_genre_gridview_item;
	private int rows;// used with TwoWayGridView
	private int columnWidth;
	private int rowHeight;

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
				columnWidth = 256 ;

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
	public View getView(int position, View convertView, ViewGroup parent) {

		final Genre g = (Genre) getItem(position);
		ViewHandler handler;

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

		String[] colorArray = 
				mContext.getResources().getStringArray(R.array.gridview_item_bg_colors);
		int indentify ;
		if(position>=8){
			indentify = mContext.getResources().getIdentifier(colorArray[position % 8], "color", mContext.getPackageName());
		}else{
			indentify = mContext.getResources().getIdentifier(colorArray[position], "color", mContext.getPackageName());
		}
		handler.ll.setBackgroundResource(indentify);
		
		/**
		 * calculate position of the item, and set the color
		 */
		
		
		handler.tv.setText(g.getName());
		
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
