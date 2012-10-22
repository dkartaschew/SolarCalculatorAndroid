package com.anonymous.solar.android;

import android.view.View;
import android.widget.EditText;
import android.view.View.OnFocusChangeListener;

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

		// TODO: Setup event handler to update other values when editext loses
		// focus.
		setupEditTextEventHandler();
	}

	/**
	 * Create event handlers for the edit text boxes that when a value is
	 * entered in one field, it is replicated to the other fields accordingly.
	 */
	private void setupEditTextEventHandler() {
		// Set the handler for setting the other fields when the user leaves the
		// daily kWh input field;
		daily.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// Only process when we leave the field.
				if (!hasFocus) {
					String fieldContents = daily.getText().toString();
					if (fieldContents != null && fieldContents.length() != 0) {
						Double value = Double.valueOf(fieldContents);
						// If we have a value, then set the others appropraitely
						if (value != null) {
							Double hourly = value / 24.0;
							daytime.setText(String.format("%.2f", hourly));
							Double monthlyV = value * 30.0;
							monthly.setText(String.format("%.2f", monthlyV));
						}
					}
				}

			}
		});

		// Set the handler for setting the other fields when the user leaves the
		// hourly kWh input field;
		daytime.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// Only process when we leave the field.
				if (!hasFocus) {
					String fieldContents = daytime.getText().toString();
					if (fieldContents != null && fieldContents.length() != 0) {
						Double value = Double.valueOf(fieldContents);
						// If we have a value, then set the others appropraitely
						if (value != null) {
							Double day = value * 24.0;
							daily.setText(String.format("%.2f", day));
							Double monthlyV = day * 30.0;
							monthly.setText(String.format("%.2f", monthlyV));
						}
					}
				}

			}
		});

		// Set the handler for setting the other fields when the user leaves the
		// monthly kWh input field;
		monthly.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// Only process when we leave the field.
				if (!hasFocus) {
					String fieldContents = monthly.getText().toString();
					if (fieldContents != null && fieldContents.length() != 0) {
						Double value = Double.valueOf(fieldContents);
						// If we have a value, then set the others appropraitely
						if (value != null) {
							Double dailyV = value / 30.0;
							daily.setText(String.format("%.2f", dailyV));
							Double hourly = dailyV / 24.0;
							daytime.setText(String.format("%.2f", hourly));
						}
					}
				}

			}
		});

	}

	@Override
	public boolean callbackStart() {
		SolarSetup global = parent.getSolarSetup();
		CustomerData customer = global.getCustomerData();
		if (customer != null) {
			daily.setText(String.format("%.2f", customer.getDailyAverageUsage()));
			daytime.setText(String.format("%.2f", customer.getHourlyAverageUsage()));
			monthly.setText(String.format("%.2f", customer.getMonthlyAverageUsage()));
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

		if (validateInput) {
			if (dailyAmount <= 0.0 && hourlyAmount <= 0.0 && monthlyAmount <= 0.0) {
				new SolarAlertDialog().displayAlert(parent, "Missing Input", "Please enter at least 1 valid amount.");
				return false;
			}
			if (dailyAmount < hourlyAmount || hourlyAmount > monthlyAmount) {
				new SolarAlertDialog().displayAlert(parent, "Invalid Input", "Usage values don't appear correct.");
				return false;
			}
		}
		// Get our current focused handlers...
		if (daily.isFocused()) {
			// populate the other values.
			if (dailyAmount != 0.0) {
				hourlyAmount = dailyAmount / 24.0;
				monthlyAmount = dailyAmount * 30.0;
			}
		} else if (daytime.isFocused()) {
			if (hourlyAmount != 0.0) {
				dailyAmount = hourlyAmount * 24.0;
				monthlyAmount = dailyAmount * 30.0;
			}
		} else if (monthly.isFocused()) {
			if (monthlyAmount != 0.0) {
				dailyAmount = monthlyAmount / 30.0;
				hourlyAmount = dailyAmount / 24.0;
			}
		}

		if (dailyAmount > 0.0) {
			try {
				customer.setDailyAverageUsage(dailyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		if (hourlyAmount > 0.0) {
			try {
				customer.setHourlyAverageUsage(hourlyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		if (monthlyAmount > 0.0) {
			try {
				customer.setMonthlyAverageUsage(monthlyAmount);
			} catch (SolarPanelException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
