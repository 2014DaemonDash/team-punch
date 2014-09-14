package com.teampunch.recyclepunch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.teampunch.recyclepunch.Database.DatabaseLocation;

public class MapDetailActivity extends Activity {
	
	private Database database;
	private boolean type;
	private ArrayList<DatabaseLocation> recycleList, refillList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_in_left, R.anim.fade_out);
		setContentView(R.layout.activity_map_detail);
		byte initState = getIntent().getByteExtra("com.teampunch.recyclepunch.InitCategory", (byte)0);
		type = initState != 1;
		database = Database.loadDataFromStorage(this);
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
}

	public void finish(){
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.push_out_right);
	}
}
