package com.anonymous.solar.android;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class WizardLocation extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_location;

	// Layout widgets.
	private Spinner locations;

	private EditText name;
	private EditText longitude;
	private EditText latitude;

	// Reference to the parent view.
	private MainActivity parent;

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
		
	}

	@Override
	public boolean callbackStart() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean callbackDispose(boolean validateInput) {
		// TODO Auto-generated method stub
		return true;
	}

}
