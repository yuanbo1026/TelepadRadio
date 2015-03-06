package com.technisat.radiotheque.Drawer;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnGenericMotionListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.technisat.radiotheque.android.Misc;
import com.technisat.telepadradiothek.R;

public class NavDrawerListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private ArrayList<NavDrawerItem> navDrawerItemsVisible = new ArrayList<NavDrawerItem>();

	public NavDrawerListAdapter(Context context,
			ArrayList<NavDrawerItem> navDrawerItems) {
		this.context = context;
		this.navDrawerItems = navDrawerItems;

		for (NavDrawerItem item : this.navDrawerItems) {
			if (item.isVisible()) {
				this.navDrawerItemsVisible.add(item);
			}
		}
	}

	@Override
	public int getCount() {
		return navDrawerItemsVisible.size();
	}

	@Override
	public Object getItem(int position) {
		return navDrawerItemsVisible.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setItemVisibility(int id, boolean isVisible) {
		navDrawerItemsVisible.clear();
		for (NavDrawerItem item : navDrawerItems) {
			if (item.getId() == id) {
				item.setVisiblity(isVisible);
			}

			if (item.isVisible()) {
				navDrawerItemsVisible.add(item);
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}
		final ImageView imgIcon = (ImageView) convertView
				.findViewById(R.id.icon);
		final TextView txtTitle = (TextView) convertView
				.findViewById(R.id.title);

		txtTitle.setTypeface(Misc.getCustomFont(context, Misc.FONT_LIGHT));

		imgIcon.setImageResource(navDrawerItemsVisible.get(position).getIcon());
		txtTitle.setText(navDrawerItemsVisible.get(position).getTitle());
		RelativeLayout mRelativeLayout = (RelativeLayout)convertView;
		mRelativeLayout.setOnGenericMotionListener(new OnGenericMotionListener(){

			@Override
			public boolean onGenericMotion(View v, MotionEvent event) {
				return false;
			}
			 
		 });
		
		return convertView;
	}

}