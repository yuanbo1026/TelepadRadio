package com.technisat.radiotheque.player;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.station.StationActivity;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link PlayerFragement.OnStationDetailListener} interface to handle
 * interaction events. Use the {@link PlayerFragement#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class PlayerFragement extends Fragment {

	private static final String ARG_STATION = "radiothek.station";

	private Station mCurrentStation;
	private ImageView mIvPlayButton;
	private ImageView mIvStationLogo;
	private TextView mTvMeta;
	private ProgressBar mSpinner;

	private IPlayerFragment mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * @return A new instance of fragment PlayerFragement.
	 */
	public static PlayerFragement newInstance(Station station) {
		PlayerFragement fragment = new PlayerFragement();
		Bundle args = new Bundle();
		args.putParcelable(ARG_STATION, station);
		fragment.setArguments(args);
		return fragment;
	}

	public PlayerFragement() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mCurrentStation = getArguments().getParcelable(getString(R.string.radiothek_bundle_station));			
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_player_fragement, container,
				false);
		mIvPlayButton = (ImageView) v.findViewById( R.id.iv_player_fragment_playbutton );
		mIvStationLogo = (ImageView) v.findViewById( R.id.iv_player_fragment_stationlogo );
		mTvMeta = (TextView) v.findViewById( R.id.tv_player_fragment_meta );
		mSpinner = (ProgressBar) v.findViewById(R.id.pb_player_fragment_spinner);
		
		mTvMeta.setTypeface(Misc.getCustomFont(getActivity(), Misc.FONT_BOLD));
		
		mIvPlayButton.setClickable(true);
		mIvPlayButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onPlayButtonPressed(mCurrentStation);
			}
		});
		
		mIvStationLogo.setClickable(true);
		mIvStationLogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), StationActivity.class);
				i.putExtra(getString(R.string.radiothek_bundle_station), mCurrentStation);
				startActivity(i);
			}
		});
		
		return v;
	}

	public void onPlayButtonPressed(Station station) {
		if (mListener != null) {
			mListener.onTogglePlay(station);			
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (IPlayerFragment) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IPlayerFragment");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	public void updateUI(boolean isPlaying){
		if (mCurrentStation != null){
			mSpinner.setVisibility(View.GONE);
//			if (isPlaying)
//				mIvPlayButton.setImageResource(R.drawable.ic_pause);
//			else
//				mIvPlayButton.setImageResource(R.drawable.ic_play);
			
			if (mCurrentStation != null){
				if (Misc.isValidMetaData(mCurrentStation.getMetadata())){
					mTvMeta.setText(mCurrentStation.getMetadata());
				} else {
					mTvMeta.setText(mCurrentStation.getStationName());
				}
				
				ImageLoader imageLoader = ImageLoader.getInstance();
//				if (mCurrentStation.hasCoverAvailable()){					
//					imageLoader.displayImage(mCurrentStation.getCoverUrl(), mIvStationLogo);
//				} else 
				if (mCurrentStation.hasStationLogoAvailable()){
					imageLoader.displayImage(mCurrentStation.getStationLogoUrl(), mIvStationLogo);
				} else {
					mIvStationLogo.setImageResource(R.drawable.ic_default_station);
				}
					
			}
		} else {
			mIvPlayButton.setImageResource(R.drawable.ic_play_deactivated);
			mTvMeta.setText("");
		}
	}
	
	public void setStation(Station station){
		mCurrentStation = station;		
	}
	
	public void setLoading(){
		mTvMeta.setText(getString(R.string.playerfragment_text_loading));
		mSpinner.setVisibility(View.VISIBLE);
	}
	
	public void setErrorMessage(String msg){
		mTvMeta.setText(msg);
		setStation(null);
		mSpinner.setVisibility(View.GONE);
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface IPlayerFragment {
		public void onTogglePlay(Station station);
	}

}
