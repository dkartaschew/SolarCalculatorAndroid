package com.anonymous.solar.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Generic helper class to display a dialog box.
 * @author darran
 *
 */
public class SolarAlertDialog {
	
	AlertDialog dialog;
	
	/**
	 * Display a dialog box with the following parameters
	 * @param context
	 * @param title
	 * @param msg
	 */
	public void displayAlert(MainActivity context, String title, String msg){
		
		dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		           }
		       });
		
		dialog.show();
	}
	
	/**
	 * Return if the dialog is being displayed.
	 * @return
	 */
	public boolean isShowing(){
		return dialog.isShowing();
	}
}
