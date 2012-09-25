/**
 * 
 */
package com.anonymous.solar.android;

/**
 * Wizard View for finish screen.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardFinish extends WizardViews {
	
	// Our default layout.
	private static int layout = R.layout.wizard_finish;

	/**
	 * Default Constructor
	 * @param parent The parent Activity which is hosting the wizard view controls.
	 */
	public WizardFinish(MainActivity parent) {
		// Set the layout.
		setView(parent, layout);
		
	    // Add event handlers for this layout.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackStart()
	 */
	@Override
	public boolean callbackStart() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackDispose(boolean)
	 */
	@Override
	public boolean callbackDispose(boolean validateInput) {
		return true;
	}

}
