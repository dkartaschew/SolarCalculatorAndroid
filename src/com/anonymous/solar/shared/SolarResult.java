package com.anonymous.solar.shared;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Class to hold the result of the solar output calculation from the server and
 * to handle any additional calculations using that result
 * 
 * @author 07377495 Luke Durkan, 07627505 Darran Kartaschew (modified for Android).
 * @version 1.0
 * 
 */
@Root(name="SolarResult", strict=false)
public class SolarResult {
	
	@Element
    private SolarSetup solarSetup;
	@ElementList (inline=true)
	private List<ResultsDetails> resultDetailsList;
    @Element(required=false)
    private Long ID;
    @Element(required=false)
    private String dateTime;
    @Element(required=false)
    private Double initialSystemCost;
	
	public SolarResult() {

	}
	
	public SolarResult(SolarSetup solarSetup) throws SolarResultException {
		SolarSetupExceptionCheck(solarSetup);
		this.solarSetup = solarSetup;
		resultDetailsList = new ArrayList<ResultsDetails>();
		ID = (long) 0;
		dateTime = null;
	}
	
	/**
	 * This method returns the solarSetup that was used to calculate the results
	 * 
	 * @return a SolarSetup object that contains the current solar setup
	 */
	public SolarSetup getSolarSetup() {
		return solarSetup;
	}
	
	/**
	 * 
	 * @param solarSetup
	 * @throws SolarResultException
	 */
	public void setSolarSetup(SolarSetup solarSetup) throws SolarResultException {
		SolarSetupExceptionCheck(solarSetup);
		this.solarSetup = solarSetup;
	}
	
	/**
	 * This method gets a result details breakdown of the results
	 * 
	 * @return a ResultsDetails object which breaks down the power output;
	 */
	public ArrayList<ResultsDetails> getResultsDetailsList() {
		return (ArrayList<ResultsDetails>)resultDetailsList;
	}
	
	/**
	 * This methods set the results details 
	 * 
	 * @param newResultDetails the new results breakdown from the calculation
	 */
	public void setResultsDeatilsList(ArrayList<ResultsDetails> newResultDetailsList) {
		resultDetailsList = newResultDetailsList;
	}
	
	/**
	 * This method gets the unique ID for this calculation
	 * 
	 * @return an int which represents the ID for this calculation
	 */
	public long getID() {
		return ID;
	}
	
	/**
	 * This method sets the unique ID for this calculation
	 * 
	 * @param newID the new ID for this transaction
	 */
	public void setID(long newID) {
		ID = newID;
	}
	
	/**
	 * This method get the time the calculation occurred
	 * 
	 * @return the date and time the calculation occurred
	 */
	public Date getDateTime() {
		try {
			return new SimpleDateFormat().parse(dateTime);
		} catch (ParseException e) {
			return new Date();
		}
	}
	
	/**
	 * This method sets the time the calculation occurred
	 * 
	 * @param newDateTime - a Date type that represents the new time
	 * 						the calculation occured
	 */
	public void setDateTime(Date newDateTime) {
		dateTime = newDateTime.toGMTString();
	}
	
	/**
	 * This method returns the cost of the solar system
	 * 
	 * @return a double that represents the cost of the solar system
	 */
	public double getInitialSystemCost() {
		return initialSystemCost;
	}
	
	/**
	 * This method sets the cost of the solarSystem
	 * 
	 * @param newInitialSystemCost - a double type that represents the
	 * 								 new cost of the system
	 */
	public void setInitialSystemCost(double newInitialSystemCost) {
		initialSystemCost = newInitialSystemCost;
	}
	
	/**
	 * This method returns the average daily savings(income) for the given month and year
	 * 
	 * @param day - the day in the year in which to find the daily savings for (1 <= day <= 365)
	 * @param year - the year in which the given day is located
	 * @return a double that represents the daily savings from a given day and year ($)
	 */
	public double getDailySavings(int day, int year) {
		double dailySavings = 0.0;
		
		int month = getMonthForDay(day);
		double monthlySavings = resultDetailsList.get(((year - 1) * 12) + month).getIncome();
		dailySavings = monthlySavings / daysInMonth(month);
		
		return dailySavings;
	}
	
	/**
	 * This method returns the yearly savings from the system for the given year
	 * 
	 * @param year - the year in which to get the yearly savings for (year >= 1)
	 * @return - a double that represents the yearly savings for the system for
	 * 			 the given year ($)
	 */
	public double getYearlySavings(int year) {
		double yearlySavings = 0;
		
		for(int i = (year - 1) * 12; i < ((year - 1) * 12) + 12; i++) {
			yearlySavings += resultDetailsList.get(i).getIncome();
		}
		
		return yearlySavings;
	}
	
