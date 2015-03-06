package com.technisat.radiotheque.stationdetail;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.service.MediaPlayerService;


public class StationDetailFragment extends Fragment {
	
	private RelativeLayout mPlayer;
	private RelativeLayout mStation;
	private RelativeLayout mMore;
	
	private ImageView mIvPlayButton;
	private ImageView mIvBuyIcon;
	
	private ImageView mIvStationLogo;
	private ImageView mIvFavIcon;
	
	private TextView mTvMeta;
	
	private TextView mTvStationName;
	private TextView mTvStationGenre;
	
	private LinearLayout mLlMoreStations;
	
	private Station mCurrentStation;

	private OnStationDetailListener mListener;
	private ImageLoader mImageLoader;
	private ProgressBar mSpinner;
	private TextView mTvMoreStations;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment StationDetailFragment.
	 */	
	public static StationDetailFragment newInstance(Context ctx, Station station) {
		StationDetailFragment fragment = new StationDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable(ctx.getString(R.string.radiothek_bundle_station), station);
		fragment.setArguments(args);
		return fragment;
	}

	public StationDetailFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mImageLoader = ImageLoader.getInstance();
		
		Intent intent = getActivity().getIntent();
		mCurrentStation = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));

	}
	
	public void setNewStation(Station station){
		if (station != null){
			mCurrentStation = station;
			initUI();
			
			if (mListener != null) {
				mListener.onNewStation(station);			
			}
			
		}
	}
	
	private void initUI(){
		if (mCurrentStation != null){
			mIvPlayButton.setClickable(true);
			mIvPlayButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onPlayButtonPressed(mCurrentStation);
				}
			});
			
			setPlayButton(mCurrentStation.isPlaying());
		
			mTvStationGenre.setVisibility(View.VISIBLE);
			if (Misc.isValidMetaData(mCurrentStation.getMetadata())){
				mTvMeta.setText(mCurrentStation.getMetadata());
				//mTvStationGenre.setText(mCurrentStation.getMetadata());
				mTvStationGenre.setVisibility(View.GONE);
			} else {
				mTvMeta.setText(mCurrentStation.getStationName());
				mTvStationGenre.setVisibility(View.GONE);
				mTvStationGenre.setText("");
			}
			
			if (mCurrentStation.getBuyLinkAmazon() == null){
				mIvBuyIcon.setOnClickListener(null);
				mIvBuyIcon.setImageResource(R.drawable.ic_player_basket);
			} else {
				mIvBuyIcon.setImageResource(R.drawable.ic_player_basket_h);
				mIvBuyIcon.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (mCurrentStation != null && mCurrentStation.getBuyLinkAmazon() != null){
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(mCurrentStation.getBuyLinkAmazon()));
							startActivity(i);
						}
					}
				});
			}

			mIvFavIcon.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					mCurrentStation.setFav(!mCurrentStation.isFav());
					DatabaseHandler dbhandler = new DatabaseHandler(getActivity());
					dbhandler.insertStation(mCurrentStation);
					setFavIcon(mCurrentStation.isFav());
				}
			});
			setFavIcon(mCurrentStation.isFav());
			
			if (mCurrentStation.hasStationLogoAvailable()){
				mImageLoader.displayImage(mCurrentStation.getStationLogoUrl(), mIvStationLogo);
			} else {
				mIvStationLogo.setImageResource(R.drawable.ic_default_station);
			}
			
			RequestMoreStationsAsyncTask task = new RequestMoreStationsAsyncTask(getActivity(), mCurrentStation, 
					new IRequestMoreStations() {
				
				@Override
				public void onGotMoreStations(List<Station> stationList) {
					if (stationList != null){
						mLlMoreStations.removeAllViews();
						for (Station s : stationList){
							View v = insertStation(s);
							if (v != null)
								mLlMoreStations.addView(v);							
				        }
					}
				}
			});
			try {
				task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} catch (RejectedExecutionException e) {
				//handle this in v2
			}
			
			mTvStationName.setText(mCurrentStation.getStationName());

		}
	}
	
	private View insertStation(final Station station){
	     if (getActivity() != null){
		     LinearLayout layout = new LinearLayout(getActivity());
		     layout.setLayoutParams(new LayoutParams(100, 100));
		     layout.setGravity(Gravity.CENTER);
		     
		     ImageView imageView = new ImageView(getActivity());
		     LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(100, 100);
		     para.setMargins(8, 0, 0, 0);	     
		     
		     imageView.setLayoutParams(para);
		     imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		     if (station.hasStationLogoAvailable())
		    	 mImageLoader.displayImage(station.getStationLogoUrl(), imageView);
		     else
		    	 imageView.setImageResource(R.drawable.ic_default_station);
		     imageView.setClickable(true);
		     imageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent i = new Intent(getActivity(), MediaPlayerService.class);
					i.setAction(getString(R.string.radiothek_mediaplayerservice_playstream));
					i.putExtra(getString(R.string.radiothek_bundle_station), station);
					getActivity().startService(i);
				}
			});
		     
		    layout.addView(imageView);
		    return layout;
	     }
	     
	     return null;
	}

	public void setLoading(){
		mSpinner.setVisibility(View.VISIBLE);
		mTvMeta.setText(getString(R.string.playerfragment_text_loading));
	}
	
	public void setErrorMessage(String msg){
		mTvMeta.setText(msg);
		mCurrentStation = null;
		mSpinner.setVisibility(View.GONE);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_station_detail, container,
				false);
		
		mPlayer = (RelativeLayout) v.findViewById(R.id.rl_stationdetail_playbox);
		mStation = (RelativeLayout) v.findViewById(R.id.rl_stationdetail_stationbox);
		mMore = (RelativeLayout) v.findViewById(R.id.rl_stationdetail_morestations);
		
		mIvPlayButton = (ImageView) mPlayer.findViewById(R.id.iv_stationdetail_fragment_playbutton);
		mIvBuyIcon = (ImageView) mPlayer.findViewById(R.id.iv_stationdetail_fragment_buy);
		
		mIvStationLogo = (ImageView) mStation.findViewById(R.id.iv_stationdetail_fragment_stationlogo);
		mIvFavIcon = (ImageView) mStation.findViewById(R.id.iv_stationdetail_fragment_fav);
		
		mTvMeta = (TextView) mPlayer.findViewById(R.id.tv_stationdetail_fragment_meta);
		
		mTvStationName = (TextView) mStation.findViewById(R.id.tv_stationdetail_item_stationname);
		mTvStationGenre = (TextView) mStation.findViewById(R.id.tv_stationdetail_item_stationgenre);
		
		mTvMoreStations = (TextView) mMore.findViewById(R.id.tv_stationdetail_morestations);
		
		mLlMoreStations = (LinearLayout) mMore.findViewById(R.id.ll_stationdetail_morestations);
		
		mSpinner = (ProgressBar) mPlayer.findViewById(R.id.pb_stationdetail_fragment_spinner);
		
		mTvMeta.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_BOLD));
		mTvStationName.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_BOLD));
		mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		mTvMoreStations.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_NORMAL));
		
		initUI();
		
		return v;
	}

	public void onPlayButtonPressed(Station station) {
		if (mListener != null) {
			mListener.onTogglePlay(station);			
		}
	}
	
	public void setPlayButton(boolean isPlaying){
		mSpinner.setVisibility(View.GONE);
		if (isPlaying)
			mIvPlayButton.setImageResource(R.drawable.ic_player_pause);
		else
			mIvPlayButton.setImageResource(R.drawable.ic_player_play);
	}
	
	public void setFavIcon(boolean isFav){
		if (isFav){
			mIvFavIcon.setImageResource(R.drawable.ic_player_fav);
		} else {
			mIvFavIcon.setImageResource(R.drawable.ic_player_fav_h);				
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnStationDetailListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnStationDetailListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	public interface OnStationDetailListener {
		public void onTogglePlay(Station station);
		public void onNewStation(Station station);
	}

}
