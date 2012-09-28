package com.anonymous.solar.android;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.anonymous.solar.shared.LocationData;
import com.anonymous.solar.shared.LocationDataException;
import com.anonymous.solar.shared.SolarSetup;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

/**
 * The Location Wizard Pane.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardLocation extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_location;

	// Layout widgets.
	private EditText name;
	private EditText longitude;
	private EditText latitude;
	private Spinner definedLocations;
	private boolean spinnerInitialised = false;
	private MapView mapView;
	private Spinner mapTypes;
	private boolean spinnerMapInitialised = false;

	// Reference to the parent view.
	private MainActivity parent;
	private WizardLocation wizardLocation;

	// Private LocationData instance.
	private LocationData locationData;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardLocation(MainActivity parent) {
		this.parent = parent;
		this.wizardLocation = this;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		name = (EditText) parent.findViewById(R.id.editTextLocationName);
		longitude = (EditText) parent.findViewById(R.id.editTextLongitude);
		latitude = (EditText) parent.findViewById(R.id.editTextLatitude);
		definedLocations = (Spinner) parent.findViewById(R.id.spinnerLocationPredefined);
		
		// Setup the map
		mapView = (MapView) parent.findViewById(R.id.mapView);
		mapTypes = (Spinner) parent.findViewById(R.id.spinnerMapType);
		mapView.setBuiltInZoomControls(true);
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = parent.getResources().getDrawable(R.drawable.android);
		WizardLocationItemOverlay itemizedoverlay = new WizardLocationItemOverlay(drawable, parent, this);
		mapOverlays.add(itemizedoverlay);
		setupMapSpinner();

		// Setup predefined locations
		setupSpinner();
	}

	/**
	 * Setup the spinner used for the predefined locations.
	 */
	private void setupSpinner() {
		// Clear the defined locations, and reload from service.
		definedLocations.setSelection(Adapter.NO_SELECTION);

		// Launch background thread to get our data.
		new WizardLocationSetupLocations(this.parent).execute();

		// Add our handler for selecting items.
		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spinnerInitialised) {
					locationData = (LocationData) arg0.getItemAtPosition(arg2);
					// Populate the text fields.
					name.setText(locationData.getLocationName());
					longitude.setText(locationData.getLongitude().toString());
					latitude.setText(locationData.getLatitude().toString());
					
					// move map to location.
					GeoPoint point = new GeoPoint((int)(locationData.getLatitude()* 1E6), (int)(locationData.getLongitude()* 1E6));
					MapController controller = mapView.getController();
					controller.animateTo(point);
					mapView.invalidate();
					
					// Display our little man.
					List<Overlay> mapOverlays = mapView.getOverlays();
					OverlayItem overlayitem = new OverlayItem(point, "Location", "Overlay item");
					Drawable drawable = parent.getResources().getDrawable(R.drawable.android);
					WizardLocationItemOverlay itemizedoverlay = new WizardLocationItemOverlay(drawable, parent, wizardLocation);
					// Clear all existing overlays, and add our new overlay.
					mapOverlays.clear();
					mapView.invalidate();
					itemizedoverlay.addOverlay(overlayitem);
					mapOverlays.add(itemizedoverlay);
					
				} else {
					spinnerInitialised = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		};
		definedLocations.setOnItemSelectedListener(spinnerListener);
	}
	
	/**
	 * Setup the spinner used for the predefined locations.
	 */
	private void setupMapSpinner() {
		
		// Clear the defined locations, and reload from service.
		mapTypes.setSelection(Adapter.NO_SELECTION);

		// Add our items
		mapTypes.setEnabled(true);
		mapTypes.setFocusable(true);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getApplicationContext(),
				R.layout.spinner_text, new String[] {"Street", "Satellite"});
		adapter.setDropDownViewResource(R.layout.spinner_text_menu);
		mapTypes.setAdapter(adapter);

		// Add our handler for selecting items.
		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spinnerMapInitialised) {
					String type = (String) arg0.getItemAtPosition(arg2);
					if(type.compareTo("Street") == 0){
						mapView.setSatellite(false);
					} else if (type.compareTo("Satellite") == 0) {
						// Satellite
						mapView.setSatellite(true);
					}
					
				} else {
					spinnerMapInitialised = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		};
		mapTypes.setOnItemSelectedListener(spinnerListener);
	}
	
	/**
	 * Callback for when the pane is brought into view.
	 */
	@Override
	public boolean callbackStart() {
		// Set the name, long and lat.
		if (parent.getSolarSetup() != null) {
			SolarSetup global = parent.getSolarSetup();
			if (global.getLocationInformation() != null) {
				locationData = global.getLocationInformation();
			} else {
				locationData = new LocationData();
			}
		}
		if (locationData != null) {
			if (locationData.getLocationName() != null) {
				name.setText(locationData.getLocationName());
			}
			longitude.setText(locationData.getLongitude().toString());
			latitude.setText(locationData.getLatitude().toString());
		} else {
			locationData = new LocationData();
		}
		return true;
	}

	/**
	 * Callback when the the pane is about to leave view.
	 * 
	 * @return true if ok to move, otherwise false is an error.
	 */
	@Override
	public boolean callbackDispose(boolean validateInput) {
		if (validateInput) {
			if (locationData.getLocationName() == null) {
				// See if we have co-ordinates?
				if(locationData.getLatitude() == null){
					// Oops!
					new SolarAlertDialog().displayAlert(parent,
							"Invalid Location, please ensure a location has been selected");
					name.requestFocus();
					return false;
				} else {
					// set the name!
					if(name.getText() == null || name.getText().length() == 0){
						new SolarAlertDialog().displayAlert(parent,
								"Invalid Location, please ensure a location name has been entered");
						name.requestFocus();
						return false;
					}
				}
			}
			try {
				locationData.setLocationName(name.getText().toString());
			} catch (LocationDataException e) {
				new SolarAlertDialog().displayAlert(parent,
						"Invalid Name, please ensure a valid location name has been entered");
				name.requestFocus();
				return false;
			}

		}
		try {
			parent.getSolarSetup().setLocationInformation(locationData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Method to get the local copy of the location data for the map callback handler.
	 * @return the local copy of the location data.
	 */
	public LocationData getLocalLocationData(){
		return locationData;
	}

	/**
	 * Method to set a new location in the wizard with lat and long being provided.
	 * @param lat
	 * @param lon
	 */
	public void setMapLocation(double lat, double lon) {
		if(locationData == null){
			locationData = new LocationData();
		}
		try {
			locationData.setLatitude(lat);
			locationData.setLongitude(lon);
		} catch (LocationDataException e) {
			e.printStackTrace();
		}
		longitude.setText(locationData.getLongitude().toString());
		latitude.setText(locationData.getLatitude().toString());
	}
	
}
