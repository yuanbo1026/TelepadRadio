package com.technisat.radiotheque.stationdetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.DatabaseHandler;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;

public class TelepadStationDetailFragment extends Fragment {

	private RelativeLayout mPlayer;

	private LinearLayout mInformation;
	private LinearLayout mFunction;

	private ImageView mIvPlayButton;
	private ImageView mIvBuyIcon;

	private ImageView mIvFavIcon;

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
	public static TelepadStationDetailFragment newInstance(Context ctx,
			Station station) {
		TelepadStationDetailFragment fragment = new TelepadStationDetailFragment();
		Bundle args = new Bundle();
		args.putParcelable(ctx.getString(R.string.radiothek_bundle_station),
				station);
		fragment.setArguments(args);
		return fragment;
	}

	public TelepadStationDetailFragment() {
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
			mIvPlayButton.setClickable(true);
			mIvPlayButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onPlayButtonPressed(mCurrentStation);
				}
			});

			setPlayButton(mCurrentStation.isPlaying());

			mTvStationGenre.setVisibility(View.VISIBLE);
			if (Misc.isValidMetaData(mCurrentStation.getMetadata())) {
				mTvStationGenre.setText(mCurrentStation.getMetadata());
			} else {
				mTvStationGenre.setText(mCurrentStation.getStationName());
			}

			if (mCurrentStation.getBuyLinkAmazon() == null) {
				mIvBuyIcon.setOnClickListener(null);
				mIvBuyIcon.setImageResource(R.drawable.ic_player_basket);
			} else {
				mIvBuyIcon.setImageResource(R.drawable.ic_player_basket_h);
				mIvBuyIcon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mCurrentStation != null
								&& mCurrentStation.getBuyLinkAmazon() != null) {
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(mCurrentStation
									.getBuyLinkAmazon()));
							startActivity(i);
						}
					}
				});
			}

			mIvFavIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCurrentStation.setFav(!mCurrentStation.isFav());
					DatabaseHandler dbhandler = new DatabaseHandler(
							getActivity());
					dbhandler.insertStation(mCurrentStation);
					setFavIcon(mCurrentStation.isFav());
				}
			});
			setFavIcon(mCurrentStation.isFav());

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
		View v = inflater.inflate(R.layout.telepad_fragment_station_detail,
				container, false);

		mPlayer = (RelativeLayout) v
				.findViewById(R.id.telepad_stationdetail_player_rl);
		mInformation = (LinearLayout) v
				.findViewById(R.id.telepad_stationdetail_information_ll);
		mFunction = (LinearLayout) v
				.findViewById(R.id.telepad_stationdetail_function_ll);

		mIvPlayButton = (ImageView) mPlayer
				.findViewById(R.id.telepad_iv_stationdetail_player_play);
		mIvBuyIcon = (ImageView) mFunction
				.findViewById(R.id.telepad_iv_stationdetail_function_buy);

		mIvFavIcon = (ImageView) mFunction
				.findViewById(R.id.telepad_iv_stationdetail_function_fav);

		mTvStationName = (TextView) mInformation
				.findViewById(R.id.telepad_tv_stationdetail_item_stationname);
		mTvStationGenre = (TextView) mInformation
				.findViewById(R.id.telepad_tv_stationdetail_item_stationgenre);

//		mSpinner = (ProgressBar) mPlayer
//				.findViewById(R.id.telepad_pb_station_spinner);

		mTvStationName.setTypeface(Misc.getCustomFont(getActivity(),
				Misc.FONT_BOLD));
		mTvStationGenre.setTypeface(Misc.getCustomFont(getActivity(),
				Misc.FONT_NORMAL));

		initUI();

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("TelepadRadiotheque", "TelepadStationDetailFragment.onActivityCreated()");
		
	}

	public void onPlayButtonPressed(Station station) {
		if (mListener != null) {
			mListener.onTogglePlay(station);
		}
	}

	public void setPlayButton(boolean isPlaying) {
//		mSpinner.setVisibility(View.GONE);
		if (isPlaying)
			mIvPlayButton.setImageResource(R.drawable.ic_player_pause);
		else
			mIvPlayButton.setImageResource(R.drawable.ic_player_play);
	}

	public void setFavIcon(boolean isFav) {
		if (isFav) {
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
