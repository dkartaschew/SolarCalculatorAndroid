/**
 * 
 */
package com.anonymous.solar.android;

/**
 * Wizard View for Welcome Screen
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardWelcome extends WizardViews {
	
	// Our default layout.
	private static int layout = R.layout.wizard_start;
	
	private MainActivity parent;

	/**
	 * Default Constructor
	 * @param parent The parent Activity which is hosting the wizard view controls.
	 */
	public WizardWelcome(MainActivity parent) {
		// Set the layout.
		setView(parent, layout);
		
	    this.parent = parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackStart()
	 */
	@Override
	public boolean callbackStart() {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackDispose(boolean)
	 */
	@Override
	public boolean callbackDispose(boolean validateInput) {
		parent.checkOnline();
		return true;
	}

}
