package com.teampunch.recyclepunch;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.teampunch.recyclepunch.Database.DatabaseLocation;

public class ContribMapActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RESOLUTION_REQUEST_CODE = 9001;
    private static final String ERROR_TAG = "shitsurei, kamimashita";
    private static final String RESOLUTION_SAVE_STATE = "the flub lives on";
    private static final int INIT_ZOOM_LEVEL = 14;
    private static final int FOCUSED_ZOOM_LEVEL = 19;
	
	private GoogleApiClient apiClient;
	private GoogleMap gm;
	private boolean resolving;
	
	private Database database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contrib_map);
		resolving = (savedInstanceState == null)? false: 
			savedInstanceState.getBoolean(RESOLUTION_SAVE_STATE, false);
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
		for (DatabaseLocation dl : database.getLocations()){
			MarkerOptions marmopt = new MarkerOptions()
					.position(dl.toCoord())
					.icon(BitmapDescriptorFactory.defaultMarker((dl.getType() == 0)? 0.0f: 0.5f));
			gm.addMarker(marmopt);
		}
	}
		
	@Override
	protected void onStop(){
		apiClient.disconnect();
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contrib_map, menu);
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
			gm.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), FOCUSED_ZOOM_LEVEL));
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
}
