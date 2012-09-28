package com.anonymous.solar.android;

import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.anonymous.solar.shared.CustomerData;
import com.anonymous.solar.shared.SolarSetup;
import com.anonymous.solar.shared.TariffRate;

/**
 * The Customer Usage Wizard Pane.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardTariff extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_tariff;

	// Reference to the parent view.
	private MainActivity parent;

	// Layout widgets.
	private EditText fee11;
	private EditText cost11;
	private EditText fee33;
	private EditText cost33;
	private EditText feedInFee;
	private EditText feeIncrease;
	private Spinner definedTariffs;

	private boolean spinnerInitialised = false;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardTariff(MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		fee11 = (EditText) parent.findViewById(R.id.editTextTariff11Fee);
		cost11 = (EditText) parent.findViewById(R.id.editTextTariff11Cost);
		fee33 = (EditText) parent.findViewById(R.id.editTextTariff33Fee);
		cost33 = (EditText) parent.findViewById(R.id.editTextTariff33Cost);
		feedInFee = (EditText) parent.findViewById(R.id.editTextTariffFeedIn);
		feeIncrease = (EditText) parent.findViewById(R.id.editTextTariffFeeIncrease);
		definedTariffs = (Spinner) parent.findViewById(R.id.spinnerTariffPredefined);

		// Setup predefined locations
		setupSpinner();
	}

	/**
	 * Setup the spinner used for the predefined locations.
	 */
	private void setupSpinner() {
		// Clear the defined locations, and reload from service.
		definedTariffs.setSelection(Adapter.NO_SELECTION);

		// Launch background thread to get our data.
		new WizardTariffSetupTariff(this.parent).execute();

		// Add our handler for selecting items.
		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (spinnerInitialised) {
					TariffRate trate = (TariffRate) arg0.getItemAtPosition(arg2);
					// Populate the text fields.
					fee11.setText(trate.getTariff11Fee().toString());
					cost11.setText(trate.getTariff11Cost().toString());
					fee33.setText(trate.getTariff33Fee().toString());
					cost33.setText(trate.getTariff33Cost().toString());
					feedInFee.setText(trate.getTariffFeedInFee().toString());

				} else {
					spinnerInitialised = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		};
		definedTariffs.setOnItemSelectedListener(spinnerListener);
	}

	@Override
	public boolean callbackStart() {
		SolarSetup global = parent.getSolarSetup();
		CustomerData customer = global.getCustomerData();
		if (customer != null) {
			fee11.setText(customer.getTariff11Fee().toString());
			cost11.setText(customer.getTariff11Cost().toString());
			fee33.setText(customer.getTariff13Fee().toString());
			cost33.setText(customer.getTariff13Cost().toString());
			feedInFee.setText(customer.getFeedInFee().toString());
			feeIncrease.setText(customer.getAnnualTariffIncrease().toString());
		}
		return true;
	}

	@Override
	public boolean callbackDispose(boolean validateInput) {
		SolarSetup global = parent.getSolarSetup();
		CustomerData customer = global.getCustomerData();

		// Get our values.
		double fee11Amount = Double.parseDouble(fee11.getText().toString());
		double cost11Amount = Double.parseDouble(cost11.getText().toString());
		double fee33Amount = Double.parseDouble(fee33.getText().toString());
		double cost33Amount = Double.parseDouble(cost33.getText().toString());
		double feedInFeeAmount = Double.parseDouble(feedInFee.getText().toString());
		double feeIncreaseAmount = Double.parseDouble(feeIncrease.getText().toString());

		try {
			customer.setTariff11Fee(fee11Amount);
			customer.setTariff11Cost(cost11Amount);
			customer.setTariff13Fee(fee33Amount);
			customer.setTariff13Cost(cost33Amount);
			customer.setFeedInFee(feedInFeeAmount);
			customer.setAnnualTariffIncrease(feeIncreaseAmount);
		} catch (Exception e) {
			if (validateInput) {
				new SolarAlertDialog().displayAlert(parent,
						"Invalid parameters entered, please ensure values entered are correct");

				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

}
