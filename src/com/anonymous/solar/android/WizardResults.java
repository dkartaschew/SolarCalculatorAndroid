/**
 * 
 */
package com.anonymous.solar.android;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;

import com.anonymous.solar.shared.SolarSetup;

/**
 * Wizard View for Welcome Screen
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardResults extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_results;

	// Layout widgets.
	private TabHost resultsTabHost;
	private ArrayList<View> tabs;

	// Reference to the parent view.
	private MainActivity parent;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardResults(final MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		resultsTabHost = (TabHost) parent.findViewById(R.id.tabHostResults);
		resultsTabHost.setup();
		tabs = new ArrayList<View>();

		TabHost.TabSpec spec = resultsTabHost.newTabSpec("Summary");
		spec.setIndicator("Summary");
		spec.setContent(new TabContentFactory() {
			
			@Override
			public View createTabContent(String tag) {
				LayoutInflater inflater = parent.getLayoutInflater();
				FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
				View view = inflater.inflate(R.layout.wizard_results_summary, f1, false);
				tabs.add(view);
				return view;
			}
		});
		resultsTabHost.addTab(spec);

		TabHost.TabSpec spec2 = resultsTabHost.newTabSpec("Graph");
		spec2.setIndicator("Graph");
		spec2.setContent(new TabContentFactory() {
			
			@Override
			public View createTabContent(String tag) {
				LayoutInflater inflater = parent.getLayoutInflater();
				FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
				View view = inflater.inflate(R.layout.wizard_results_graph, f1, false);
				tabs.add(view);
				return view;
			}
		});
		resultsTabHost.addTab(spec2);
		
		TabHost.TabSpec spec3 = resultsTabHost.newTabSpec("Details");
		spec3.setIndicator("Details");
		spec3.setContent(new TabContentFactory() {
			
			@Override
			public View createTabContent(String tag) {
				LayoutInflater inflater = parent.getLayoutInflater();
				FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
				View view = inflater.inflate(R.layout.wizard_results_detail, f1, false);
				tabs.add(view);
				return view;
			}
		});
		resultsTabHost.addTab(spec3);

		resultsTabHost.setCurrentTab(0);
		resultsTabHost.setCurrentTab(1);
		resultsTabHost.setCurrentTab(2);
		resultsTabHost.setCurrentTab(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.anonymous.solar.android.WizardViews#callbackStart()
	 */
	@Override
	public boolean callbackStart() {

		// Get our global data and set the fields based on this data.
		SolarSetup global = parent.getSolarSetup();
		resultsTabHost.setEnabled(false);
		TextView summary = (TextView) parent.findViewById(R.id.textViewResultsSummaryText);
		summary.setText(R.string.resultsWaitSummary);
		if (global != null) {
			// Get our Solar Setup and call the webservice.
			// Have it display the progress dialog and populate the tabs when done.
			// Launch background thread to get our data.
			new WizardResultsGetResults(this.parent, tabs).execute(global);
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

		return true;
	}

}
