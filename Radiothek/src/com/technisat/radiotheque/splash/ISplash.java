package com.technisat.radiotheque.splash;

import android.os.Bundle;

public interface ISplash {
	
	public void onFinishLoading(final Bundle bundle);
	
	public void onCouldNotConnectToDB();
	
	public void onStatusUpdate(final String newStatus);

}
