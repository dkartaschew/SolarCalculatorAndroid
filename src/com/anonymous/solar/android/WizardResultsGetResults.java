package com.anonymous.solar.android;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.anonymous.solar.client.SolarCalculatorService;
import com.anonymous.solar.shared.SolarResult;
import com.anonymous.solar.shared.SolarSetup;

public class WizardResultsGetResults extends AsyncTask<SolarSetup, Void, SolarResult> {

	private MainActivity parent;
	private ProgressDialog progressDialog;
	private ArrayList<View> tabs;
	final private static int resultTableHeaderColor = 0xffcccccc;

	public WizardResultsGetResults(MainActivity parent, ArrayList<View> tabs) {
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
		return new SolarCalculatorService().calculateAllResults(parent, params[0]);
	}

	protected void onPostExecute(SolarResult soapResult) {
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
			TableLayout tabResults = (TableLayout) tabs.get(2).findViewById(R.id.tableResults);
			createTable(tabResults, soapResult.getSavingsOverYears());
		}
		TabHost resultsTabHost = (TabHost) parent.findViewById(R.id.tabHostResults);
		resultsTabHost.setEnabled(true);
	}

	/**
	 * Create a table in the specified tablelayout.
	 * 
	 * @param tabResults
	 * @param savingsOverYears
	 */
	private void createTable(TableLayout tabResults, ArrayList<Double> savingsOverYears) {
		// Clear the table.
		tabResults.removeAllViews();

		// Generate the header row.
		TableRow row = new TableRow(parent);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    params.setMargins(1, 1, 1, 1);

		// Add our header columns (Year)
		int numberOfYears = savingsOverYears.size();
		for (int i = 0; i < numberOfYears; i++) {
			TextView header = new TextView(parent);
			header.setText(String.format("Year %d", i+1));
			header.setGravity(Gravity.CENTER);
			header.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
			header.setPadding(1, 1, 1, 1);
			header.setLayoutParams(params);
			header.setBackgroundColor(resultTableHeaderColor);
			header.setTextColor(parent.getResources().getColor(android.R.color.black));
			row.addView(header);
		}
		// Add our row to the table.
		row.setPadding(3, 3, 3, 3);
		tabResults.addView(row);

		// Fill in the data row.
		TableRow row2 = new TableRow(parent);

		// Add our data
		for (int i = 0; i < numberOfYears; i++) {
			TextView header = new TextView(parent);
			header.setText(String.format("$%,.2f", savingsOverYears.get(i)));
			header.setGravity(Gravity.CENTER);
			header.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
			header.setPadding(10, 10, 10, 10);
			row2.addView(header);
		}
		// Add our row to the table.
		row2.setPadding(3, 3, 3, 3);
		tabResults.addView(row2);
	}

}
