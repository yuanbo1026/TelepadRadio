package com.technisat.radiotheque.melden;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.entity.Station;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;
import com.technisat.radiotheque.service.MediaPlayerService;

/**
 * Activity that can send an Email to report a problem with the App,
 * containing the currently playing station (that got most likely something to
 * do with the occurring problem and a text typed by the user.
 *
 */
public class MeldenActivity extends Activity {
	
	EditText mEdit;
	Station station;
	private TextView mTvSubCaption;
	private TextView mTvCaption;
	private Button mAbortButton;
	private Button mSendButton;
	
	/**  handles the Intent and de-parcels the given Station (if there is one) */
	private BroadcastReceiver bcr = new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent != null && intent.getAction() != null){
				if(intent.getAction().equals(getString(R.string.radiothek_mediaplayerservice_broadcastmetadata))){
					Station s = intent.getParcelableExtra(getString(R.string.radiothek_bundle_station));
					if(s != null){
						station = s;
						mTvSubCaption.setText(getString(R.string.meldenactivity_text_withstation) + " "+ s.getStationName());
					}
				}
			}
		}
	};
	
	
	@Override
	protected void onPause() {
		unregisterReceiver(bcr);
		super.onPause();
	}

	@Override
	protected void onResume() {
		IntentFilter i = new IntentFilter();
		i.addAction(getString(R.string.radiothek_mediaplayerservice_broadcastmetadata));
		registerReceiver(bcr, i);
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_melden);
		
		Misc.initUIL(getApplicationContext());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mEdit   = (EditText)findViewById(R.id.et_activity_melden_textfield);
		mTvSubCaption = (TextView) findViewById(R.id.tv_activity_melden_subcaption);
		mTvCaption = (TextView) findViewById(R.id.tv_activity_melden_caption);
		mAbortButton = (Button) findViewById(R.id.btn_activity_melden_abort);
		mSendButton = (Button) findViewById(R.id.btn_activity_melden_send);
		
		mEdit.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));
		mTvSubCaption.setTypeface(Misc.getCustomFont(this, Misc.FONT_NORMAL));
		mTvCaption.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		mAbortButton.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		mSendButton.setTypeface(Misc.getCustomFont(this, Misc.FONT_BOLD));
		
		Intent intent = new Intent(this, MediaPlayerService.class);
		intent.setAction(getString(R.string.radiothek_mediaplayerservice_requestcurrentstation));
		startService(intent);
	}
	
	/**
	 * Method that actually sends the Email on button click
	 * @param view
	 */
	public void senden(View view){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.report_problem_mailto)});
		i.putExtra(Intent.EXTRA_SUBJECT, "Radiotheque melden");
		if (station != null)
			i.putExtra(Intent.EXTRA_TEXT   , mEdit.getText().toString() + "\n\n" + " StationName: "+
				station.getStationName()+", StationURL: "+
				station.getStationUrl()+", StationId: "+
				station.getId()+", StationMetadata: "+
				station.getMetadata());
		try {
		    startActivity(Intent.createChooser(i, getString(R.string.meldenactivity_text_mailwith)));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(this, getString(R.string.meldenactivity_text_nomailclients), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void abbrechen(View view){
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
	}
}