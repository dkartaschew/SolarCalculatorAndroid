package com.anonymous.solar.android;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ViewFlipper;

public class MainActivity extends Activity {

	private int WizardViewCount = 0;
	private int WizardViewMember = 0;

	/**
	 * Main Activity entry point.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setButtonActions();
		addViews();
	}

	/**
	 * Add views and set event handlers.
	 */
	private void addViews() {
		ViewFlipper flip = (ViewFlipper) findViewById(R.id.viewFlipperWizard);
		
		//flip.addView(child);

		// when a view is displayed
		flip.setInAnimation(this, android.R.anim.fade_in);
		// when a view disappears
		flip.setOutAnimation(this, android.R.anim.fade_out);
	}

	/**
	 * Create all button actions for wizard navigation
	 */
	private void setButtonActions() {
		Button close = (Button) findViewById(R.id.buttonClose);
		close.setOnClickListener(buttonCloseEvent());
		Button back = (Button) findViewById(R.id.buttonBack);
		back.setOnClickListener(buttonBackEvent());
		Button next = (Button) findViewById(R.id.buttonNext);
		next.setOnClickListener(buttonNextEvent());
	}

	/********************************************************************
	 * Event Handlers
	 ********************************************************************/

	/**
	 * Event handler for the Next button.
	 * 
	 * @return
	 */
	private OnClickListener buttonNextEvent() {
		ViewFlipper view = (ViewFlipper) findViewById(R.id.viewFlipperWizard);
		if (WizardViewMember < WizardViewCount) {
			view.showNext();
			WizardViewMember++;
		}
		return null;
	}

	/**
	 * Event handler for the Back button.
	 * 
	 * @return
	 */
	private OnClickListener buttonBackEvent() {
		ViewFlipper view = (ViewFlipper) findViewById(R.id.viewFlipperWizard);
		if (WizardViewMember > 0) {
			view.showPrevious();
			WizardViewMember--;
		}
		return null;
	}

	/**
	 * Event handler for the Close button.
	 * 
	 * @return
	 */
	private OnClickListener buttonCloseEvent() {
		this.finish();
		return null;
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
			return true;
		case R.id.menuRetrieve:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
