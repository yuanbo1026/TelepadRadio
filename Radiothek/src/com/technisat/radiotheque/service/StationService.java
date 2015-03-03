package com.technisat.radiotheque.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Country;
import com.technisat.radiotheque.entity.CountryList;
import com.technisat.radiotheque.entity.Genre;
import com.technisat.radiotheque.entity.GenreList;
import com.technisat.radiotheque.entity.ICountry;
import com.technisat.radiotheque.entity.IGenre;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.genre.GenreActivity;
import com.technisat.radiotheque.genre.TelepadGenreActivity;

public class StationService extends Service implements IGenre, ICountry {

	private final IBinder mBinder = new StationServiceBinder();

	private GenreList mGenreList;
	private CountryList mCountryList;

	private boolean mFetchingUpdates = false;
	private static final ScheduledExecutorService worker = Executors
			.newSingleThreadScheduledExecutor();

	private static final int TIMEUNTILNEXTFETCHING = 30;

	private List<IStationObserver> mObserverList = new ArrayList<IStationObserver>();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null && intent.getExtras() != null) {

			if (intent
					.getAction()
					.equals(getString(R.string.radiothek_stationservice_setstationdata))) {
				mGenreList = intent.getExtras().getParcelable(
						getString(R.string.radiothek_bundle_genreparceable));
				mCountryList = intent.getExtras().getParcelable(
						getString(R.string.radiothek_bundle_countryparceable));

				if (mGenreList != null && mGenreList.getGenreList() != null)
					for (Genre g : mGenreList.getGenreList()) {
						g.listenToAllStations();
						g.addListener(this);
						// g.updateMetaDataForAllStations();
					}

				if (mCountryList != null
						&& mCountryList.getCountryList() != null)
					for (Country c : mCountryList.getCountryList()) {
						c.listenToAllStations();
						c.addListener(this);
						// c.updateMetaDataForAllStations();
					}

			}
		}

		if (intent != null
				&& intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_stationservice_startcountryactivity))) {
			Intent i = new Intent(this, TelepadGenreActivity.class);
			i.putExtra(getString(R.string.radiothek_bundle_countryparceable),
					mCountryList);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);

			if (!mFetchingUpdates) {
				mFetchingUpdates = true;
				worker.schedule(switchFetchingOff(), TIMEUNTILNEXTFETCHING,
						TimeUnit.SECONDS);
				// maybe the reason of delaying
				if (mCountryList != null
						&& mCountryList.getCountryList() != null)
					for (Country c : mCountryList.getCountryList()) {
						c.updateMetaDataForAllStations();
					}
			}
		}

		if (intent != null
				&& intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_stationservice_startgenreactivity))) {
			Intent i = new Intent(this, TelepadGenreActivity.class);
			i.putExtra(getString(R.string.radiothek_bundle_genreparceable),
					mGenreList);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(i);

			if (!mFetchingUpdates) {
				mFetchingUpdates = true;
				worker.schedule(switchFetchingOff(), TIMEUNTILNEXTFETCHING,
						TimeUnit.SECONDS);

				if (mGenreList != null && mGenreList.getGenreList() != null)
					for (Genre g : mGenreList.getGenreList()) {
						g.updateMetaDataForAllStations();
					}
			}
		}

		if (intent != null
				&& intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_stationservice_startedstation))) {
			Station started = intent
					.getParcelableExtra(getString(R.string.radiothek_bundle_station));
			if (started != null) {
				// find the station in our genre list
				if (mGenreList != null && mGenreList.getGenreList() != null)
					for (Genre g : mGenreList.getGenreList()) {
						List<Station> sList = g.getsList();
						if (sList != null) {
							for (Station s : sList) {
								if (s.getId() == started.getId()) {
									s.setPlaying(true);
									s.updateMetaData(30);
								}
							}
						}
					}
				// find it in country list
				if (mCountryList != null
						&& mCountryList.getCountryList() != null)
					for (Country c : mCountryList.getCountryList()) {
						List<Station> sList = c.getsList();
						if (sList != null) {
							for (Station s : sList) {
								if (s.getId() == started.getId()) {
									s.setPlaying(true);
									s.updateMetaData(30);
								}
							}
						}
					}
			}
		}

		if (intent != null
				&& intent.getAction() != null
				&& intent
						.getAction()
						.equals(getString(R.string.radiothek_stationservice_stoppedstation))) {
			Station started = intent
					.getParcelableExtra(getString(R.string.radiothek_bundle_station));
			if (started != null) {
				// find the station in our genre list
				if (mGenreList != null && mGenreList.getGenreList() != null)
					for (Genre g : mGenreList.getGenreList()) {
						List<Station> sList = g.getsList();
						if (sList != null) {
							for (Station s : sList) {
								if (s.getId() == started.getId()) {
									s.setPlaying(false);
									s.stopUpdateMetaData();
								}
							}
						}
					}
				// find it in country list
				if (mCountryList != null
						&& mCountryList.getCountryList() != null)
					for (Country c : mCountryList.getCountryList()) {
						List<Station> sList = c.getsList();
						if (sList != null) {
							for (Station s : sList) {
								if (s.getId() == started.getId()) {
									s.setPlaying(false);
									s.stopUpdateMetaData();
								}
							}
						}
					}
			}
		}

		return START_STICKY;
	}

	public void addListener(IStationObserver listener) {
		if (mObserverList.indexOf(listener) == -1)
			mObserverList.add(listener);
	}

	public void updateStations() {
		for (Genre g : mGenreList.getGenreList()) {
			g.updateMetaDataForAllStations();
		}

		for (Country c : mCountryList.getCountryList()) {
			c.updateMetaDataForAllStations();
		}
	}

	private Runnable switchFetchingOff() {
		return new Runnable() {

			@Override
			public void run() {
				mFetchingUpdates = false;
			}
		};

	}

	/**
	 * Class used for the client Binder. Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class StationServiceBinder extends Binder {
		public StationService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return StationService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void sortStation(Genre g, Station s) {
		for (Station station : g.getsList()) {
			if (station.getId() == s.getId()) {
				station = s;
				if (station.hasCoverAvailable()) {
					g.getsList().set(0, station);
				} else if (station.hasStationLogoAvailable()) {
					if (!g.getsList().get(0).hasCoverAvailable())
						g.getsList().set(0, station);
				} else if (Misc.isValidMetaData(station.getMetadata())) {
					if (!g.getsList().get(0).hasStationLogoAvailable())
						g.getsList().set(0, station);
				}
				break;
			}
		}
	}

	private void sortStation(Country c, Station s) {
		for (Station station : c.getsList()) {
			if (station.getId() == s.getId()) {
				station = s;
				if (station.hasCoverAvailable()) {
					c.getsList().set(0, station);
				} else if (station.hasStationLogoAvailable()) {
					if (!c.getsList().get(0).hasCoverAvailable())
						c.getsList().set(0, station);
				} else if (Misc.isValidMetaData(station.getMetadata())) {
					if (!c.getsList().get(0).hasStationLogoAvailable())
						c.getsList().set(0, station);
				}
				break;
			}
		}
	}

	@Override
	public void onMetadataUpdate(Genre g, Station s) {
		sortStation(g, s);
		for (IStationObserver o : mObserverList) {
			o.onUpdateStation(g, s);
		}
	}

	@Override
	public void onMetadataUpdate(Country c, Station s) {
		sortStation(c, s);
		for (IStationObserver o : mObserverList) {
			o.onUpdateStation(c, s);
		}
	}
}