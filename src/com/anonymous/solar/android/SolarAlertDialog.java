package com.anonymous.solar.android;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class SolarAlertDialog {
	
	public void displayAlert(MainActivity context, String msg){
		
		AlertDialog dialog = new AlertDialog.Builder(context).create();
		dialog.setTitle("Missing Input");
		dialog.setMessage(msg);
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   
		           }
		       });
		
		dialog.show();
	}
}
