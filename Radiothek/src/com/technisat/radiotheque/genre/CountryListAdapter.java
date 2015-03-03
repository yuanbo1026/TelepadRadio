package com.technisat.radiotheque.genre;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.ImageLoaderSpinner;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.entity.StationList;
import com.technisat.radiotheque.stationlist.DisplayStationListActivity;
import com.technisat.radiotheque.tracking.TechniTracker;

public class CountryListAdapter extends BaseAdapter{
	
	private List<Country> mCountryItems = new ArrayList<Country>();
    private LayoutInflater mInflater;
    private Context mContext;
    ImageLoader mImageLoader;

    public CountryListAdapter(Context context, List<Country> CountryList) {
    	
        mCountryItems = new ArrayList<Country>(CountryList);
        mContext = context;
    	
        mInflater = LayoutInflater.from(context);
        mImageLoader = ImageLoader.getInstance();
    }
    
    private int getCountryIndex(Country c){
    	int cIndex = -1;
    	for (int i = 0; i < mCountryItems.size(); i++) {
    		Country country = mCountryItems.get(i);
			if (country.getId() == c.getId()){
				cIndex = i;
				break;
			}
		}
    	
    	return cIndex;
    }
    
    public void hideStationPauseButton(Country c, Station s){
    	int cIndex = getCountryIndex(c);
    	
    	for (Station station : mCountryItems.get(cIndex).getsList()) {
			if (station.getId() == s.getId()){
				station.setPlaying(false);
			}
    	}
    	
    	notifyDataSetChanged();
    }
    
    public void showStationPauseButton(Country c, Station s){
    	int cIndex = getCountryIndex(c);
    	
    	for (Station station : mCountryItems.get(cIndex).getsList()) {
			if (station.getId() == s.getId()){
				station.setPlaying(true);
			}
    	}
    	
    	notifyDataSetChanged();
    }
    
    public void updateStation(Country c, Station s){
    	int cIndex = getCountryIndex(c);

    	if (mCountryItems.size() > cIndex && cIndex != -1)
    	for (Station station : mCountryItems.get(cIndex).getsList()) {
			if (station.getId() == s.getId()){
				station = s;
				
				if (station.hasCoverAvailable()) {					
					mCountryItems.get(cIndex).getsList().set(0, station);
				} else if (station.hasStationLogoAvailable()){
					//check if item 0 already cover so we dont want to push the "just have a logo" to the front
					if (!mCountryItems.get(cIndex).getsList().get(0).hasStationLogoAvailable())
						mCountryItems.get(cIndex).getsList().set(0, station);
				} else	if (Misc.isValidMetaData(station.getMetadata())){
					if (!mCountryItems.get(cIndex).getsList().get(0).hasStationLogoAvailable())
						mCountryItems.get(cIndex).getsList().set(0, station);
				}
				break;
			}
		}
    	
    	notifyDataSetChanged();
    }
    
    @Override
    public int getCount() {
        return mCountryItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mCountryItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
    	View v = view;
    	ImageView iv_cover;
        final Country c =  (Country) getItem(i);
        Station firstStation = null;
        if (c.getsList() != null && c.getsList().size() > 0)
        	firstStation = c.getsList().get(0);
        Item item;
        
        if(v == null)
        {
           v = mInflater.inflate(R.layout.gridview_item, viewGroup, false);
           item = new Item();
           item.tvName = (TextView) v.findViewById(R.id.text);
           item.tvName.setTypeface(Misc.getCustomFont(mContext, Misc.FONT_BOLD));
           item.ivCover = (ImageView) v.findViewById(R.id.picture);
           item.ivPauseButton = (ImageView) v.findViewById(R.id.siv_gridview_pauseimage);
           item.pbSpinner = (ProgressBar) v.findViewById(R.id.pb_gridview_spinner);
           item.ivListButton = (ImageView) v.findViewById(R.id.iv_gridview_listbutton);           
           
           v.setTag(item);
        }else {
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

//        item.tvName.setText(g.getName() + " - " + g.getsList().get(0).getMetadata());
        item.tvName.setText(c.getCountry()); 
        
        item.ivListButton.setClickable(true);
        item.ivListButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, DisplayStationListActivity.class);
				i.putExtra(mContext.getString(R.string.radiothek_bundle_stationlistparcelable), new StationList(c.getsList()));
				
				i.setAction(mContext.getString(R.string.radiothek_action_morestationsofcountry));
				i.putExtra(mContext.getString(R.string.radiothek_bundle_string), c.getIsoCode());
				i.putExtra(mContext.getString(R.string.radiothek_bundle_int), 50);
				mContext.startActivity(i);
			}
		});
        
        if (firstStation != null){
        	//check if pause button visible
        	if (firstStation.isPlaying()){
        		item.ivPauseButton.setVisibility(View.VISIBLE);
        	} else {
        		item.ivPauseButton.setVisibility(View.GONE);
        	}
        	        	
        	//check covers
        	if (firstStation.hasCoverAvailable()){
        		mImageLoader.displayImage(c.getsList().get(0).getCoverUrl(), item.ivCover, new ImageLoaderSpinner(item.pbSpinner));
        	} else {        		
        		if (firstStation.hasStationLogoAvailable()){
        			mImageLoader.displayImage(c.getsList().get(0).getStationLogoUrl(), item.ivCover, new ImageLoaderSpinner(item.pbSpinner));
        		} else {
        			mImageLoader.displayImage("", item.ivCover);
        			item.ivCover.setImageResource(R.drawable.ic_default_station);
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