package com.anonymous.solar.android;

import android.widget.EditText;

import com.anonymous.solar.shared.CustomerData;
import com.anonymous.solar.shared.SolarPanelException;
import com.anonymous.solar.shared.SolarSetup;

/**
 * The Customer Usage Wizard Pane.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardUsage extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_usage;

	// Reference to the parent view.
	private MainActivity parent;

	// Layout widgets.
	private EditText daily;
	private EditText daytime;
	private EditText monthly;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardUsage(MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		daily = (EditText) parent.findViewById(R.id.editTextUsageDaily);
		daytime = (EditText) parent.findViewById(R.id.editTextUsageDayTime);
		monthly = (EditText) parent.findViewById(R.id.editTextUsageMonthly);
	}

	@Override
	public boolean callbackStart() {
		SolarSetup global = parent.getSolarSetup();
		CustomerData customer = global.getCustomerData();
		if(customer != null){
			daily.setText(customer.getDailyAverageUsage().toString());
			daytime.setText(customer.getHourlyAverageUsage().toString());
			monthly.setText(customer.getMonthlyAverageUsage().toString());
		}
		return true;
	}

	@Override
	public boolean callbackDispose(boolean validateInput) {
		SolarSetup global = parent.getSolarSetup();
		CustomerData customer = global.getCustomerData();
		
		// Get our values.
		double dailyAmount = Double.parseDouble(daily.getText().toString());
		double hourlyAmount = Double.parseDouble(daytime.getText().toString());
		double monthlyAmount = Double.parseDouble(monthly.getText().toString());
		
		if(validateInput){
			if(dailyAmount <= 0.0 && hourlyAmount <= 0.0 && monthlyAmount <= 0.0){
				new SolarAlertDialog().displayAlert(parent,
						"Please enter at least 1 valid amount.");
				return false;
			}
		}
		if(dailyAmount > 0.0){
			try {
				customer.setDailyAverageUsage(dailyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		if(hourlyAmount > 0.0){
			try {
				customer.setHourlyAverageUsage(hourlyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		if(monthlyAmount > 0.0){
			try {
				customer.setMonthlyAverageUsage(monthlyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
