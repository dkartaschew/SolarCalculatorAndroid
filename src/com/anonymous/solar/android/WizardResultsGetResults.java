package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.anonymous.solar.client.SolarCalculatorService;
import com.anonymous.solar.shared.SolarResult;
import com.anonymous.solar.shared.SolarSetup;

public class WizardResultsGetResults extends AsyncTask<SolarSetup, Void, SolarResult> {

	private MainActivity parent;
	private ProgressDialog progressDialog;
	private ArrayList<View> tabs;
	
	public WizardResultsGetResults(MainActivity parent, ArrayList<View> tabs){
		this.parent = parent;
		progressDialog = new ProgressDialog(parent);
		this.tabs = tabs;
	}
	
	protected void onPreExecute() {
        this.progressDialog.setMessage("Waiting for results.");
        this.progressDialog.show();
    }
	
	@Override
	protected SolarResult doInBackground(SolarSetup... params) {
//		try {
//			Thread.sleep(5000); // TODO: Let's be fun and make it like we are working.
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return new SolarCalculatorService().calculateAllResults(parent, params[0]);
	}
	
	protected void onPostExecute(SolarResult soapResult){
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
        }
		TextView summary = (TextView) parent.findViewById(R.id.textViewResultsSummaryText);
		
		if (soapResult == null) {
			summary.setText("Error occurred with result service. Please try again.");
			return;
		} else {
			// Add our data to the summary view
			summary.setText(Html.fromHtml(soapResult.toString()));
			// Add our data to the graph
			LinearLayout tabGraph = (LinearLayout) tabs.get(1).findViewById(R.id.layoutResultsGraph);
			tabGraph.removeAllViews(); // remove old graphs
			tabGraph.addView(new WizardResultGraph(parent, soapResult.getSavingsOverYears()));
			// Add our data to the table.
		}
		TabHost resultsTabHost = (TabHost) parent.findViewById(R.id.tabHostResults);
		resultsTabHost.setEnabled(true);
	}

}
