package com.anonymous.solar.android;

import java.util.ArrayList;

import com.anonymous.solar.shared.SolarSetup;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;


/**
 * Main Entry Point for the wizard interface for the Android Application
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class MainActivity extends Activity {

	private int WizardViewCount = 0;
	private int WizardViewMember = 0;
	private ViewFlipper wizardViewFlipper;
	private Button closeButton;
	private Button nextButton;
	private Button backButton;
	
	private OnClickListener backButtonListener;
	private OnClickListener nextButtonListener;
	private OnClickListener closeButtonListener;
	
	private ArrayList<WizardViews> wizardViews = new ArrayList<WizardViews>();
	
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
		solarSetup = new SolarSetup();
	}

	/**
	 * Add views and set event handlers.
	 */
	private void addViews() {
		
		// Ensure that we are always landscape.
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	
		wizardViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipperWizard);
		//wizardViewFlipper.addView(child);
		
		wizardViews.add(new WizardWelcome(this));
		wizardViews.add(new WizardFinish(this));
		
		WizardViewCount = wizardViews.size();

		// when a view is displayed
		wizardViewFlipper.setInAnimation(this, android.R.anim.fade_in);
		// when a view disappears
		wizardViewFlipper.setOutAnimation(this, android.R.anim.fade_out);
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
	    TextView txt = (TextView)findViewById(R.id.textView1);
		txt.setText(String.format("%d / %d", WizardViewMember+1, WizardViewCount));
	}
	
	/**
	 * Get the wizard viewFlipper object.
	 * @return the wizard viewFlipper object
	 */
	public ViewFlipper getWizard(){
		return wizardViewFlipper;
	}

	/**
	 * Get the SolarSetup to be used by the wizard
	 * @return the solarSetup
	 */
	public SolarSetup getSolarSetup() {
		return solarSetup;
	}
	
	/********************************************************************
	 * Event Handlers
	 ********************************************************************/

	/**
	 * Event handler for the Next button.
	 * 
	 * @return
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
				wizardViewFlipper.showNext();
				WizardViewMember++;
				wizardViews.get(WizardViewMember).callbackStart();
			}
			
			// TODO: remove from final release.
			TextView txt = (TextView)findViewById(R.id.textView1);
			txt.setText(String.format("%d / %d", WizardViewMember+1, WizardViewCount));
			
			// Enable the back button, and disable the next button if on last pane.
			backButton.setVisibility(View.VISIBLE);
			if(WizardViewMember == WizardViewCount - 1){
				nextButton.setVisibility(View.INVISIBLE);
			}
		} 
	}

	/**
	 * Event handler for the Back button.
	 * 
	 * @return
	 */
	private void buttonBackEvent() {
		if (WizardViewMember > 0) {
			
			boolean changePanel = false;
			try {
				changePanel = wizardViews.get(WizardViewMember).callbackDispose(false);
			} catch (Exception e) {
			}
			if (changePanel) {
				wizardViewFlipper.showPrevious();
				WizardViewMember--;
				wizardViews.get(WizardViewMember).callbackStart();
			}

			// TODO: remove from final release.
			TextView txt = (TextView)findViewById(R.id.textView1);
			txt.setText(String.format("%d / %d", WizardViewMember+1, WizardViewCount));
			
			// Disable the back button if on the first pane.
			nextButton.setVisibility(View.VISIBLE);
			if(WizardViewMember == 0){
				backButton.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * Event handler for the Close button.
	 * 
	 * @return
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
			WizardViewMember = 0;	// Set wizard back to start screen.
			wizardViewFlipper.setDisplayedChild(0);
			buttonNextEvent(); // Advance to next screen.
			return true;
		case R.id.menuRetrieve:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
