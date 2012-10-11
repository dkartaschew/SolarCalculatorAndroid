package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.anonymous.solar.shared.SolarResult;
import com.anonymous.solar.shared.SolarSetup;
import com.google.android.maps.MapActivity;

/**
 * Main Entry Point for the wizard interface for the Android Application
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class MainActivity extends MapActivity {

	// Private state fields.
	private int WizardViewCount = 0;
	private int WizardViewMember = 0;
	private int ReportYears = 25;
	private MainActivity parentContext;

	// Wizard components
	private ViewFlipper wizardViewFlipper;
	private Button closeButton;
	private Button nextButton;
	private Button backButton;
	private ProgressBar progressBar;

	// Button handlers
	private OnClickListener backButtonListener;
	private OnClickListener nextButtonListener;
	private OnClickListener closeButtonListener;

	// Animations
	private Animation animFlipInForward;
	private Animation animFlipOutForward;
	private Animation animFlipInBackward;
	private Animation animFlipOutBackward;

	// Gesture Handling.
	private SimpleOnGestureListener simpleOnGestureListener;
	private GestureDetector gestureDetector;

	// List of Views in ViewFlipper
	private ArrayList<WizardViews> wizardViews = new ArrayList<WizardViews>();

	// State of the current solar setup.
	private SolarSetup solarSetup;

	/**
	 * Main Activity entry point.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		parentContext = this;
		addViews();
		setButtonActions();
		// Set up the progress bar in the main view.
		progressBar = (ProgressBar) findViewById(R.id.progressBarMainActivity);
		progressBar.setMax(wizardViews.size() - 1);
		progressBar.setProgress(0);
		// If SolarSetup == null, then we just started, otherwise we have just
		// resumed.
		if (solarSetup == null) {
			solarSetup = new SolarSetup();
		}
	}

	/**
	 * Activity started thread.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Determine if we have a network connection?
		if (!isOnline()) {
			// Display a warning to the user.

			AlertDialog dialog = new AlertDialog.Builder(this.getApplicationContext()).create();
			dialog.setTitle("Network Connectivity Error");
			dialog.setMessage("There appears to be no network connectivity. Please enable a network connection to use this application.");
			dialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});

			dialog.show();
			while (dialog.isShowing()) {
			}
			buttonCloseEvent();
		}
		// super.onStart();
	}

	/**
	 * Add views and set event handlers.
	 */
	private void addViews() {

		// Ensure that we are always landscape.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		wizardViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperWizard);

		// Add in view animations.
		animFlipInForward = AnimationUtils.loadAnimation(this, R.anim.flipin);
		animFlipOutForward = AnimationUtils.loadAnimation(this, R.anim.flipout);
		animFlipInBackward = AnimationUtils.loadAnimation(this, R.anim.flipin_reverse);
		animFlipOutBackward = AnimationUtils.loadAnimation(this, R.anim.flipout_reverse);

		// Add in gesture handling.
		simpleOnGestureListener = new SimpleOnGestureListener() {

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

				float sensitvity = 50;
				if ((e1.getX() - e2.getX()) > sensitvity) {
					buttonNextEvent();
				} else if ((e2.getX() - e1.getX()) > sensitvity) {
					buttonBackEvent();
				}

				return true;
			}

		};
		gestureDetector = new GestureDetector(simpleOnGestureListener);

		// Add children
		wizardViews.add(new WizardWelcome(this));
		wizardViews.add(new WizardSetupDescription(this));
		wizardViews.add(new WizardLocation(this));
		wizardViews.add(new WizardUsage(this));
		wizardViews.add(new WizardTariff(this));
		wizardViews.add(new WizardElectrical(this));
		wizardViews.add(new WizardPanel(this));
		wizardViews.add(new WizardConfirmation(this));
		wizardViews.add(new WizardResults(this));
		wizardViews.add(new WizardFinish(this));

		// Set the number of views we have for button navigation.
		WizardViewCount = wizardViews.size();
	}

	/**
	 * Create all button actions for wizard navigation
	 */
	private void setButtonActions() {

		// Close button.
		closeButton = (Button) findViewById(R.id.buttonClose);
		closeButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonCloseEvent();
			}
		};
		closeButton.setOnClickListener(closeButtonListener);

		// Next Button
		nextButton = (Button) findViewById(R.id.buttonNext);
		nextButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonNextEvent();
			}
		};
		nextButton.setOnClickListener(nextButtonListener);

		// Back Button
		backButton = (Button) findViewById(R.id.buttonBack);
		backButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonBackEvent();
			}
		};
		backButton.setOnClickListener(backButtonListener);
		// Set back button to disabled.
		backButton.setVisibility(View.INVISIBLE);

		// TODO: remove from final release.
		TextView txt = (TextView) findViewById(R.id.textViewProgress);
		txt.setText(String.format("%d / %d", WizardViewMember + 1, WizardViewCount));
	}

	/**
	 * Get the wizard viewFlipper object.
	 * 
	 * @return the wizard viewFlipper object
	 */
	public ViewFlipper getWizard() {
		return wizardViewFlipper;
	}

	/**
	 * Get the SolarSetup to be used by the wizard
	 * 
	 * @return the solarSetup
	 */
	public SolarSetup getSolarSetup() {
		return solarSetup;
	}

	/********************************************************************
	 * Event Handlers
	 ********************************************************************/

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * Event handler for the Next button.
	 */
	private void buttonNextEvent() {
		if (WizardViewMember < WizardViewCount - 1) {

			// Confirm we wish to move forward 1 view.
			boolean changePanel = false;
			try {
				changePanel = wizardViews.get(WizardViewMember).callbackDispose(true);
			} catch (Exception e) {
			}
			if (changePanel) {
				// Hide keyboard if present.
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(wizardViewFlipper.getApplicationWindowToken(), 0);

				// Animate the move!
				wizardViewFlipper.setInAnimation(animFlipInForward);
				wizardViewFlipper.setOutAnimation(animFlipOutForward);
				wizardViewFlipper.showNext();
				WizardViewMember++;
				wizardViews.get(WizardViewMember).callbackStart();
				progressBar.setProgress(WizardViewMember);

			}

			// TODO: remove from final release.
			TextView txt = (TextView) findViewById(R.id.textViewProgress);
			txt.setText(String.format("%d / %d", WizardViewMember + 1, WizardViewCount));

			// Enable the back button, and disable the next button if on last
			// pane.
			backButton.setVisibility(View.VISIBLE);
			if (WizardViewMember == WizardViewCount - 1) {
				nextButton.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Event handler for the Back button.
	 */
	private void buttonBackEvent() {
		if (WizardViewMember > 0) {

			boolean changePanel = false;
			try {
				changePanel = wizardViews.get(WizardViewMember).callbackDispose(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (changePanel) {
				// Hide keyboard if present.
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(wizardViewFlipper.getApplicationWindowToken(), 0);

				wizardViewFlipper.setInAnimation(animFlipInBackward);
				wizardViewFlipper.setOutAnimation(animFlipOutBackward);
				wizardViewFlipper.showPrevious();
				WizardViewMember--;
				wizardViews.get(WizardViewMember).callbackStart();
				progressBar.setProgress(WizardViewMember);
			}

			// TODO: remove from final release.
			TextView txt = (TextView) findViewById(R.id.textViewProgress);
			txt.setText(String.format("%d / %d", WizardViewMember + 1, WizardViewCount));

			// Disable the back button if on the first pane.
			nextButton.setVisibility(View.VISIBLE);
			if (WizardViewMember == 0) {
				backButton.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Event handler for the Close button.
	 */
	private void buttonCloseEvent() {
		this.finish();
	}

	/**
	 * Option Menu items.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Option menu event handler.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menuQuit:
			this.finish();
			return true;
		case R.id.menuNew:
			solarSetup = new SolarSetup();
			WizardViewMember = 0; // Set wizard back to start screen.
			wizardViewFlipper.setDisplayedChild(0);
			buttonNextEvent(); // Advance to next screen.
			return true;
		case R.id.menuRetrieve:
			// Display the menu to select a saved item.
			displaySavedItems();
			return true;
		case R.id.menuManage:
			displayManageItems();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Display a dialog box of all saved items, and if the user selects, one
	 * return it...
	 * 
	 * @return The Solar Result object that was retrieved, or null for no
	 *         selection.
	 */
	private void displaySavedItems() {
		DeviceLocalStorage database = new DeviceLocalStorage(this, this);
		database.open();
		// Get our items off the internal storage.
		List<SolarResult> results = database.getAllResults();
		database.close();
		if (results == null || results.size() == 0) {
			// Oops no items?
			new SolarAlertDialog().displayAlert(this, "No Items", "No results have been stored on this device");
			return;
		}

		// If we have some items, display them, otherwise return null;

		// Create the dialog.
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Saved Items");

		// Add our saved items.
		ListView itemList = new ListView(this);
		ArrayAdapter<SolarResult> itemAdapter = new ArrayAdapter<SolarResult>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, results);
		itemList.setAdapter(itemAdapter);
		itemList.setClickable(true);

		// Add the list to the dialog.
		alert.setView(itemList);
		// Add a Close button
		alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		final Dialog dialog = alert.create();

		// Add our click handler.
		itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				SolarResult result = (SolarResult) arg0.getAdapter().getItem(arg2);
				dialog.dismiss();
				if (result != null) {
					// Set our configuration
					solarSetup = result.getSolarSetup();
					// Change to the wizard result pane.

					// And display the results.
					wizardViewFlipper.setInAnimation(animFlipInForward);
					wizardViewFlipper.setOutAnimation(animFlipOutForward);
					WizardViewMember = 8;
					wizardViewFlipper.setDisplayedChild(WizardViewMember);
					WizardResults results = (WizardResults) wizardViews.get(WizardViewMember);
					WizardResultsGetResults resultsEngine = new WizardResultsGetResults(parentContext, results.tabs);
					resultsEngine.displayResults(result, false);
					progressBar.setProgress(WizardViewMember);
					TextView txt = (TextView) findViewById(R.id.textViewProgress);
					txt.setText(String.format("%d / %d", WizardViewMember + 1, WizardViewCount));
				}
			}

		});
		dialog.show();
		return;
	}

	/**
	 * Display a dialog box of all saved items, and if the user selects, one
	 * return it...
	 * 
	 * @return The Solar Result object that was retrieved, or null for no
	 *         selection.
	 */
	private void displayManageItems() {
		DeviceLocalStorage database = new DeviceLocalStorage(this, this);
		database.open();
		// Get our items off the internal storage.
		List<SolarResult> results = database.getAllResults();
		database.close();
		if (results == null || results.size() == 0) {
			// Oops no items?
			new SolarAlertDialog().displayAlert(this, "No Items", "No results have been stored on this device");
			return;
		}

		// If we have some items, display them, otherwise return null;

		// Create the dialog.
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Saved Items");

		// Add our saved items.
		final ListView itemList = new ListView(this);
		ItemsAdapter adapter = new ItemsAdapter(this, results);
		itemList.setAdapter(adapter);
		itemList.setClickable(true);
		itemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CheckedTextView tv = (CheckedTextView) arg1;
				toggle(tv);
			}
		});

		alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				DeviceLocalStorage database = new DeviceLocalStorage(parentContext, parentContext);
				database.open();
				for (int i = 0; i < itemList.getChildCount(); i++) {
					View view = itemList.getChildAt(i);
					CheckedTextView cv = (CheckedTextView) view.findViewById(R.id.checkList);
					if (cv.isChecked()) {
						Log.i("result delete", cv.getText().toString());
						SolarResult result = (SolarResult) itemList.getItemAtPosition(i);
						database.deleteResult(result);
					}
				}
				database.close();
			}
		});
		alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

			}
		});
		alert.setView(itemList);
		alert.show();
		return;
	}

	/**
	 * Customer adapter class for the management of saved results.
	 */
	private class ItemsAdapter extends BaseAdapter {
		List<SolarResult> items;

		public ItemsAdapter(Context context, List<SolarResult> item) {
			this.items = item;
		}

		// @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.results_row, null);
			}
			CheckedTextView post = (CheckedTextView) v.findViewById(R.id.checkList);
			post.setText(items.get(position).toString());
			return v;
		}

		public int getCount() {
			return items.size();
		}

		public SolarResult getItem(int position) {
			return items.get(position);
		}

		public long getItemId(int position) {
			return position;
		}
	}

	/**
	 * Toggle the line item in the list.
	 * 
	 * @param v
	 */
	private void toggle(CheckedTextView v) {
		if (v.isChecked()) {
			v.setChecked(false);
		} else {
			v.setChecked(true);
		}
	}

	/**
	 * Customer function for Google Maps.
	 */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Get the number of years to report on.
	 * 
	 * @return The number of years.
	 */
	public Integer getReportYears() {
		return ReportYears;
	}

	/**
	 * Set the number of years to report on.
	 * 
	 * @param reportYears
	 */
	public void setReportYears(int reportYears) {
		if (reportYears > 0) {
			ReportYears = reportYears;
		}
	}

	/**
	 * Determine if the device has a valid network connection.
	 * 
	 * @return true if the device reports a valid network connection, otherwise
	 *         false.
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
