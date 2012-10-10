package com.anonymous.solar.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
	private ImageButton findLocation;
	private ImageButton buttonGPS;

	// Reference to the parent view.
	private MainActivity parent;
	private WizardLocation wizardLocation;

	// Private LocationData instance.
	private LocationData locationData;

	// Location Awareness manager
	private boolean GPSAvailable = false;
	private double gpsLongitude = 0.0;
	private double gpsLatitude = 0.0;
	private String gpsLocation = "";
	private LocationManager locationService;

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
		findLocation = (ImageButton) parent.findViewById(R.id.buttonFindLocation);
		buttonGPS = (ImageButton) parent.findViewById(R.id.buttonLocationGPS);

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

		// setup GPS.
		buttonGPS.setEnabled(false);
		setupGPS();

		// setup find location button.
		setupFindLocation();
	}

	/**
	 * Enable the button to use Google gelocation to determine the lat/long of
	 * the user.
	 */
	private void setupFindLocation() {
		OnClickListener findButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Attempt to find the lat/long based on the information
				// entered.
				String locationSearch = name.getText().toString();
				// Only attempt if the user entered something.
				if (locationSearch != null && locationSearch.length() > 1) {
					// Call google services.
					Geocoder gcd = new Geocoder(parent, Locale.getDefault());
					List<Address> addresses = new ArrayList<Address>();
					try {
						addresses = gcd.getFromLocationName(locationSearch, 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (addresses.size() > 0) {
						gpsLongitude = addresses.get(0).getLongitude();
						gpsLatitude = addresses.get(0).getLatitude();
						// Update our information when the button clicked.
						// name.setText(gpsLocation);
						longitude.setText(String.format("%f", gpsLongitude));
						latitude.setText(String.format("%f", gpsLatitude));
						setMapLocation(gpsLatitude, gpsLongitude);
						try {
							locationData.setLocationName(gpsLocation);
						} catch (LocationDataException e) {
							try {
								locationData.setLocationName("Unknown");
							} catch (LocationDataException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						updateMap();
					}
				}
			}
		};
		// Add our listeners...
		findLocation.setOnClickListener(findButtonListener);

	}

	/**
	 * Setup and enable the GPS if available;
	 */
	private void setupGPS() {
		// Create our service listeners
		LocationListener locationListener = new LocationListenerImpl();
		locationService = (LocationManager) parent.getSystemService(Context.LOCATION_SERVICE);

		// Enable the GPS and/or wifi geolocation.
		GPSAvailable = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
		GPSAvailable = GPSAvailable || locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// If GPS/Wifi Available, add the listener.
		if (GPSAvailable) {
			locationService.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			OnClickListener gpsButtonListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Update our information when the button clicked.
					name.setText(gpsLocation);
					longitude.setText(String.format("%f", gpsLongitude));
					latitude.setText(String.format("%f", gpsLatitude));
					setMapLocation(gpsLatitude, gpsLongitude);
					try {
						locationData.setLocationName(gpsLocation);
					} catch (LocationDataException e) {
						try {
							locationData.setLocationName("Unknown");
						} catch (LocationDataException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					updateMap();
				}
			};
			// Add our listeners...
			buttonGPS.setOnClickListener(gpsButtonListener);
			buttonGPS.setEnabled(true);

		}
	}

	/**
	 * GPS Listening service.
	 */
	private class LocationListenerImpl implements LocationListener {
		private LocationListenerImpl() {
		}

		/**
		 * Update our location based on GPS Change.
		 */
		@Override
		public void onLocationChanged(Location location) {
			// Set this location
			gpsLatitude = location.getLatitude();
			gpsLongitude = location.getLongitude();
			Geocoder gcd = new Geocoder(parent, Locale.getDefault());
			List<Address> addresses = new ArrayList<Address>();
			try {
				addresses = gcd.getFromLocation(gpsLatitude, gpsLongitude, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (addresses.size() > 0) {
				if (addresses.get(0).getLocality() != null) {
					gpsLocation = addresses.get(0).getLocality();
				}
				if (addresses.get(0).getAdminArea() != null) {
					if (gpsLocation == null) {
						gpsLocation = addresses.get(0).getAdminArea();
					} else {
						gpsLocation += ", " + addresses.get(0).getAdminArea();
					}
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
			Toast.makeText(parent, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(parent, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
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
					try {
						locationData = new LocationData((LocationData) arg0.getItemAtPosition(arg2));
					} catch (LocationDataException e) {
						locationData = new LocationData();
						try {
							locationData.setLocationName("Unknown");
							locationData.setLatitude(0.0);
							locationData.setLongitude(0.0);
						} catch (LocationDataException e1) {
							// This shouldn't happen, but safety first.
							e1.printStackTrace();
						}

						e.printStackTrace();
					}
					// Populate the text fields.
					name.setText(locationData.getLocationName());
					longitude.setText(locationData.getLongitude().toString());
					latitude.setText(locationData.getLatitude().toString());

					updateMap();

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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(parent.getApplicationContext(), R.layout.spinner_text,
				new String[] { "Street", "Satellite" });
		adapter.setDropDownViewResource(R.layout.spinner_text_menu);
		mapTypes.setAdapter(adapter);

		// Add our handler for selecting items.
		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spinnerMapInitialised) {
					String type = (String) arg0.getItemAtPosition(arg2);
					if (type.compareTo("Street") == 0) {
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
				if (locationData.getLatitude() == null) {
					// Oops!
					new SolarAlertDialog().displayAlert(parent,
							"Invalid Location, please ensure a location has been selected");
					name.requestFocus();
					return false;
				} else {
					// set the name!
					if (name.getText() == null || name.getText().length() == 0) {
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
	 * Method to get the local copy of the location data for the map callback
	 * handler.
	 * 
	 * @return the local copy of the location data.
	 */
	public LocationData getLocalLocationData() {
		return locationData;
	}

	/**
	 * Method to set a new location in the wizard with lat and long being
	 * provided.
	 * 
	 * @param lat
	 * @param lon
	 */
	public void setMapLocation(double lat, double lon) {
		if (locationData == null) {
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

	private void updateMap() {
		// move map to location.
		GeoPoint point = new GeoPoint((int) (locationData.getLatitude() * 1E6),
				(int) (locationData.getLongitude() * 1E6));
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
	}

}
