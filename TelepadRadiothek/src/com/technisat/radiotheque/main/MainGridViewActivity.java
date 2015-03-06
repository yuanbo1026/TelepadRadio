//package com.technisat.radiotheque.main;
//
//import android.app.Activity;
//import android.app.SearchManager;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
//import android.os.Bundle;
//import android.support.v4.widget.DrawerLayout;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.SearchView;
//import android.widget.TextView;
//
//import com.technisat.radiothek.R;
//import com.technisat.radiotheque.Drawer.MyDrawer;
//import com.technisat.radiotheque.menu.ActionBarMenuHandler;
///**
// * not used
// * @author b.yuan
// *
// */
//public class MainGridViewActivity extends Activity implements OnItemClickListener ,android.view.View.OnClickListener{
//	private MyDrawer mMyDrawer;
//	private DrawerLayout mDrawerLayout;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.drawer_layout_container);
//
//		mMyDrawer = new MyDrawer(this);
//		mDrawerLayout = mMyDrawer.getDrawerLayout();
//
//		LayoutInflater inflater = getLayoutInflater();
//		RelativeLayout container = (RelativeLayout) findViewById(R.id.frame_container);
//		inflater.inflate(R.layout.activity_main_grid, container);
//		
//		RelativeLayout rl = (RelativeLayout) findViewById(R.id.grid_item_rl_1);
//		ImageView bg = (ImageView) findViewById(R.id.grid_item_bg_1);
//		LinearLayout ll = (LinearLayout) findViewById(R.id.grid_item_ll_1);
//		ImageView lv = (ImageView) findViewById(R.id.grid_item_iv_1);
//		TextView tv = (TextView) findViewById(R.id.grid_item_tv_1);
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar actions click
//        switch (item.getItemId()) {
//        case R.id.action_drawer:
//        	if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//        		mDrawerLayout.openDrawer(Gravity.RIGHT);
//        		
//        	}else{
//        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
//        	}
//            return true;
//        default:
//        	if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//        		mDrawerLayout.closeDrawer(Gravity.RIGHT);
//        	}
//        	return ActionBarMenuHandler.handleActionBarItemClick(this, item);
//        }
//	}
//	
//	@Override
//	protected void onDestroy() {
//		if (mMyDrawer != null)
//			mMyDrawer.finalize();
//		super.onDestroy();
//	}
//	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.menu_new, menu);
//		
//	    SearchManager searchManager =
//	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//	    SearchView searchView =
//	            (SearchView) menu.findItem(R.id.search).getActionView();
//	    searchView.setSearchableInfo(
//	            searchManager.getSearchableInfo(getComponentName()));
//		return true;
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		
//		
//	}
//
//	@Override
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}
