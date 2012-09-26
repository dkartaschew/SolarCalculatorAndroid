package com.anonymous.solar.shared;

public class CustomerData {
	
	protected Double annualTariffIncrease;
    protected Double dailyAverageUsage;
    protected Double feedInFee;
    protected Double hourlyAverageUsage;
    protected Double monthlyAverageUsage;
    protected Double tariff11Cost;
    protected Double tariff11Fee;
    protected Double tariff13Cost;
    protected Double tariff13Fee;
	
	public CustomerData(){
		dailyAverageUsage = 0.0;
		hourlyAverageUsage = 0.0;
		monthlyAverageUsage = 0.0;
		
		tariff11Cost = 0.0;
		tariff11Fee = 0.0;
		tariff13Cost = 0.0;
		tariff13Fee = 0.0;
		
		feedInFee = 0.0;
		annualTariffIncrease = 0.0;
	}
	
	public CustomerData(Double daily, Double hourly, Double monthly, Double tar11C, Double tar11F,
			Double tar13C, Double tar13F, Double feedFee, Double tarIncrease){
		dailyAverageUsage = daily;
		hourlyAverageUsage = hourly;
		monthlyAverageUsage = monthly;
		
		tariff11Cost = tar11C;
		tariff11Fee = tar11F;
		tariff13Cost = tar13C;
		tariff13Fee = tar13F;
		
		feedInFee = feedFee;
		annualTariffIncrease = tarIncrease;
	}
	
	
	/********************************************************
	 * GETTERS
	 ********************************************************/
	/**
	 * Return the estimated daily average kWh usage
	 * 
	 * @return average daily kWh usage
	 */
	public Double getDailyAverageUsage(){
		return dailyAverageUsage;
	}
	
	/**
	 * Return the estimated monthly average kWh usage
	 * 
	 * @return average daily kWh usage
	 */
	public Double getMonthlyAverageUsage(){
		return monthlyAverageUsage;
	}
	
	/**
	 * Return the estimated hourly average kWh usage
	 * 
	 * @return average daily kWh usage
	 */
	public Double getHourlyAverageUsage(){
		return hourlyAverageUsage;
	}
	
	/**
	 * Return the estimated cost per kWh for Tariff 11
	 * 
	 * @return Tariff 11 Cost
	 */
	public Double getTariff11Cost(){
		return tariff11Cost;
	}
	
	/**
	 * Return the estimated fee for tariff 11
	 * 
	 * @return Tariff 11 Fee
	 */
	public Double getTariff11Fee(){
		return tariff11Fee;
	}
	
	/**
	 * Return the estimated cost per kWh for Tariff 13
	 * 
	 * @return Tariff 13 Cost
	 */
	public Double getTariff13Cost(){
		return tariff13Cost;
	}
	
	/**
	 * Return the estimated fee for tariff 13
	 * 
	 * @return Tariff 13 Fee
	 */
	public Double getTariff13Fee(){
		return tariff13Fee;
	}
	
	/**
	 * Return the estimated feed in fee
	 * 
	 * @return Fee In Fee increase
	 */
	public Double getFeedInFee(){
		return feedInFee;
	}
	
	/**
	 * Return the estimated increase in tariff fees
	 * 
	 * @return Annual Increase in tariff fee
	 */
	public Double getAnnualTariffIncrease(){
		return annualTariffIncrease;
	}
	
	/********************************************************
	 * SETTERS
	 ********************************************************/
	/**
	 * Return the estimated daily average kWh usage
	 * @throws SolarPanelException 
	 * 
	 */
	public void setDailyAverageUsage(Double newAverage) throws SolarPanelException{
		
		if(newAverage == null || newAverage < 0){
			throw new SolarPanelException();
		}
		dailyAverageUsage = newAverage;
	}
	
	/**
	 * Return the estimated monthly average kWh usage
	 * @throws SolarPanelException 
	 */
	public void setMonthlyAverageUsage(Double newAverage) throws SolarPanelException{
		if(newAverage == null || newAverage < 0){
			throw new SolarPanelException();
		}
		monthlyAverageUsage = newAverage;
	}
	
