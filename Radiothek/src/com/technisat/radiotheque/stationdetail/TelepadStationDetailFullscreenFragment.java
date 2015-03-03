package com.technisat.radiotheque.stationdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;

public class TelepadStationDetailFullscreenFragment extends Fragment implements
		OnClickListener {

	private LinearLayout mInformation;

	private TextView mTvStationName;
	private TextView mTvStationGenre;

	private Station mCurrentStation;

	private OnStationDetailListener mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @return A new instance of fragment StationDetailFragment.
	 */
	public static TelepadStationDetailFullscreenFragment newInstance(
			Context ctx, Station station) {
		TelepadStationDetailFullscreenFragment fragment = new TelepadStationDetailFullscreenFragment();
		Bundle args = new Bundle();
		args.putParcelable(ctx.getString(R.string.radiothek_bundle_station),
				station);
		fragment.setArguments(args);
		return fragment;
	}

	public TelepadStationDetailFullscreenFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ImageLoader.getInstance();

		Intent intent = getActivity().getIntent();
		mCurrentStation = intent
				.getParcelableExtra(getString(R.string.radiothek_bundle_station));

	}

	public void setNewStation(Station station) {
		// TODO Update UI for both StationActivity and StationDetailFragment
		if (station != null) {
			mCurrentStation = station;
			// TODO update Genre name
			initUI();
			if (mListener != null) {
				// TODO update cover image
				mListener.onNewStation(station);
			}

		}
	}

	private void initUI() {
		if (mCurrentStation != null) {
			mTvStationGenre.setVisibility(View.VISIBLE);
			if (Misc.isValidMetaData(mCurrentStation.getMetadata())) {
				mTvStationGenre.setText(mCurrentStation.getMetadata());
			} else {
				mTvStationGenre.setText(mCurrentStation.getStationName());
			}
			mTvStationName.setText(mCurrentStation.getStationName());

		}
	}

	public void setLoading() {
	}

	public void setErrorMessage(String msg) {
		mCurrentStation = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(
				R.layout.telepad_fragment_station_detail_fullscreen, container,
				false);

		mInformation = (LinearLayout) v
				.findViewById(R.id.telepad_stationdetail_information_fullscreen_ll);

		mTvStationName = (TextView) mInformation
				.findViewById(R.id.telepad_tv_stationdetail_item_fullscreen_stationname);
		mTvStationGenre = (TextView) mInformation
				.findViewById(R.id.telepad_tv_stationdetail_item_fullscreen_stationgenre);

		mTvStationName.setTypeface(Misc.getCustomFont(getActivity(),
				Misc.FONT_BOLD));
		mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(),
				Misc.FONT_NORMAL));
		
		mInformation.setOnClickListener(this);
		mTvStationName.setOnClickListener(this);
		mTvStationGenre.setOnClickListener(this);

		initUI();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("TelepadRadiotheque",
				"TelepadStationDetailFullscreenFragment.onActivityCreated()");

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

	@Override
	public void onClick(View v) {
		Intent i = new Intent(
				TelepadStationDetailFullscreenFragment.this.getActivity(),
				TelepadStationDetailFragment.class);
		i.putExtra(this.getString(R.string.radiothek_bundle_station), mCurrentStation);
		this.getActivity().startActivity(i);

	}

}
