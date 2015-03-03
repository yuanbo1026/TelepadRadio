package com.technisat.radiotheque.main;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.technisat.radiothek.R;

public class TelepadGridViewAdapter extends BaseAdapter {

	private Context mContext;
	private List<DataObject> data;

	// HorzGridView stuff
	private final int childLayoutResourceId = R.layout.horz_gridview_child_layout;
	private int rows;// used with TwoWayGridView
	private int columnWidth;
	private int rowHeight;

	public TelepadGridViewAdapter(Context context, List<DataObject> data) {
		this.mContext = context;
		this.data = data;
		// Get dimensions from values folders; note that the value will change
		// based on the device size but the dimension name will remain the same
		// Resources res = mContext.getResources();
		// itemPadding = (int) res.getDimension(R.dimen.horz_item_padding);
		rows = 1;
		TelepadMainActivity.horzGridView.setNumRows(rows);
		ViewTreeObserver vto = TelepadMainActivity.horzGridView
				.getViewTreeObserver();
		OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				rowHeight = 500;
				columnWidth = 256;
				
				TelepadMainActivity.horzGridView.setRowHeight(rowHeight);

				// Then remove the listener
				ViewTreeObserver vto = TelepadMainActivity.horzGridView
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data for the given position in the array
		DataObject thisData = data.get(position);

		// Use a viewHandler to improve performance
		ViewHandler handler;

		// If reusing a view get the handler info; if view is null, create it
		if (convertView == null) {

			// Only get the inflater when it's needed, then release it-which
			// isn't frequently
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater
					.inflate(childLayoutResourceId, parent, false);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(columnWidth,
					rowHeight);
			
			
			// User findViewById only when first creating the child view
			handler = new ViewHandler();
			handler.ll = (LinearLayout)convertView.findViewById(R.id.grid_item_ll_111);
			handler.ll.setLayoutParams(lp);
			handler.iv = (ImageView) convertView.findViewById(R.id.grid_item_iv_111);
			handler.tv = (TextView) convertView.findViewById(R.id.grid_item_tv_111);
			convertView.setTag(handler);

		} else {
			handler = (ViewHandler) convertView.getTag();
		}

		// Set the data outside once the handler and view are instantiated
//		convertView.setBackgroundColor(thisData.getBg_color());
//		handler.iv.setBackgroundColor(thisData.getColor());
		handler.ll.setBackgroundResource(thisData.getBg_color());
		handler.iv.setBackgroundResource(thisData.getColor());
		handler.tv.setText(thisData.getName());

		Log.d("HorzGVAdapter",
				"Position:" + position + ",children:" + parent.getChildCount());
		return convertView;
	}

	private class ViewHandler {
		ImageView iv;
		TextView tv;
		LinearLayout ll;
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

}