	/**
	 * Set the estimated hourly average kWh usage
	 * @throws SolarPanelException 
	 * 
	 */
	public void setHourlyAverageUsage(Double newAverage) throws SolarPanelException{
		if(newAverage == null || newAverage < 0){
			throw new SolarPanelException();
		}
		hourlyAverageUsage = newAverage;
	}
	
	/**
	 * Set the estimated cost per kWh for Tariff 11
	 * @throws SolarPanelException 
	 * 
	 */
	public void setTariff11Cost(Double newTariffCost) throws SolarPanelException{
		if(newTariffCost == null || newTariffCost < 0){
			throw new SolarPanelException();
		}
		tariff11Cost = newTariffCost;
	}
	
	/**
	 * Set the estimated fee for tariff 11
	 * @throws SolarPanelException 
	 * 
	 */
	public void setTariff11Fee(Double newTariffFee) throws SolarPanelException{
		if(newTariffFee == null || newTariffFee < 0){
			throw new SolarPanelException();
		}
		tariff11Fee = newTariffFee;
	}
	
	/**
	 * Set the estimated cost per kWh for Tariff 13
	 * @throws SolarPanelException 
	 * 
	 */
	public void setTariff13Cost(Double newTariffCost) throws SolarPanelException{
		if(newTariffCost == null || newTariffCost < 0){
			throw new SolarPanelException();
		}
		tariff13Cost = newTariffCost;
	}
	
	/**
	 * Set the estimated fee for tariff 13
	 * @throws SolarPanelException 
	 * 
	 */
	public void setTariff13Fee(Double newTariffFee) throws SolarPanelException{
		if(newTariffFee == null || newTariffFee < 0){
			throw new SolarPanelException();
		}
		tariff13Fee = newTariffFee;
	}
	
	/**
	 * Set the estimated feed in fee
	 * @throws SolarPanelException 
	 * 
	 */
	public void setFeedInFee(Double newFeedInFee) throws SolarPanelException{
		if(newFeedInFee == null || newFeedInFee < 0 ){
			throw new SolarPanelException();
		}
		feedInFee = newFeedInFee;
	}
	
	/**
	 * Set the estimated increase in tariff fees
	 * @throws SolarPanelException 
	 * 
	 */
	public void setAnnualTariffIncrease(Double newAnnualTariff) throws SolarPanelException{
		if(newAnnualTariff == null || newAnnualTariff < 0){
			throw new SolarPanelException();
		}
		annualTariffIncrease = newAnnualTariff;
	}
	
	@Override
	public String toString(){
		return "Daily Average: " + dailyAverageUsage + "<br />"
				+ "Hourly Average: " + hourlyAverageUsage + "<br />"
				+ "Tariff 11 Cost: " +  String.format("$%,.2f", tariff11Cost) + "<br />"
				+ "Tariff 11 Fee: " + String.format("$%,.2f", tariff11Fee) + "<br />"
				+ "Tariff 13 Cost: " +  String.format("$%,.2f", tariff13Cost) + "<br />"
				+ "Tariff 13 Fee: " + String.format("$%,.2f", tariff13Fee) + "<br />"
				+ "Feed In Fee: " + String.format("$%,.2f", feedInFee) + "<br />"
				+ "Annual Tariff Increase: " + annualTariffIncrease + "<br />";
	}
	
	public String toString(boolean htmlTags){
		String about = "";
		
		if(htmlTags){
			about += "<html>";
		}
		
		about += "Daily Average: " + dailyAverageUsage + "<br />"
				 + "Hourly Average: " + hourlyAverageUsage + "<br />"
				 + "Tariff 11 Cost: " +  String.format("$%,.2f", tariff11Cost) + "<br />"
				 + "Tariff 11 Fee: " + String.format("$%,.2f", tariff11Fee) + "<br />"
				 + "Tariff 13 Cost: " +  String.format("$%,.2f", tariff13Cost) + "<br />"
				 + "Tariff 13 Fee: " + String.format("$%,.2f", tariff13Fee) + "<br />"
				 + "Feed In Fee: " + String.format("$%,.2f", feedInFee) + "<br />"
				 + "Annual Tariff Increase: " + annualTariffIncrease + "<br />";
		
		if(htmlTags){
			about += "</html>";
		}
		return about;
	}
	
}
