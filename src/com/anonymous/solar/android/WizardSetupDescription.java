/**
 * 
 */
package com.anonymous.solar.android;

import android.widget.EditText;

import com.anonymous.solar.shared.SolarSetup;
import com.anonymous.solar.shared.SolarSetupException;

/**
 * Wizard View for Welcome Screen
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardSetupDescription extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_setup_description;

	// Layout widgets.
	private EditText name;
	private EditText description;

	// Reference to the parent view.
	private MainActivity parent;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardSetupDescription(MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		name = (EditText) parent.findViewById(R.id.editTextSetupName);
		description = (EditText) parent.findViewById(R.id.editTextSetupDescription);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackStart()
	 */
	@Override
	public boolean callbackStart() {
		// Clear all fields.
		if (name != null && description != null) {
			name.setText("");
			description.setText("");
		}

		// Get our global data and set the fields based on this data.
		SolarSetup global = parent.getSolarSetup();

		if (global != null) {
			name.setText(global.getSetupName());
			description.setText(global.getSetupDescription());

			// Set focus on the desired widget.
			name.requestFocus();
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
		
		// Validate input if needed.
		if (validateInput) {
			if ((name.getText() == null) || (name.getText().length() == 0)) {
				// Oops, missing data, need to handle this.
				new SolarAlertDialog().displayAlert(parent, "Invalid Parameter, please ensure a name is present");
				return false;
			}
		}

		SolarSetup global = parent.getSolarSetup();
		if (global != null) {
			// Store the name and description fields.
			try {
				if(name.getText().length() > 0){
					global.setSetupName(name.getText().toString());
				}
				global.setSetupDescription(description.getText().toString());
			} catch (SolarSetupException e) {
				// Oops, missing data, need to handle this.
				new SolarAlertDialog().displayAlert(parent, "Invalid Parameter, please ensure a name is present");
				name.requestFocus();
				return false;
			}
		}
		return true;
	}

}
