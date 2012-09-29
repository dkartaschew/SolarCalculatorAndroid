package com.anonymous.solar.android;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anonymous.solar.client.PanelInformationService;
import com.anonymous.solar.shared.SolarPanel;

public class WizardPanelSetupPanels extends AsyncTask<Void, Void, List<SolarPanel>> {

	private MainActivity parent;
	
	public WizardPanelSetupPanels(MainActivity parent){
		this.parent = parent;
	}
	
	@Override
	protected List<SolarPanel> doInBackground(Void... params) {
		
		return new PanelInformationService().getPanels();
	}
	
	protected void onPostExecute(List<SolarPanel> soapPanels){
		
		Spinner definedPanels = (Spinner) parent.findViewById(R.id.spinnerPanels);
		definedPanels.setSelection(Adapter.NO_SELECTION);
		
		if (soapPanels == null || soapPanels.size() == 0) {
			// disable the spinner.
			definedPanels.setEnabled(false);
			definedPanels.setFocusable(false);
		} else {
			// Sort our data by provider name.
			Collections.sort(soapPanels);
			
			// Add our data.
			definedPanels.setEnabled(true);
			definedPanels.setFocusable(true);
			ArrayAdapter<SolarPanel> adapter = new ArrayAdapter<SolarPanel>(parent.getApplicationContext(),
					R.layout.spinner_text, soapPanels);
			adapter.setDropDownViewResource(R.layout.spinner_text_menu);
			definedPanels.setAdapter(adapter);

		}
	}

}
