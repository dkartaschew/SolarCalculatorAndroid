package com.anonymous.solar.android;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anonymous.solar.client.InverterInformationService;
import com.anonymous.solar.shared.SolarInverter;

public class WizardElectricalSetupInverters extends AsyncTask<Void, Void, List<SolarInverter>> {

	private MainActivity parent;
	private WizardElectrical pane;
	
	public WizardElectricalSetupInverters(MainActivity parent, WizardElectrical pane){
		this.parent = parent;
		this.pane = pane;
	}
	
	@Override
	protected List<SolarInverter> doInBackground(Void... params) {
		
		return new InverterInformationService().getInverters();
	}
	
	protected void onPostExecute(List<SolarInverter> soapInverters){
		// Store the list in the panes' data fields.
		pane.setInverters(soapInverters);
		
		Spinner definedInverters = (Spinner) parent.findViewById(R.id.spinnerInverters);
		definedInverters.setSelection(Adapter.NO_SELECTION);
		
		if (soapInverters == null || soapInverters.size() == 0) {
			// disable the spinner.
			definedInverters.setEnabled(false);
			definedInverters.setFocusable(false);
		} else {
			// Sort our data by provider name.
			Collections.sort(soapInverters);
			
			// Add our data.
			definedInverters.setEnabled(true);
			definedInverters.setFocusable(true);
			ArrayAdapter<SolarInverter> adapter = new ArrayAdapter<SolarInverter>(parent.getApplicationContext(),
					R.layout.spinner_text, soapInverters);
			adapter.setDropDownViewResource(R.layout.spinner_text_menu);
			definedInverters.setAdapter(adapter);

		}
	}

}