	/**
	 * This method returns the cumulative yearly savings for the system from the start of the
	 * plan up to the year given.
	 * 
	 * @param year the year in which to calculate the cumulative savings up to. (year >= 1)
	 * @return a double representing the cumulative savings up to the given year
	 */
	public double getCumulativeYearlySavings(int year) {
		double cumulativeSavings = 0.0;
		
		for (int i = 0; i < ((year - 1) * 12) + 12; i++) {
			cumulativeSavings += resultDetailsList.get(i).getIncome();
		}
		
		return cumulativeSavings;
	}
	
	/**
	 * This method returns the power generated by the system on the given day and year
	 * 
	 * @param day - the day in which to find the power generated for (1 <= day <= 365)
	 * @param year - the year in which the day is located (year >= 1)
	 * @return a double representing the power generated on the given day in the given year (kwH)
	 */
	public double getDailyPowerGenerated(int day, int year) {
		double dailyPowerGenerated = 0.0;
		
		int month = getMonthForDay(day);
		double monthlyPowerGenerated = resultDetailsList.get(
				((year - 1) * 12) + month).getPowerGenerated();
		dailyPowerGenerated = monthlyPowerGenerated / daysInMonth(month);
		
		return dailyPowerGenerated;
	}
	
	/**
	 * This method calculate the power generated by the system for the given year
	 * 
	 * @param year - the year in which to find the power generated for (1 >= year);
	 * @return a double representing the power generation of the system for the given year (kwH)
	 */
	public double getYearlyPowerGenerated(int year) {
		double yearlyPowerGenerated = 0.0;
		
		for(int i = (year - 1) * 12; i < ((year - 1) * 12) + 12; i++) {
			yearlyPowerGenerated += resultDetailsList.get(i).getPowerGenerated();
		}
		
		return yearlyPowerGenerated;
	}
	
	/**
	 * This method finds the number of days in a given month
	 * 
	 * @param month the month in which to find the number of days for (0 <= month <= 11)
	 * @return an integer representing the number of days in the given month
	 */
	private int daysInMonth(int month) {
		int numberOfDays = 0;
		
		switch(month) {
			//January
			case 0:
				numberOfDays = 31;
				break;
			//Feburary
			case 1:
				numberOfDays = 28;
				break;
			//March
			case 2:
				numberOfDays = 31;
				break;
			//April
			case 3:
				numberOfDays = 30;
				break;
			//May
			case 4:
				numberOfDays = 31;
				break;
			//June
			case 5:
				numberOfDays = 30;
				break;
			//July
			case 6:
				numberOfDays = 31;
				break;
			//August
			case 7:
				numberOfDays = 31;
				break;
			//September
			case 8:
				numberOfDays = 30;
				break;
			//October
			case 9:
				numberOfDays = 31;
				break;
			//November
			case 10:
				numberOfDays = 30;
				break;
			//December
			case 11:
				numberOfDays = 31;
				break;
		}
		
		return numberOfDays;
	}
	
	/**
	 * This method finds the month in which the provided day is located in
	 * 
	 * @param day - the day in which to find the month it is located in
	 * @return an integer between 0 and 11 representing the month the day is in
	 */
	private int getMonthForDay(int day) {
		int month = 0;
		//Jan
		if (day >= 1 && day <= 31) {
			month = 0;
		}
		//Feb
		else if (day >= 32 && day <= 59) {
			month = 1;
		}
		//Mar
		else if (day >= 60 && day <= 90) {
			month = 2;
		}
		//Apr
		else if (day >= 91 && day <= 120) {
			month = 3;
		}
		//May
		else if (day >= 121 && day <= 151) {
			month = 4;
		}
		//Jun
		else if (day >= 152 && day <= 181) {
			month = 5;
		}
		//Jul
		else if (day >= 182 && day <= 212) {
			month = 6;
		}
		//Aug
		else if (day >= 213 && day <= 243) {
			month = 7;
		}
		//Sep
		else if (day >= 244 && day <= 273) {
			month = 8;
		}
		//Oct
		else if (day >= 274 && day <= 304) {
			month = 9;
		}
		//Nov
		else if (day >= 305 && day <= 334) {
			month = 10;
		}
		//Dec
		else if (day >= 335 && day <= 365) {
			month = 11;
		}
		
		return month;
	}
	
	/**
	 * This method checks that the solar setup passed is a valid solar setup
	 * 
	 * @param solarSetup the solar setup to calculate the results for
	 * @throws SolarResultException - indicates there is a problem with the solar setup 
	 * 								  being passed
	 */
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
	

	public String toString2() {
		String details = "";
		return details;
	}

	@Override
	public String toString() {
		return solarSetup.setupName + " ( " + getDateTime().toLocaleString() + ")";
	}

	public List<Double> getSavingsOverYears() {
		ArrayList<Double> ROI = new ArrayList<Double>();
		int years = resultDetailsList.size()/12;
		for(int i = 0; i < years; i++){
			Double savings = 0.00;
			for(int j = 0; j < 12; j++){
				savings += resultDetailsList.get((i*12) + j).getIncome();
			}
			ROI.add(savings);
		}
		return ROI;
	}
	
}
