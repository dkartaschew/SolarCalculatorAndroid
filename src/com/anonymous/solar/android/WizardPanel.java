package com.anonymous.solar.android;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
	private Button editPanelButton;
	private OnClickListener editBttonListener;
	private Button addPanelButton;
	private OnClickListener addButtonListener;
	private Button removePanelButton;
	private OnClickListener removeBttonListener;
	private Spinner definedPanels;

	private List<SolarPanel> spinner_panels;
	private ArrayList<SolarPanels> panels;

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

		// Setup Edit Button.
		setupButtons();

		// Setup predefined panels
		setupSpinner();
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
	}

	/**
	 * Setup the edit button for the solar panel information.
	 */
	private void buttonPanelEditEvent() {

		AlertDialog.Builder alert = new AlertDialog.Builder(parent);

		alert.setTitle("Edit Panel Information");
		// alert.setMessage("Modify Inverter Information");

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
	 * Add a panel configuration to the table.
	 * 
	 * @param lpanels
	 */
	protected void addRow(SolarPanels lpanels) {
		TableLayout table = (TableLayout) parent.findViewById(R.id.tablePanels);

		TableRow row = new TableRow(parent);

		// Add our five columns (Name)
		TextView tvName = new TextView(parent);
		tvName.setText(lpanels.getPanelType().toString());
		tvName.setGravity(Gravity.LEFT);
		tvName.setTextAppearance(parent, android.R.attr.textAppearanceMedium);
		row.addView(tvName);

		// Add Wattage
		TextView tvWattage = new TextView(parent);
		tvWattage.setText(lpanels.getPanelType().getPanelWattage().toString() + "W");
		tvWattage.setGravity(Gravity.CENTER);
		tvWattage.setTextAppearance(parent, android.R.attr.textAppearanceMedium);
		row.addView(tvWattage);

		// Add Count
		TextView tvCount = new TextView(parent);
		tvCount.setText(lpanels.getPanelCount().toString());
		tvCount.setGravity(Gravity.CENTER);
		tvCount.setTextAppearance(parent, android.R.attr.textAppearanceMedium);
		row.addView(tvCount);

		// Add Direction
		TextView tvDirection = new TextView(parent);
		tvDirection.setText(String.format("%.0f", lpanels.getPanelDirection()));
		tvDirection.setGravity(Gravity.CENTER);
		tvDirection.setTextAppearance(parent, android.R.attr.textAppearanceMedium);
		row.addView(tvDirection);

		// Add Azimuth
		TextView tvAzimuth = new TextView(parent);
		tvAzimuth.setText(String.format("%.0f", lpanels.getPanelAzimuth()));
		tvAzimuth.setGravity(Gravity.CENTER);
		tvAzimuth.setTextAppearance(parent, android.R.attr.textAppearanceMedium);
		row.addView(tvAzimuth);

		// Add our row to the table.
		row.setPadding(5, 5, 5, 5);
		row.setLongClickable(true);
		row.setClickable(true);
		row.setFocusable(true);
		table.addView(row);
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

	@Override
	public boolean callbackStart() {
		SolarSetup global = parent.getSolarSetup();

		panels = global.getSolarPanels();
		if (panels == null) {
			panels = new ArrayList<SolarPanels>();
		}

		// TODO : populate the SolarPanels Table.

		return true;
	}

	@Override
	public boolean callbackDispose(boolean validateInput) {
		SolarSetup global = parent.getSolarSetup();
		if (validateInput) {
			if (panels.size() == 0) {
				new SolarAlertDialog().displayAlert(parent, "Please enter at least 1 solar panel configuration");
				return false;
			}
		}
		if (global != null) {
			try {
				global.setSolarPanels(panels);
			} catch (SolarSetupException e) {
				if (validateInput) {
					new SolarAlertDialog().displayAlert(parent, "Please enter at least 1 solar panel configuration");
					return false;
				}
			}
		}
		return true;
	}

}
