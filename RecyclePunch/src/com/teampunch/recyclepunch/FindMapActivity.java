package com.teampunch.recyclepunch;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teampunch.recyclepunch.Database.DatabaseLocation;

public class FindMapActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RESOLUTION_REQUEST_CODE = 9001;
    private static final String ERROR_TAG = "shitsurei, kamimashita";
    private static final String RESOLUTION_SAVE_STATE = "the flub lives on";
    private static final String RECYCLING_TOGGLE_STATE = "running out of ideas, so we recycle old ones";
    private static final String REFILL_TOGGLE_STATE = "refill me up oniichan";
    private static final int INIT_ZOOM_LEVEL = 14;
    private static final int FOCUSED_ZOOM_LEVEL = 19;
	
	private GoogleApiClient apiClient;
	private GoogleMap gm;
	private boolean resolving;
	private boolean drawRecycling, drawRefill;
	private ArrayList<Marker> markers;
	private double userLat, userLong;
	
	private Database database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.push_in_left, R.anim.fade_out);
		boolean first = savedInstanceState == null;
		setContentView(R.layout.activity_find_map);
		resolving = first? false: 
			savedInstanceState.getBoolean(RESOLUTION_SAVE_STATE, false);
		drawRecycling = first? true: 
			savedInstanceState.getBoolean(RECYCLING_TOGGLE_STATE, true);
		drawRefill = first? true:
			savedInstanceState.getBoolean(REFILL_TOGGLE_STATE, true);
        apiClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API)
				.addScope(Drive.SCOPE_FILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
		gm = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng umcp = new LatLng(38.9875, -76.9410);
		gm.setMyLocationEnabled(true);
		gm.moveCamera(CameraUpdateFactory.newLatLngZoom(umcp, INIT_ZOOM_LEVEL));
		apiClient.connect();
		database = Database.loadDataFromStorage(this);
		initMarkers();
		userLat = 0; userLong = 0;
	}
	
	@Override
	protected void onStop(){
		apiClient.disconnect();
		super.onStop();
	}
	
	public void recyclingToggleClicked(View view){
		drawRecycling = !drawRecycling;
		repaintMarkers();
	}
	
	public void refillToggleClicked(View view){
		drawRefill = !drawRefill;
		repaintMarkers();
	}
	
	public void viewallClicked(View view) {
		Intent intent = new Intent(this, MapDetailActivity.class);
		intent.putExtra("com.teampunch.recyclepunch.InitCategory", (byte)(2*(drawRecycling?1:0) + (drawRefill?1:0)));
    	intent.putExtra("com.teampunch.recyclepunch.UserCoord", new double[]{userLat, userLong});
		startActivity(intent);
	}
	
	private void initMarkers(){
		markers = new ArrayList<Marker>();
		for (DatabaseLocation dl : database.getLocations()){
			boolean type = (dl.getType() == (byte)0);
			MarkerOptions marmopt = new MarkerOptions()
					.position(dl.toCoord())
					.title(type? "Recycling Location" : "Water Refill Station")
					.visible(type? drawRecycling : drawRefill)
					.alpha(0.85f)
					.icon(BitmapDescriptorFactory.defaultMarker(type? 0.0f: 180.0f));
			markers.add(gm.addMarker(marmopt));
		}
	}
	
	private void repaintMarkers(){
		int i;
		for (i = 0; i < markers.size() && i < database.getLocations().size(); i++){
			boolean type = database.getLocations().get(i).getType() == (byte)0;
			markers.get(i).setVisible(type? drawRecycling : drawRefill);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_map, menu);
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

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (resolving) {
            return;
        } else if (result.hasResolution()) {
            try {
                resolving = true;
                result.startResolutionForResult(this, RESOLUTION_REQUEST_CODE);
            } catch (SendIntentException e) {
                apiClient.connect();
            }
        } else {
            showErrorDialog(result.getErrorCode());
            resolving = true;
        }
	}

	@Override
	public void onConnected(Bundle hint) {
		Location loc = LocationServices.FusedLocationApi.getLastLocation(apiClient);
		if (loc != null){
			userLat = loc.getLatitude(); userLong = loc.getLongitude();
			gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLong), FOCUSED_ZOOM_LEVEL));
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESOLUTION_REQUEST_CODE) {
            resolving = false;
            if (resultCode == RESULT_OK) {
                if (!apiClient.isConnecting() &&
                        !apiClient.isConnected()) {
                    apiClient.connect();
                }
            }
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(RESOLUTION_SAVE_STATE, resolving);
        outState.putBoolean(RECYCLING_TOGGLE_STATE, drawRecycling);
        outState.putBoolean(REFILL_TOGGLE_STATE, drawRefill);
    }
	
    private void showErrorDialog(int errorCode) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle b = new Bundle();
        b.putInt(ERROR_TAG, errorCode);
        dialogFragment.setArguments(b);
        dialogFragment.show(getSupportFragmentManager(), "error message");
    }
    
    public void onDialogDismissed() {
        resolving = false;
    }
    
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int errorCode = this.getArguments().getInt(ERROR_TAG);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), RESOLUTION_REQUEST_CODE);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((FindMapActivity)getActivity()).onDialogDismissed();
        }
    }
    
    public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.fade_in, R.anim.push_out_right);
	}
}
