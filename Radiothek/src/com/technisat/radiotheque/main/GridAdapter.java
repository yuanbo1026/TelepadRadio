package com.technisat.radiotheque.main;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.Misc;

/**
 * Adapter to handle and display the Main Menu Buttons
 */
public class GridAdapter extends ArrayAdapter<MainMenuButton> {
	
	private HashMap<MainMenuButton, Integer> mIdMap = new HashMap<MainMenuButton, Integer>();
	private Context mContext;

	public GridAdapter(Context context, int resource, List<MainMenuButton> objects) {
		super(context, resource, objects);
		mContext = context;
		for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.main_grid_item, parent, false);
	    	    	    
	    MainMenuButton button = getItem(position);
	    
	    ImageView iv = (ImageView) rowView.findViewById( R.id.iv_main_grid_item_icon );
	    TextView tv = (TextView) rowView.findViewById( R.id.tv_main_grid_item_text );
	    tv.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_NORMAL));	    
	    
	    iv.setImageResource(button.getIconResource());
	    tv.setText(button.getCaption());
		
		return rowView;
	}
	
	@Override
    public long getItemId(int position) {
      MainMenuButton item = getItem(position);
      return mIdMap.get(item);
    }

	@Override
	public boolean hasStableIds() {
		return true;
	}
}