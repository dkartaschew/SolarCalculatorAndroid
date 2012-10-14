/**
 * 
 */
package com.anonymous.solar.android;

import android.text.Html;
import android.widget.EditText;
import android.widget.TextView;

import com.anonymous.solar.shared.SolarSetup;

/**
 * Wizard View for Welcome Screen
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardConfirmation extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_confirmation;

	// Layout widgets.
	private TextView details;
	private EditText projection;

	// Reference to the parent view.
	private MainActivity parent;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardConfirmation(MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		details = (TextView) parent.findViewById(R.id.TextViewConfirmationDetails);
		projection = (EditText) parent.findViewById(R.id.editTextConfirmationYears);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackStart()
	 */
	@Override
	public boolean callbackStart() {

		// Get our global data and set the fields based on this data.
		SolarSetup global = parent.getSolarSetup();

		if (global != null) {
			details.setText(Html.fromHtml(global.toString()));
			projection.setText(parent.getReportYears().toString());
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackDispose(boolean)
	 */
	@Override
	public boolean callbackDispose(boolean validateInput) {

		if ((projection.getText() == null) || (projection.getText().length() == 0)) {
			// Oops, missing data, need to handle this.
			if (validateInput) {
				new SolarAlertDialog().displayAlert(parent, "Missing Input", "Invalid Parameter, please ensure a year value is present");
				return false;
			}
		}
		try {
			parent.setReportYears(Integer.parseInt(projection.getText().toString()));
		} catch (Exception e) {
			if (validateInput) {
				new SolarAlertDialog().displayAlert(parent, "Missing Input",
						"Invalid Parameter, please ensure a year value is present and valid");
				return false;
			}
		}
		return true;
	}

}
