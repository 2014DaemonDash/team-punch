package com.teampunch.recyclepunch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.teampunch.recyclepunch.Database.DatabaseLocation;

public class MapDetailActivity extends Activity {
	
	private int sortedUpTo;
	private boolean type;
	private double userLat, userLong;
	private ArrayList<DatabaseLocation> recycleList, refillList;
	
	private Database db;
	
	private DetailAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_in_left, R.anim.fade_out);
		setContentView(R.layout.activity_map_detail);
		Intent i = getIntent();
		byte initState = i.getByteExtra("com.teampunch.recyclepunch.InitCategory", (byte)0);
		double[] coords = i.getDoubleArrayExtra("com.teampunch.recyclepunch.UserCoord");
		type = initState == 1;
		ImageButton tmp1 = (ImageButton)findViewById(R.id.imageButton2);
		ImageButton tmp2 = (ImageButton)findViewById(R.id.imageButton3);
		tmp1.setImageResource(type?R.drawable.off_recycle_toggle : R.drawable.recycle_toggle);
		tmp2.setImageResource(type? R.drawable.refill_toggle : R.drawable.off_refill_toggle);
		userLat = coords[0];
		userLong = coords[1];
		initLists();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
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
	
	private void initLists()
	{
		sortedUpTo = 0;
		recycleList = new ArrayList<DatabaseLocation>();
		refillList = new ArrayList<DatabaseLocation>();
		db = Database.loadDataFromStorage(this);
		
		Collections.sort(db.getLocations(), new DatabaseLocationComparator());
		
		adapter = new DetailAdapter();
		if (type)
			adapter.setType((byte)1);
		else
			adapter.setType((byte)0);
		
		
		for (DatabaseLocation loc : db.getLocations())
		{
			createRow(adapter, loc, distToUser(new LatLng(loc.getX(), loc.getY())));
		}
		((ListView) findViewById(R.id.listView)).setAdapter(adapter);
	}
	
	public double distToUser(LatLng coords)
	{
		double dlon, dlat, a, c, d;
		dlon = coords.longitude - userLong;
		dlat = coords.latitude - userLat;
		if (dlon > 180) dlon -= 360;
		if (dlon < -180) dlon += 360;
		dlon *= Math.cos(userLat*Math.PI/180);
		d = Math.sqrt(dlat*dlat + dlon*dlon);
		
		
		d *= 362776;
//		a = Math.pow((Math.sin(dlat/2)), 2) + Math.cos(userLat) * Math.cos(coords.latitude) * Math.pow(Math.sin(dlon/2),2);
//		c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//		d = 6373000 * c;
		return d;
	}
	
	public class DatabaseLocationComparator implements Comparator<DatabaseLocation>
	{		
		public int compare(DatabaseLocation lhs, DatabaseLocation rhs)
		{
			double ldist = distToUser(new LatLng(lhs.getX(), lhs.getY()));
			double rdist = distToUser(new LatLng(rhs.getX(), rhs.getY()));
			
			if (ldist < rdist)
				return -1;
			else if (ldist > rdist)
				return 1;
			return 0;
		}
	}
	
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.push_out_right);
	}
	
	public void createRow(DetailAdapter adapter, DatabaseLocation loc, double distance)
	{
		LinearLayout row = new LinearLayout(this);
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		row.setLayoutParams(layoutParams);
		row.setPadding(8, 8, 8, 8);
		
		TextView textView = new TextView(this);
		LayoutParams layoutParams2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
		layoutParams2.weight = 1;
		layoutParams2.gravity = Gravity.CENTER_VERTICAL;
		textView.setLayoutParams(layoutParams2);
		textView.setText(getInitialString(loc) + Integer.toString((int)distance) + " ft.  " + getDirection(loc));
		textView.setTextSize(24);
		row.addView(textView);
		
		adapter.addView(row, loc.getType());
	}
	
	private String getInitialString(DatabaseLocation loc)
	{
		if (loc.getType() == 0)
			return "Recycle bin: ";
		else
			return "Refill station: ";
	}
	
	private String getDirection(DatabaseLocation loc)
	{
		double dlon = loc.getY() - userLong;
		double dlat = loc.getX() - userLat;
		if (dlon > 180) dlon -= 360;
		if (dlon < -180) dlon += 360;
		dlon *= Math.cos(userLat*Math.PI/180);
		
		double angle = Math.atan2(dlat, dlon)*180/Math.PI;
		angle += 22.5;
		if (angle < 0) angle += 360;
		
		if (angle < 45) return "E";
		else if (angle < 90) return "NE";
		else if (angle < 135) return "N";
		else if (angle < 180) return "NW";
		else if (angle < 225) return "W";
		else if (angle < 270) return "SW";
		else if (angle < 315) return "S";
		else return "SE";
	}
	
	private static class DetailAdapter extends BaseAdapter
	{
		private byte type;
		private ArrayList<View> rowsRecycle;
		private ArrayList<View> rowsWater;
		
		public DetailAdapter()
		{
			rowsRecycle = new ArrayList<View>();
			rowsWater = new ArrayList<View>();
			type = 0;
		}
		
		public void setType(byte type)
		{
			this.type = type;
			notifyDataSetChanged();
		}
		
		public void addView(View view, byte type)
		{
			if (type == 0)
				rowsRecycle.add(view);
			else
				rowsWater.add(view);
			notifyDataSetChanged();
		}
		
		public int getCount()
		{
			int count = 0;
			if (type == 0)
				count = rowsRecycle.size();
			else
				count = rowsWater.size();
			if (count > 7) count = 7;
			return count;
		}
		
		public Object getItem(int position)
		{
			if (type == 0)
				return rowsRecycle.get(position);
			else
				return rowsWater.get(position);
		}
		
		public long getItemId(int position)
		{
			return position;
		}
		
		public boolean hasStableIds()
		{
			return true;
		}
		
		public View getView(int groupPosition, View convertView, ViewGroup parent)
		{
			if (type == 0)
				return rowsRecycle.get(groupPosition);
			else
				return rowsWater.get(groupPosition);
		}
	}
	
	public void recyclingClicked(View view){
		type = false;
		adapter.setType((byte)0);
		ImageButton tmp1 = (ImageButton)findViewById(R.id.imageButton2);
		ImageButton tmp2 = (ImageButton)findViewById(R.id.imageButton3);
		tmp1.setImageResource(R.drawable.recycle_toggle);
		tmp2.setImageResource(R.drawable.off_refill_toggle);
	}
	
	public void refillClicked(View view){
		type = true;
		adapter.setType((byte)1);
		ImageButton tmp1 = (ImageButton)findViewById(R.id.imageButton2);
		ImageButton tmp2 = (ImageButton)findViewById(R.id.imageButton3);
		tmp2.setImageResource(R.drawable.refill_toggle);
		tmp1.setImageResource(R.drawable.off_recycle_toggle);
	}
}
