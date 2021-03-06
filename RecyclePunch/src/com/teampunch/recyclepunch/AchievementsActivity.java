package com.teampunch.recyclepunch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class AchievementsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_in_left, R.anim.fade_out);
		setContentView(R.layout.activity_achievements);
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.achievements, menu);
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
	
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.push_out_right);
	}
}
