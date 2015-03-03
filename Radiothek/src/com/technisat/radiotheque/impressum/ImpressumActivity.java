package com.technisat.radiotheque.impressum;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.technisat.radiothek.R;
import com.technisat.radiotheque.Drawer.MyDrawer;
import com.technisat.radiotheque.android.Misc;
import com.technisat.radiotheque.menu.ActionBarMenuHandler;

/**
 * Displays the Impressum Screen
 *
 */
public class ImpressumActivity extends Activity {
	
	private MyDrawer mMyDrawer;
	private DrawerLayout mDrawerLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout_container);
		
		Misc.initUIL(getApplicationContext());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mMyDrawer = new MyDrawer(this);
        mDrawerLayout = mMyDrawer.getDrawerLayout();
        
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
    	inflater.inflate(R.layout.activity_impressum, container);
	}
	
	@Override
	protected void onDestroy() {
		if (mMyDrawer != null)
			mMyDrawer.finalize();
		super.onDestroy();
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_new, menu);
	    
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	            (SearchView) menu.findItem(R.id.search).getActionView();
	    searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
        switch (item.getItemId()) {
        case R.id.action_drawer:
        	if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
        		mDrawerLayout.openDrawer(Gravity.RIGHT);
        		
        	}else{
        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
        	}
            return true;
        default:
        	if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
        	}
        	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
        }
	}
}