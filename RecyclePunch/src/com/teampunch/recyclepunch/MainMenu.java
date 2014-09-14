package com.teampunch.recyclepunch;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainMenu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
    }

    public void findClicked(View view)
    {
    	Intent intent = new Intent(this, FindMapActivity.class);
    	startActivity(intent);
    }
    
    public void learnClicked(View view)
    {
    	Intent intent = new Intent(this, LearnSlidesActivity.class);
    	startActivity(intent);
    }
    
    public void contribClicked(View view)
    {
    	Intent intent = new Intent(this, ContribMapActivity.class);
    	startActivity(intent);
    }
    
    public void extraClicked(View view)
    {
    	Intent intent = new Intent(this, ExtraMenuActivity.class);
    	startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
