package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.List;

import com.anonymous.solar.shared.SolarResult;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableRow.LayoutParams;

public class WizardResultsComparison extends View {

	private MainActivity parent;
	private Spinner availableResults;
	private Button buttonAdd;
	private LinearLayout layoutGraph;
	private TableLayout tableComparison;
	private View thisView;

	final private static int resultTableHeaderColor = 0xffcccccc;

	// This List holds all the results we wish to compare against.
	private ArrayList<SolarResult> comparisons;

	public WizardResultsComparison(Context context) {
		super(context);
	}

	/**
	 * Basci constructor
	 * 
	 * @param parent
	 *            The MainActivity parent.
	 * @param soapResult
	 *            The initial result to load
	 * @param parentView
	 *            The view of the parent layout to hold this view.
	 */
	public WizardResultsComparison(MainActivity parent, SolarResult soapResult, LinearLayout parentView) {
		super(parent);
		this.parent = parent;

		// Set our default make up.
		comparisons = new ArrayList<SolarResult>();
		comparisons.add(soapResult);
		
		// Ensure we are at a clean slate.
		parentView.removeAllViews();

		// Construct our internal layout.
		LayoutInflater inflater = parent.getLayoutInflater();
		FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
		thisView = (LinearLayout) inflater.inflate(R.layout.wizard_results_comparison_view, f1, false);
		parentView.addView(thisView);

		// Get our controls.
		buttonAdd = (Button) thisView.findViewById(R.id.buttonComparisonAdd);

		// Populate the drop down;
		DeviceLocalStorage database = new DeviceLocalStorage(parent, parent);
		database.open();
		// Get our items off the internal storage.
		List<SolarResult> results = database.getAllResults();
		database.close();

		availableResults = (Spinner) thisView.findViewById(R.id.spinnerComparison);
		if (results == null || results.size() == 0) {
			// Oops no items? Note: we should never, ever get this, but you
			// never know!
			new SolarAlertDialog().displayAlert(parent, "No Items", "No results have been stored on this device");
			availableResults.setEnabled(false);
			buttonAdd.setEnabled(false);
			return;
		}
		availableResults.setEnabled(true);
		availableResults.setFocusable(true);
		ArrayAdapter<SolarResult> adapter = new ArrayAdapter<SolarResult>(parent.getApplicationContext(),
				R.layout.spinner_text, results);
		adapter.setDropDownViewResource(R.layout.spinner_text_menu);
		availableResults.setAdapter(adapter);

		// Define our add button actions.

		// Create our basic graph.
		layoutGraph = (LinearLayout) thisView.findViewById(R.id.layoutComparisonGraph);
		createGraph(comparisons);

		// Create our table of comparisons.
		tableComparison = (TableLayout) thisView.findViewById(R.id.tableComparisonResults);
		createTable(comparisons);

	}

	/**
	 * Create the table of comparisons.
	 * 
	 * @param comparisons2
	 */
	private void createTable(ArrayList<SolarResult> rows) {
		tableComparison.removeAllViews();

		// Generate the header row.
		TableRow row = new TableRow(parent);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(1, 1, 1, 1);

		// Add our header columns (Setup Names)
		int numResults = rows.size();
		TextView rowHeader = new TextView(parent);
		rowHeader.setText("");
		rowHeader.setGravity(Gravity.CENTER);
		rowHeader.setPadding(5, 1, 5, 1);
		rowHeader.setLayoutParams(params);
		rowHeader.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(rowHeader);
		for (int i = 0; i < numResults; i++) {
			TextView header = new TextView(parent);
			header.setText(rows.get(i).getSolarSetup().getSetupName());
			header.setGravity(Gravity.CENTER);
			header.setTextAppearance(parent, android.R.style.TextAppearance_Small);
			header.setPadding(5, 1, 5, 1);
			header.setLayoutParams(params);
			header.setBackgroundColor(resultTableHeaderColor);
			header.setTextColor(parent.getResources().getColor(android.R.color.black));
			row.addView(header);
		}
		// Add our row to the table.
		row.setPadding(1, 1, 1, 1);
		tableComparison.addView(row);

		// Add our annual savings
		TableRow row2 = new TableRow(parent);
		TextView rowHeader2 = new TextView(parent);
		rowHeader2.setText("Annual Savings");
		rowHeader2.setGravity(Gravity.CENTER);
		rowHeader2.setTextAppearance(parent, android.R.style.TextAppearance_Small);
		rowHeader2.setPadding(5, 1, 5, 1);
		rowHeader2.setLayoutParams(params);
		rowHeader2.setBackgroundColor(resultTableHeaderColor);
		rowHeader2.setTextColor(parent.getResources().getColor(android.R.color.black));
		row2.addView(rowHeader2);
		for (int i = 0; i < numResults; i++) {
			TextView header = new TextView(parent);
			header.setText(String.format("$%,.2f", rows.get(i).getYearlySavings()));
			header.setGravity(Gravity.CENTER);
			header.setTextAppearance(parent, android.R.style.TextAppearance_Small);
			header.setPadding(5, 1, 5, 1);
			row2.addView(header);
		}
		// Add our row to the table.
		row2.setPadding(1, 1, 1, 1);
		tableComparison.addView(row2);
	}

	/**
	 * Create and draw the graph comparing all results.
	 * 
	 * @param grpahs
	 *            The SolarResult information to render on the graph.
	 */
	private void createGraph(ArrayList<SolarResult> graphs) {

		layoutGraph.removeAllViews();

		ArrayList<List<Double>> list = new ArrayList<List<Double>>();
		ArrayList<String> columns = new ArrayList<String>();

		for (int i = 0; i < graphs.size(); i++) {
			list.add(graphs.get(i).getSavingsOverYears());
			columns.add(graphs.get(i).getSolarSetup().getSetupName());
		}
		layoutGraph.addView(new WizardResultGraph(parent, "Cumulative Savings", "Year", list, columns));
	}

}
