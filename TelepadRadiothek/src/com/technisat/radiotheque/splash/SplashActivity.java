package com.technisat.radiotheque.splash;

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewConfiguration;

import com.technisat.telepadradiothek.R;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.main.TelepadMainActivity;
import com.technisat.radiotheque.service.StationService;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equals(getString(R.string.splash_closeapp))){
//			killApp();
		} else {			

			if (Misc.isOnline(this)){
				
				Misc.initUIL(getApplicationContext());
			
				try {
			        ViewConfiguration config = ViewConfiguration.get(this);
			        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			        if(menuKeyField != null) {
			            menuKeyField.setAccessible(true);
			            menuKeyField.setBoolean(config, false);
			        }
			    } catch (Exception ex) {
			        // Ignore
			    }
						
				new StartUpAsync(this, new ISplash() {
					
					@Override
					public void onStatusUpdate(String newStatus) {
						//if current status is displayed, update it here
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// update gui here
								
							}
						});
					}
					
					@Override
					public void onFinishLoading(final Bundle bundle) {
						//start service with data we got from webservice
						Intent i = new Intent(SplashActivity.this, StationService.class);
						i.setAction( getString(R.string.radiothek_stationservice_setstationdata) );
						i.putExtras(bundle);
						startService(i);
						
						//call main activity here				
						i = new Intent(SplashActivity.this, TelepadMainActivity.class);			
						//i.putExtras(bundle);
						startActivity(i);
						finish();
					}

					@Override
					public void onCouldNotConnectToDB() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								buildNoDBDialog().show();
							}
						});
											
					}
				}).execute();
			} else {
				//no internet
				buildAlertDialog().show();
			}
		}
	}
	
	private AlertDialog buildAlertDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

		alertDialogBuilder.setTitle(getString(R.string.splashactivity_caption_internet));
		alertDialogBuilder
			.setMessage(getString(R.string.splashactivity_text_internet))
			.setCancelable(false)
			 .setPositiveButton(getString(R.string.splashactivity_button_gotosetting), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
					startActivity(intent);
					SplashActivity.this.finish();
							
				}
			})
			.setNegativeButton(getString(R.string.splashactivity_button_abort), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						SplashActivity.this.finish();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
 
				return alertDialog;
	}
	
	private AlertDialog buildNoDBDialog(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);

		alertDialogBuilder.setTitle(getString(R.string.splashactivity_caption_internet));
		alertDialogBuilder
			.setMessage(getString(R.string.splashactivity_text_nodbconnection))
			.setCancelable(false)
			 .setPositiveButton(getString(R.string.splashactivity_button_continue), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//call main activity here				
					Intent i = new Intent(SplashActivity.this, TelepadMainActivity.class);			
					startActivity(i);
					SplashActivity.this.finish();
				}
			})
			.setNegativeButton(getString(R.string.splashactivity_button_abort), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						SplashActivity.this.finish();
					}
				});

				AlertDialog alertDialog = alertDialogBuilder.create();
				
				return alertDialog;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
}