package com.technisat.radiotheque.service.error;

import com.technisat.radiothek.R;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerErrors {
	
	private static String DEFAULTERROR = "There was a problem opening the stream. Please try again later or contact the support.";
	
	public static String getErrorMessageByErrorCode(Context context, int errorCode){
		if (context == null)
			return DEFAULTERROR;
		
		return getErrorMsg(context, errorCode);
	}
	
	private static String getErrorMsg(Context context, int code){
		switch (code) {
		case MediaPlayer.MEDIA_ERROR_IO:
			return context.getString( R.string.radiothek_mediaplayerservice_error_io );
		case MediaPlayer.MEDIA_ERROR_MALFORMED:
			return context.getString( R.string.radiothek_mediaplayerservice_error_malformed );
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			return context.getString( R.string.radiothek_mediaplayerservice_error_server_died );
		case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
			return context.getString( R.string.radiothek_mediaplayerservice_error_timed_out );
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			return context.getString( R.string.radiothek_mediaplayerservice_error_unknown );
		case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
			return context.getString( R.string.radiothek_mediaplayerservice_error_unsupported );
		default:
			return context.getString( R.string.radiothek_mediaplayerservice_error_unknown );
		}
	}

}
