package com.technisat.radiotheque.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.coverservice.CoverRetrieverAsyncTask;
import com.technisat.radiotheque.coverservice.IAWSHandle;
import com.technisat.radiotheque.metadataretriver.IMetadataCallback;
import com.technisat.radiotheque.metadataretriver.MetadataRetriverAsyncTask;

public class Station implements Parcelable, IMetadataCallback, IAWSHandle {
	
	private long mId;
	
	private String mStationUrl;
	private String mStationName;
	private String mStationLogoSmall = null;
	private String mStationLogoMedium = null;
	private String mStationLogoLarge = null;
	private String mMetadata = null;
	private String mCoverUrlSmall = null;
	private String mCoverUrlMedium = null;
	private String mCoverUrlLarge = null;
	private String buyLinkAmazon;
	private String buyLinkGoogle;
	private long mLanguageId;
	
	private boolean mIsFav = false;
	private boolean mIsPlaying = false;
	
	private List<IStationMetadataUpdate> mMetaObserver = new ArrayList<IStationMetadataUpdate>();
	private List<IStationCoverUpdate> mCoverObserver = new ArrayList<IStationCoverUpdate>();
	
	private static final ScheduledExecutorService worker = 
			  Executors.newSingleThreadScheduledExecutor();
	
	public Station(long id, String url, String name) {
		setStationUrl(url);
		setStationName(name);
		mId = id;
//		updateMetaData(); // <-- holst sich sofort metadaten ab
	}
	
