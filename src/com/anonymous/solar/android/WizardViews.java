package com.anonymous.solar.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


/**
 * The default handlers that all client views within the wizard MUST implement.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public abstract class WizardViews {

	/**
	 * Default callback handler that will get called when the view is brought
	 * into focus.
	 * 
	 * @return true, if the callback succeeded, otherwise false. (false will
	 *         rollback the operation).
	 */
	public abstract boolean callbackStart();

	/**
	 * Default callback handler that will get called when the view is from
	 * focus.
	 * 
	 * @return true, if the callback succeeded, otherwise false. (false will
	 *         rollback the operation).
	 */
	public abstract boolean callbackDispose(boolean validateInput);

	protected void setView(final MainActivity parent, final int layout){
		LayoutInflater inflater = (LayoutInflater)parent.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View viewLoader=(View)inflater.inflate(layout, null);
	    parent.getWizard().addView(viewLoader);
	}
}
