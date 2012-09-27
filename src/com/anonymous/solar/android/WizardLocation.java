package com.anonymous.solar.android;

import java.util.List;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.anonymous.solar.client.LocationInformationService;
import com.anonymous.solar.shared.LocationData;
import com.anonymous.solar.shared.SolarSetup;
import com.anonymous.solar.shared.SolarSetupException;

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

	// Reference to the parent view.
	private MainActivity parent;

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

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		name = (EditText) parent.findViewById(R.id.editTextLocationName);
		longitude = (EditText) parent.findViewById(R.id.editTextLongitude);
		latitude = (EditText) parent.findViewById(R.id.editTextLatitude);
		definedLocations = (Spinner) parent.findViewById(R.id.spinnerLocationPredefined);
		setupSpinner();
	}

	/**
	 * Setup the spinner used for the predefined locations.
	 */
	private void setupSpinner() {
		// Clear the defined locations, and reload from service.
		definedLocations.setSelection(Adapter.NO_SELECTION);

		List<LocationData> soapLocations = new LocationInformationService().StoreLocationGetAll();

		if (soapLocations == null || soapLocations.size() == 0) {
			// disable the spinner.
			definedLocations.setEnabled(false);
			definedLocations.setFocusable(false);
		} else {

			// Add our data.
			definedLocations.setEnabled(true);
			definedLocations.setFocusable(true);
			ArrayAdapter<LocationData> adapter = new ArrayAdapter<LocationData>(parent.getApplicationContext(),
					R.layout.spinner_text, soapLocations);
			adapter.setDropDownViewResource(R.layout.spinner_text_menu); 
			definedLocations.setAdapter(adapter);

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
		if (locationData.getLocationName() != null) {
			name.setText(locationData.getLocationName());
		}
		longitude.setText(locationData.getLongitude().toString());
		latitude.setText(locationData.getLatitude().toString());
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
				// Oops!
				new SolarAlertDialog().displayAlert(parent,
						"Invalid Location, please ensure a location has been selected");
				name.requestFocus();
				return false;
			}

		}
		try {
			parent.getSolarSetup().setLocationInformation(locationData);
		} catch (SolarSetupException e) {
			e.printStackTrace();
		}
		return true;
	}

}
