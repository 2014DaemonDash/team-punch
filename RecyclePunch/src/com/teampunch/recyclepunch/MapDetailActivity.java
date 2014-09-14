package com.teampunch.recyclepunch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.teampunch.recyclepunch.Database.DatabaseLocation;

public class MapDetailActivity extends Activity {
	
	private int sortedUpTo;
	private boolean type;
	private double userLat, userLong;
	private ArrayList<DatabaseLocation> recycleList, refillList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_in_left, R.anim.fade_out);
		setContentView(R.layout.activity_map_detail);
		Intent i = getIntent();
		byte initState = i.getByteExtra("com.teampunch.recyclepunch.InitCategory", (byte)0);
		double[] coords = i.getDoubleArrayExtra("com.teampunch.recyclepunch.UserCoord");
		type = initState != 1;
		userLat = coords[0];
		userLong = coords[1];
		initLists();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, OptionMenuActivity.class);
        	startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void initLists(){
		int n = 5;
		sortedUpTo = 0;
		recycleList = new ArrayList<DatabaseLocation>();
		refillList = new ArrayList<DatabaseLocation>();
		for (DatabaseLocation dl : Database.loadDataFromStorage(this).getLocations()){
			
		}
		
	}
	
	private double distToUser(LatLng coords){
		double dlon, dlat, a, c, d;
		dlon = coords.longitude - userLong;
		dlat = coords.latitude - userLat;
		a = Math.pow((Math.sin(dlat/2)), 2) + Math.cos(userLat) * Math.cos(coords.latitude) * Math.pow(Math.sin(dlon/2),2);
		c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		d = 6373000 * c;
		return d;
	}

	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.push_out_right);
	}
}
