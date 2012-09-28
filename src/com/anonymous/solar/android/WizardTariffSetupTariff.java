package com.anonymous.solar.android;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.anonymous.solar.client.LocationInformationService;
import com.anonymous.solar.client.TariffInformationService;
import com.anonymous.solar.shared.TariffRate;

public class WizardTariffSetupTariff extends AsyncTask<Void, Void, List<TariffRate>> {

	private MainActivity parent;
	
	public WizardTariffSetupTariff(MainActivity parent){
		this.parent = parent;
	}
	
	@Override
	protected List<TariffRate> doInBackground(Void... params) {
		
		return new TariffInformationService().getTariffRates();
	}
	
	protected void onPostExecute(List<TariffRate> soapTariffs){
		Spinner defineTariffs = (Spinner) parent.findViewById(R.id.spinnerTariffPredefined);
		defineTariffs.setSelection(Adapter.NO_SELECTION);
		
		if (soapTariffs == null || soapTariffs.size() == 0) {
			// disable the spinner.
			defineTariffs.setEnabled(false);
			defineTariffs.setFocusable(false);
		} else {
			// Sort our data by provider name.
			Collections.sort(soapTariffs);
			
			// Add our data.
			defineTariffs.setEnabled(true);
			defineTariffs.setFocusable(true);
			ArrayAdapter<TariffRate> adapter = new ArrayAdapter<TariffRate>(parent.getApplicationContext(),
					R.layout.spinner_text, soapTariffs);
			adapter.setDropDownViewResource(R.layout.spinner_text_menu);
			defineTariffs.setAdapter(adapter);

		}
	}

}