	public void updateMetaData() {	
//		Log.d("Nexxoo", "Updating meta data for station " + getStationName());
		MetadataRetriverAsyncTask task = new MetadataRetriverAsyncTask(mStationUrl, this);
		try {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (RejectedExecutionException e) {
			//handle this in v2
		}
		
	}
	
	public void updateMetaData(int seconds){
//		Log.d("nexxoo2", "Want updates every 30 seconds: " + getStationName());
		Runnable task = new Runnable() {
			public void run() {
	    		updateMetaData();
	    	}
		};
		try {
			worker.scheduleAtFixedRate(task, seconds, seconds, TimeUnit.SECONDS);
		} catch (RejectedExecutionException e){
			//handle in v2
		}
	}
	
	public void stopUpdateMetaData(){
//		Log.d("nexxoo2", "Dont want update anymore for " + getStationName());
		worker.shutdownNow();
	}
	
	public void updateCoverInformation(){		
		if (getMetadata() == null){
			updateMetaData();
			return;
		}
		
		try {		
			CoverRetrieverAsyncTask cr = new CoverRetrieverAsyncTask(mMetadata, Station.this);
			cr.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (RejectedExecutionException e) {
			//handle this in v2
		}
		
	}

	public void addMetadataListener(IStationMetadataUpdate iS) {
		if (mMetaObserver.indexOf(iS) == -1)
			mMetaObserver.add(iS);
	}
	
	public void addCoverListener(IStationCoverUpdate iS) {
		if (mCoverObserver.indexOf(iS) == -1)
			mCoverObserver.add(iS);
	}

	public String getStationUrl() {
		return mStationUrl;
	}

	public void setStationUrl(String url) {
		this.mStationUrl = url;
	}

	public String getStationName() {
		return mStationName;
	}

	public void setStationName(String name) {
		this.mStationName = name;
	}

	public synchronized String getMetadata() {
		return mMetadata;
	}

	public synchronized void setMetadata(String metadata) {
		mMetadata = metadata;
	}

	public String getCoverUrlSmall() {
		return mCoverUrlSmall;
	}

	public void setCoverUrlSmall(String coverUrlSmall) {
		mCoverUrlSmall = coverUrlSmall;
	}

	public String getCoverUrlMedium() {
		return mCoverUrlMedium;
	}

	public void setCoverUrlMedium(String coverUrlMedium) {
		mCoverUrlMedium = coverUrlMedium;
	}

	public String getCoverUrlLarge() {
		return mCoverUrlLarge;
	}

	public void setCoverUrlLarge(String coverUrlLarge) {
		mCoverUrlLarge = coverUrlLarge;
	}
	
	public boolean hasCoverAvailable(){
		return mCoverUrlSmall != null ||
				mCoverUrlMedium != null ||
				mCoverUrlLarge != null;
	}
	
	public boolean hasStationLogoAvailable(){
		return mStationLogoLarge != null ||
				mStationLogoMedium != null ||
				mStationLogoSmall != null;
	}
	
	public long getLanguageId(){
		return mLanguageId;
	}
	
	public void setLanguageId(long id){
		mLanguageId = id;
	}
	
	/**
	 * Returns the biggest cover we have an url to. Only should
	 * be used when wifi is available. Will return medium cover if large is no
	 * set. Will return small cover if medium is not set.
	 * @return The biggest cover url we have or null if there is no cover at all.
	 */
	public String getCoverUrl(){
		if (mCoverUrlLarge != null)
			return mCoverUrlLarge;
		if (mCoverUrlMedium != null)
			return mCoverUrlMedium;
		if (mCoverUrlSmall != null)
			return mCoverUrlSmall;
		
		return null;
	}
	
	public String getStationLogoUrl(){
		if (mStationLogoLarge != null)
			return mStationLogoLarge;
		if (mStationLogoMedium != null)
			return mStationLogoMedium;
		if (mStationLogoSmall != null)
			return mStationLogoSmall;
		
		return null;
	}

	public String getBuyLinkAmazon() {
		return buyLinkAmazon;
	}

	public void setBuyLinkAmazon(String buyLinkAmazon) {
		this.buyLinkAmazon = buyLinkAmazon;
	}

	public String getBuyLinkGoogle() {
		return buyLinkGoogle;
	}

	public void setBuyLinkGoogle(String buyLinkGoogle) {
		this.buyLinkGoogle = buyLinkGoogle;
	}
	
	public long getId(){
		return mId;
	}
	
	public void setStationLogoSmall(String url){
		mStationLogoSmall = url;
	}
	
	public String getStationLogoSmall(){
		return mStationLogoSmall;
	}
	
	public void setStationLogoMedium(String url){
		mStationLogoMedium = url;
	}
	
	public String getStationLogoMedium(){
		return mStationLogoMedium;
	}
	
	public void setStationLogoLarge(String url){
		mStationLogoLarge = url;
	}
	
	public String getStationLogoLarge(){
		return mStationLogoLarge;
	}
	
	public boolean isFav(){
		return mIsFav;
	}
	
	public void setFav(boolean isFav){
		mIsFav = isFav;
	}
	
	public boolean isPlaying(){
		return mIsPlaying;
	}
	
	public void setPlaying(boolean isPlaying){
		mIsPlaying = isPlaying;
	}

	// Parcelling part
    public Station(Parcel in){
        String[] data = new String[8];

        in.readStringArray(data);
        this.mStationUrl = data[0];
        this.mStationName = data[1];
        this.mMetadata = data[2];
        this.mCoverUrlSmall = data[3];
        this.mCoverUrlMedium = data[4];
        this.mCoverUrlLarge = data[5];
        this.buyLinkAmazon = data[6];
        this.buyLinkGoogle = data[7];
        
        long[] data2 = new long[2];
        in.readLongArray(data2);
        this.mLanguageId = data2[0];
        this.mId = data2[1];
        
        boolean[] data3 = new boolean[2];
        in.readBooleanArray(data3);
        this.mIsFav = data3[0];
        this.mIsPlaying = data3[1];
        
        mStationLogoLarge = in.readString();
        mStationLogoMedium = in.readString();
        mStationLogoSmall = in.readString();
    }
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.mStationUrl, this.mStationName, this.mMetadata, this.mCoverUrlSmall, this.mCoverUrlMedium,
        									this.mCoverUrlLarge, this.buyLinkAmazon, this.buyLinkGoogle});
        dest.writeLongArray(new long[] {this.mLanguageId, this.mId});
        dest.writeBooleanArray(new boolean[] {this.mIsFav, this.mIsPlaying});
        dest.writeString(mStationLogoLarge);
        dest.writeString(mStationLogoMedium);
        dest.writeString(mStationLogoSmall);
    }
    public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() {
        public Station createFromParcel(Parcel in) {
            return new Station(in); 
        }

        public Station[] newArray(int size) {
            return new Station[size];            
        }
    };
    
    /* Interfaces */
	@Override
	public void onFinish(String metadata, String url) {	
//		Log.d("Nexxoo", "Got meta for station " + getStationName() + ", meta: " + metadata);
		if (metadata != null){
			if (!metadata.equals(getMetadata())){
				//new\changed meta data available
				mMetadata = metadata;
				if (Misc.isValidMetaData(metadata)){
					updateCoverInformation();
				}
				for(IStationMetadataUpdate iS : mMetaObserver){
					iS.onMetadataUpdate(this);
				}
			}			
		}	
	}

	@Override
	public void onFinishLoading(CoverUrls cu) {
		if (cu != null) {
			mCoverUrlLarge = cu.getCoverLarge();
			mCoverUrlMedium = cu.getCoverMedium();
			mCoverUrlSmall = cu.getCoverSmall();
			buyLinkAmazon = cu.getBuyLink();
			
			for(IStationCoverUpdate iS : mCoverObserver){
				iS.onNewCoverReceived(this);
			}
		}
	}
}