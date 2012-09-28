package com.anonymous.solar.android;

import java.util.List;

import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anonymous.solar.client.LocationInformationService;
import com.anonymous.solar.shared.LocationData;

public class WizardLocationSetupLocations extends AsyncTask<Void, Void, List<LocationData>> {

	private MainActivity parent;
	
	public WizardLocationSetupLocations(MainActivity parent){
		this.parent = parent;
	}
	
	@Override
	protected List<LocationData> doInBackground(Void... params) {
		
		return new LocationInformationService().StoreLocationGetAll();
	}
	
	protected void onPostExecute(List<LocationData> soapLocations){
		Spinner definedLocations = (Spinner) parent.findViewById(R.id.spinnerLocationPredefined);
		definedLocations.setSelection(Adapter.NO_SELECTION);
		
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

		}
	}

}
