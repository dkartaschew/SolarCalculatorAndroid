package com.anonymous.solar.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold the result of the solar output calculation from the server and
 * to handle any additional calculations using that result
 * 
 * @author 07377495 Luke Durkan
 * @version 1.0
 * 
 */
public class SolarResult {

	protected double dailySavings;
	protected double monthlySavings;
	protected List<Double> savingsOverYears;
	protected SolarSetup solarSetup;
	protected double yearlySavings;

	public SolarResult() {
		savingsOverYears = new ArrayList<Double>();
		dailySavings = 0.0;
		yearlySavings = 0.0;

	}

	public SolarResult(SolarSetup solarSetup) throws SolarResultException {
		SolarSetupExceptionCheck(solarSetup);
		savingsOverYears = new ArrayList<Double>();
		dailySavings = 0.0;
		monthlySavings = 0.0;
		yearlySavings = 0.0;
		this.solarSetup = solarSetup;

	}

	public SolarSetup getSolarSetup() {
		return solarSetup;
	}

	public void setSolarSetup(SolarSetup solarSetup) throws SolarResultException {
		SolarSetupExceptionCheck(solarSetup);
		solarSetup = solarSetup;
	}

	public double getDailySavings() {
		return dailySavings;
	}

	public void setDailySavings(double newDailySavings) {
		dailySavings = newDailySavings;
	}

	public double getYearlySavings() {
		return yearlySavings;
	}

	public void setYearlySavings(double newYearlySavings) {
		yearlySavings = newYearlySavings;
	}

	public double getMonthlySavings() {
		return monthlySavings;
	}

	public void setMonthlySavings(double newMonthlySavings) {
		monthlySavings = newMonthlySavings;
	}

	public ArrayList<Double> getSavingsOverYears() {
		return (ArrayList<Double>) savingsOverYears;
	}

	public void setSavingsOverYears(ArrayList<Double> newSavingsOverYears) {
		savingsOverYears = newSavingsOverYears;
	}

	public double getCumulativeSavings(int year) {
		double cumulativeSavings = 0;

		for (int i = 0; i < year; i++) {
			cumulativeSavings = cumulativeSavings + savingsOverYears.get(i);
		}

		return cumulativeSavings;
	}

	private void SolarSetupExceptionCheck(SolarSetup solarSetup) throws SolarResultException {
		if (solarSetup.getInverter() == null) {
			throw new SolarResultException("You cannot have a null inverter");
		}
		if (solarSetup.getSolarPanels() == null) {
			throw new SolarResultException("You cannot have null panels");
		}
		if (solarSetup.getSolarPanels().isEmpty()) {
			throw new SolarResultException("You cannot have an empty array of panels");
		}
	}

	@Override
	public String toString() {
		String details = "<html>";

		details += "<b>Daily Savings: </b>";
		details += String.format("$%,.2f", dailySavings) + "<br /><br />";

		details += "<b>Monthly Savings: </b>";
		details += String.format("$%,.2f", monthlySavings) + "<br /><br />";

		details += "<b>Yearly Savings: </b>";
		details += String.format("$%,.2f", yearlySavings) + "<br /><br />";

		details += "</html>";
		return details;
	}

}
