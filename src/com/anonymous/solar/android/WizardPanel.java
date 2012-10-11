package com.anonymous.solar.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.anonymous.solar.shared.SolarPanel;
import com.anonymous.solar.shared.SolarPanels;
import com.anonymous.solar.shared.SolarSetup;
import com.anonymous.solar.shared.SolarSetupException;

/**
 * The Customer Usage Wizard Pane.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class WizardPanel extends WizardViews {

	// Our default layout.
	private static int layout = R.layout.wizard_panels;

	// Reference to the parent view.
	private MainActivity parent;

	// Layout widgets.
	private TableLayout panelTable;
	final private static int panelTableHeaderColor = 0xffcccccc;
	private Button editPanelButton;
	private OnClickListener editBttonListener;
	private Button addPanelButton;
	private OnClickListener addButtonListener;
	private Button removePanelButton;
	private OnClickListener removeButtonListener;
	private Spinner definedPanels;

	private ArrayList<SolarPanels> panels;

	// Sensor services.
	private final int DEFAULT_SENSOR_DELAY = 100000;
	private SensorManager mSensorManager;
	private Sensor accelerometer;
	private Sensor magnetometer;
	private boolean haveSensors;
	private float deviceAzimuth = 0;
	private float devicePitch = 0;
	private float deviceRoll = 0;

	/**
	 * Default Constructor
	 * 
	 * @param parent
	 *            The parent Activity which is hosting the wizard view controls.
	 */
	public WizardPanel(MainActivity parent) {
		this.parent = parent;

		// Set the layout.
		setView(parent, layout);

		// Set the layout widgets.
		definedPanels = (Spinner) parent.findViewById(R.id.spinnerPanels);
		editPanelButton = (Button) parent.findViewById(R.id.buttonPanelEdit);
		addPanelButton = (Button) parent.findViewById(R.id.buttonPanelAdd);
		removePanelButton = (Button) parent.findViewById(R.id.buttonPanelDelete);
		panelTable = (TableLayout) parent.findViewById(R.id.tablePanels);
		// Setup Edit Button.
		setupButtons();

		// Setup predefined panels
		setupSpinner();

		// Setup the compass, etc.

		setupCompass();
	}

	/**
	 * Setup the use of the compass sensor.
	 */
	private void setupCompass() {
		mSensorManager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
			// Success! There's a magnetometer.
			accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			haveSensors = true;
		} else {
			// No compass, so just ignore the functions.
			haveSensors = false;
		}
	}

	private class sensorEventHandler implements SensorEventListener {

		private float[] mGravity;
		private float[] mGeomagnetic;
		private EditText Direction;
		private EditText Azimuth;
		private View dialog;

		public sensorEventHandler(EditText Direction, EditText Azimuth, View dialog) {
			this.Direction = Direction;
			this.Azimuth = Azimuth;
			this.dialog = dialog;
		}

		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				mGravity = event.values;
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				mGeomagnetic = event.values;
			if (mGravity != null && mGeomagnetic != null) {
				float R[] = new float[9];
				float I[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
				if (success) {
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);
					deviceAzimuth = orientation[0]; // orientation contains:
													// azimuth, pitch and roll
					devicePitch = orientation[1];
					deviceRoll = orientation[2];
					
					// Convert direction from -180..0..180 to 0..360
					if(deviceAzimuth < 0){
						deviceAzimuth = 360 + (float)Math.toDegrees(deviceAzimuth);
					} else {
						deviceAzimuth = (float)Math.toDegrees(deviceAzimuth);
					}
					
					if(devicePitch < 0){
						// We are facing the wrong direction so fix up the direction.
						// Basically make north = south.
						deviceAzimuth -= 180;
						if(deviceAzimuth < 0){
							deviceAzimuth = 360 + deviceAzimuth;
						}
					}
					
					// Ensure our compass is 0..360
					deviceAzimuth = (float) (deviceAzimuth % 360.00);

					// Update the text boxes.
					Direction.setText(String.format("%.0f", deviceAzimuth));
					Azimuth.setText(String.format("%.0f", Math.toDegrees(Math.abs(devicePitch))));
					dialog.invalidate();
				}
			}
		}
	}

	/**
	 * Create all button actions for wizard navigation
	 */
	private void setupButtons() {

		// Close button.
		editBttonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPanelEditEvent();
			}
		};
		editPanelButton.setOnClickListener(editBttonListener);

		addButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPanelAddEvent();
			}
		};
		addPanelButton.setOnClickListener(addButtonListener);

		removeButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonPanelRemoveEvent();
			}
		};
		removePanelButton.setOnClickListener(removeButtonListener);
	}

	/**
	 * Setup the edit button for the solar panel information.
	 */
	private void buttonPanelEditEvent() {

		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle("Edit Panel Information");

		LayoutInflater inflater = parent.getLayoutInflater();
		FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
		// f1.addView(inflater.inflate(R.layout.inverter_edit, f1, false));
		View view = inflater.inflate(R.layout.panel_edit, f1, false);
		alert.setView(view);

		// Get our dialog elements
		final EditText cost = (EditText) view.findViewById(R.id.editTextPanelEditCost);
		final EditText rrp = (EditText) view.findViewById(R.id.editTextPanelEditRRP);
		final EditText wattage = (EditText) view.findViewById(R.id.editTextPanelEditWattage);
		final EditText life = (EditText) view.findViewById(R.id.editTextPanelEditLife);
		final EditText effLoss = (EditText) view.findViewById(R.id.editTextPanelEditEfficicencyLoss);

		// Set our information.
		final SolarPanel panel = (SolarPanel) definedPanels.getSelectedItem();

		cost.setText(panel.getPanelCost().toString());
		rrp.setText(panel.getPanelRRP().toString());
		wattage.setText(panel.getPanelWattage().toString());
		life.setText(panel.getPanelLifeYears().toString());
		effLoss.setText(panel.getPanelLossYear().toString());

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// record the values
				try {
					panel.setPanelCost(Double.parseDouble(cost.getText().toString()));
					panel.setPanelRRP(Double.parseDouble(rrp.getText().toString()));
					panel.setPanelWattage(Double.parseDouble(wattage.getText().toString()));
					panel.setPanelLossYear(Double.parseDouble(effLoss.getText().toString()));
					panel.setPanelLifeYears(Integer.parseInt(life.getText().toString()));
				} catch (Exception e) {
					// kill the exception.
				}
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();

	}

	/**
	 * Setup the add button for adding a solar panel to the table.
	 */
	private void buttonPanelAddEvent() {
		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle("Add Panel Configuration");
		// alert.setMessage("Modify Inverter Information");

		LayoutInflater inflater = parent.getLayoutInflater();
		FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
		// f1.addView(inflater.inflate(R.layout.inverter_edit, f1, false));
		View view = inflater.inflate(R.layout.panel_edittable, f1, false);
		alert.setView(view);

		// Get our dialog elements
		final EditText count = (EditText) view.findViewById(R.id.editTextPanelEditCount);
		final EditText direction = (EditText) view.findViewById(R.id.editTextPanelEditDirection);
		final EditText azimuth = (EditText) view.findViewById(R.id.editTextPanelEditAzimuth);
		final CheckBox useSensors = (CheckBox) view.findViewById(R.id.checkBoxUseSensor);

		useSensors.setEnabled(haveSensors);
		final sensorEventHandler eventHandler = new sensorEventHandler(direction, azimuth, view);

		// Set our information.
		final SolarPanel panel = (SolarPanel) definedPanels.getSelectedItem();
		final SolarPanels lpanels = new SolarPanels();

		count.setText("4");
		direction.setText("0.0");
		azimuth.setText("0.0");

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// record the values
				try {
					int panelCount = (Integer.parseInt(count.getText().toString()));
					double panelDirection = (Double.parseDouble(direction.getText().toString()));
					double panelAzimuth = (Double.parseDouble(azimuth.getText().toString()));
					lpanels.setPanelType(panel);
					lpanels.setPanelCount(panelCount);
					lpanels.setPanelDirection(panelDirection);
					lpanels.setPanelAzimuth(panelAzimuth);
					panels.add(lpanels);

					// Add the row to the table.
					addRow(lpanels);

				} catch (Exception e) {
					e.printStackTrace();
				}
				mSensorManager.unregisterListener(eventHandler, accelerometer);
				mSensorManager.unregisterListener(eventHandler, magnetometer);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				mSensorManager.unregisterListener(eventHandler, accelerometer);
				mSensorManager.unregisterListener(eventHandler, magnetometer);
			}
		});

		// Enable the check box event handler for the sensors.
		useSensors.setChecked(false);
		useSensors.setEnabled(haveSensors);
		if (haveSensors) {
			useSensors.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// We simple enable or disable the sensor event handler for
					// this.
					if (isChecked) {
						mSensorManager.registerListener(eventHandler, accelerometer, DEFAULT_SENSOR_DELAY);
						mSensorManager.registerListener(eventHandler, magnetometer, DEFAULT_SENSOR_DELAY);
					} else {
						mSensorManager.unregisterListener(eventHandler, accelerometer);
						mSensorManager.unregisterListener(eventHandler, magnetometer);
					}
				}
			});
		}

		alert.show();
		useSensors.setChecked(false);
	}

	/**
	 * Remove the current selected row from the table.
	 */
	private void buttonPanelRemoveEvent() {
		int numberOfRows = panelTable.getChildCount();
		// Scan all rows for the selected flag, and remove as needed.
		for (int i = 1; i < numberOfRows; i++) {
			TableRow row = (TableRow) panelTable.getChildAt(i);
			if (row != null) {
				if (row.isSelected()) {
					panels.remove(i - 1);
					panelTable.removeView(row);
				}
			}
		}
	}

	/**
	 * Add the default header row to the table.
	 */
	protected void addHeaderRow() {
		TableRow row = new TableRow(parent);

		// Add our five columns (Name)
		TextView tvName = new TextView(parent);
		tvName.setText(R.string.panelsName);
		tvName.setGravity(Gravity.CENTER);
		tvName.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		tvName.setPadding(1, 1, 1, 1);
		tvName.setBackgroundColor(panelTableHeaderColor);
		tvName.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(tvName);

		// Add Wattage
		TextView tvWattage = new TextView(parent);
		tvWattage.setText(R.string.panelsWattage);
		tvWattage.setGravity(Gravity.CENTER);
		tvWattage.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		tvWattage.setPadding(1, 1, 1, 1);
		tvWattage.setBackgroundColor(panelTableHeaderColor);
		tvWattage.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(tvWattage);

		// Add Count
		TextView tvCount = new TextView(parent);
		tvCount.setText(R.string.panelsCount);
		tvCount.setGravity(Gravity.CENTER);
		tvCount.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		tvCount.setPadding(1, 1, 1, 1);
		tvCount.setBackgroundColor(panelTableHeaderColor);
		tvCount.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(tvCount);

		// Add Direction
		TextView tvDirection = new TextView(parent);
		tvDirection.setText(R.string.panelsDiection);
		tvDirection.setGravity(Gravity.CENTER);
		tvDirection.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		tvDirection.setPadding(1, 1, 1, 1);
		tvDirection.setBackgroundColor(panelTableHeaderColor);
		tvDirection.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(tvDirection);

		// Add Azimuth
		TextView tvAzimuth = new TextView(parent);
		tvAzimuth.setText(R.string.panelsAzimuth);
		tvAzimuth.setGravity(Gravity.CENTER);
		tvAzimuth.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		tvAzimuth.setPadding(1, 1, 1, 1);
		tvAzimuth.setBackgroundColor(panelTableHeaderColor);
		// tvAzimuth.setBackgroundResource(android.R.color.secondary_text_light);
		tvAzimuth.setTextColor(parent.getResources().getColor(android.R.color.black));
		row.addView(tvAzimuth);

		// Add our row to the table.
		row.setPadding(3, 3, 3, 3);
		panelTable.addView(row);
	}

	/**
	 * Add a panel configuration to the table.
	 * 
	 * @param lpanels
	 */
	protected void addRow(SolarPanels lpanels) {

		TableRow row = new TableRow(parent);

		// Add our five columns (Name)
		TextView tvName = new TextView(parent);
		tvName.setText(lpanels.getPanelType().toString());
		tvName.setGravity(Gravity.LEFT);
		tvName.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		row.addView(tvName);

		// Add Wattage
		TextView tvWattage = new TextView(parent);
		tvWattage.setText(lpanels.getPanelType().getPanelWattage().toString() + "W");
		tvWattage.setGravity(Gravity.CENTER);
		tvWattage.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		row.addView(tvWattage);

		// Add Count
		TextView tvCount = new TextView(parent);
		tvCount.setText(lpanels.getPanelCount().toString());
		tvCount.setGravity(Gravity.CENTER);
		tvCount.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		row.addView(tvCount);

		// Add Direction
		TextView tvDirection = new TextView(parent);
		tvDirection.setText(String.format("%.2f", lpanels.getPanelDirection()));
		tvDirection.setGravity(Gravity.CENTER);
		tvDirection.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		row.addView(tvDirection);

		// Add Azimuth
		TextView tvAzimuth = new TextView(parent);
		tvAzimuth.setText(String.format("%.2f", lpanels.getPanelAzimuth()));
		tvAzimuth.setGravity(Gravity.CENTER);
		tvAzimuth.setTextAppearance(parent, android.R.style.TextAppearance_Medium);
		row.addView(tvAzimuth);

		// Add our row to the table.
		row.setPadding(3, 10, 3, 10);
		row.setLongClickable(true);
		row.setClickable(true);
		row.setFocusable(true);
		row.setFocusableInTouchMode(true);
		row.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				setRowBackground(v);
				// get the panel information.
				int index = panelTable.indexOfChild(v);
				longClickPanelEvent(v, panels.get(index - 1));
				// Unset the background.
				v.setBackgroundColor(0);
				v.clearFocus();
				v.setSelected(false);
				return true;
			}
		});
		row.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Clear all views backgrounds;
				setRowBackground(v);
			}
		});
		panelTable.addView(row);
	}

	/**
	 * Display the modify details dialog for a table row.
	 */
	private void longClickPanelEvent(final View v, final SolarPanels lpanels) {
		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle("Modify Panel Configuration");
		// alert.setMessage("Modify Inverter Information");

		LayoutInflater inflater = parent.getLayoutInflater();
		FrameLayout f1 = (FrameLayout) parent.findViewById(android.R.id.custom);
		// f1.addView(inflater.inflate(R.layout.inverter_edit, f1, false));
		View view = inflater.inflate(R.layout.panel_edittable, f1, false);
		alert.setView(view);

		// Get our dialog elements
		final EditText count = (EditText) view.findViewById(R.id.editTextPanelEditCount);
		final EditText direction = (EditText) view.findViewById(R.id.editTextPanelEditDirection);
		final EditText azimuth = (EditText) view.findViewById(R.id.editTextPanelEditAzimuth);
		final CheckBox useSensors = (CheckBox) view.findViewById(R.id.checkBoxUseSensor);

		useSensors.setEnabled(haveSensors);
		final sensorEventHandler eventHandler = new sensorEventHandler(direction, azimuth, view);

		count.setText(lpanels.getPanelCount().toString());
		direction.setText(lpanels.getPanelDirection().toString());
		azimuth.setText(lpanels.getPanelAzimuth().toString());

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// record the values
				try {
					int panelCount = (Integer.parseInt(count.getText().toString()));
					double panelDirection = (Double.parseDouble(direction.getText().toString()));
					double panelAzimuth = (Double.parseDouble(azimuth.getText().toString()));
					// Update the stored panel information.
					lpanels.setPanelCount(panelCount);
					lpanels.setPanelDirection(panelDirection);
					lpanels.setPanelAzimuth(panelAzimuth);
					// update the table row.
					TableRow tr = (TableRow) v;
					((TextView) tr.getChildAt(2)).setText(lpanels.getPanelCount().toString());
					((TextView) tr.getChildAt(3)).setText(String.format("%.2f", lpanels.getPanelDirection()));
					((TextView) tr.getChildAt(4)).setText(String.format("%.2f", lpanels.getPanelAzimuth()));

				} catch (Exception e) {
					e.printStackTrace();
				}
				mSensorManager.unregisterListener(eventHandler, accelerometer);
				mSensorManager.unregisterListener(eventHandler, magnetometer);
			}
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
				mSensorManager.unregisterListener(eventHandler, accelerometer);
				mSensorManager.unregisterListener(eventHandler, magnetometer);
			}
		});
		// Enable the check box event handler for the sensors.
		useSensors.setChecked(false);
		useSensors.setEnabled(haveSensors);
		if (haveSensors) {
			useSensors.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// We simple enable or disable the sensor event handler for
					// this.
					if (isChecked) {
						mSensorManager.registerListener(eventHandler, accelerometer, DEFAULT_SENSOR_DELAY);
						mSensorManager.registerListener(eventHandler, magnetometer, DEFAULT_SENSOR_DELAY);
					} else {
						mSensorManager.unregisterListener(eventHandler, accelerometer);
						mSensorManager.unregisterListener(eventHandler, magnetometer);
					}
				}
			});
		}

		alert.show();
		useSensors.setChecked(false);
	}

	/**
	 * Clears all the highlights for all rows, then sets the background colour.
	 * 
	 * @param v
	 */
	private void setRowBackground(View v) {
		// Clear all rows
		int numberOfRows = panelTable.getChildCount();
		for (int i = 1; i < numberOfRows; i++) {
			panelTable.getChildAt(i).setBackgroundColor(0);
			panelTable.getChildAt(i).clearFocus();
			panelTable.getChildAt(i).setSelected(false);
		}
		// Set our row.
		v.setBackgroundColor(panelTableHeaderColor);
		v.requestFocus();
		v.setSelected(true);
	}

	/**
	 * Setup the spinner used for the predefined locations.
	 */
	private void setupSpinner() {
		// Clear the defined locations, and reload from service.
		definedPanels.setSelection(Adapter.NO_SELECTION);

		// Launch background thread to get our data.
		new WizardPanelSetupPanels(this.parent).execute();

		// Add our handler for selecting items.
		OnItemSelectedListener spinnerPanelListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				SolarPanel panel = (SolarPanel) arg0.getItemAtPosition(arg2);
				// TODO: I don't think we actually need to do anything here?
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		};
		definedPanels.setOnItemSelectedListener(spinnerPanelListener);
	}

	/**
	 * Callback handler on create
	 */
	@Override
	public boolean callbackStart() {
		SolarSetup global = parent.getSolarSetup();

		panels = global.getSolarPanels();
		if (panels == null) {
			panels = new ArrayList<SolarPanels>();
		}

		TableLayout table = (TableLayout) parent.findViewById(R.id.tablePanels);
		// Clear the table and re-add all the rows.
		table.removeAllViews();
		addHeaderRow();
		for (SolarPanels pan : panels) {
			addRow(pan);
		}

		return true;
	}

	/**
	 * Callback handler on dispose
	 */
	@Override
	public boolean callbackDispose(boolean validateInput) {
		SolarSetup global = parent.getSolarSetup();
		if (validateInput) {
			if (panels.size() == 0) {
				new SolarAlertDialog().displayAlert(parent, "Missing Input", "Please enter at least 1 solar panel configuration");
				return false;
			}
		}
		if (global != null) {
			try {
				global.setSolarPanels(panels);
			} catch (SolarSetupException e) {
				if (validateInput) {
					new SolarAlertDialog().displayAlert(parent, "Missing Input", "Please enter at least 1 solar panel configuration");
					return false;
				}
			}
		}
		return true;
	}

}
