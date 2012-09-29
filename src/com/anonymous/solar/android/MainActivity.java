package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.Locale;

import com.anonymous.solar.shared.LocationData;
import com.anonymous.solar.shared.LocationDataException;
import com.anonymous.solar.shared.SolarSetup;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

import android.location.Geocoder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

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
		addViews();
		setButtonActions();
		progressBar = (ProgressBar)findViewById(R.id.progressBarMainActivity);
		progressBar.setMax(wizardViews.size()-1);
		progressBar.setProgress(0);
		if (solarSetup == null) {
			solarSetup = new SolarSetup();
		}
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
		TextView txt = (TextView) findViewById(R.id.textView1);
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
			TextView txt = (TextView) findViewById(R.id.textView1);
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
			TextView txt = (TextView) findViewById(R.id.textView1);
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
			return true;
		default:
			return super.onOptionsItemSelected(item);
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
}
