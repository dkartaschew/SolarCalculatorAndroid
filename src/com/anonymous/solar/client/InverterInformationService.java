package com.anonymous.solar.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.anonymous.solar.shared.SolarInverter;
import com.anonymous.solar.shared.TariffRate;

/**
 * SOAP Handler class for getting Inverter Information from the AppEngine Web
 * Service.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class InverterInformationService {

	private final static String SOAP_URI = "http://anonymous-solarenergy.appspot.com/SInverterService";
	private final static String SOAP_NAMESPACE = "http://server.solar.anonymous.com/";

	private final static String SOAP_METHOD = "getInverters";
	private final static String SOAP_ACTION = "http://server.solar.anonymous.com/getInverters";

	/**
	 * Retrieve a list of all locations stored in the web service.
	 * 
	 * @return List of LocationData objects.
	 */
	public List<SolarInverter> getInverters() {
		ArrayList<SolarInverter> inverters = new ArrayList<SolarInverter>();

		// Start SOAP request.
		SoapObject Request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(Request);

		HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URI);

		// Attempt to execute the method call.
		try {
			httpTransport.call(SOAP_ACTION, envelope);
			// Get our returned objects and count.
			Vector<SoapObject> response = (Vector<SoapObject>) envelope.getResponse();
			int responseCount = response.size();

			// loop through our responses to unmarshall them...
			for (int i = 0; i < responseCount; i++) {
				SolarInverter inverter = new SolarInverter();
				// get the next item.
				SoapObject element = (SoapObject) response.get(i);
				// Unmarshall the element.
//				  <xs:element name="INITIAL_VALUES" type="xs:double" minOccurs="0"/>
//			      <xs:element name="key" type="xs:long" minOccurs="0"/>
//			      <xs:element name="inverterCost" type="xs:double" minOccurs="0"/>
//			      <xs:element name="inverterEfficiency" type="xs:double" minOccurs="0"/>
//			      <xs:element name="inverterLifeYears" type="xs:int" minOccurs="0"/>
//			      <xs:element name="inverterLossYear" type="xs:double" minOccurs="0"/>
//			      <xs:element name="inverterManufacturer" type="xs:string" minOccurs="0"/>
//			      <xs:element name="inverterManufacturerCode" type="xs:string" minOccurs="0"/>
//			      <xs:element name="inverterName" type="xs:string" minOccurs="0"/>
//			      <xs:element name="inverterRRP" type="xs:double" minOccurs="0"/>
//			      <xs:element name="inverterWattage" type="xs:double" minOccurs="0"/>
				
				inverter.setInverterName(element.getPrimitivePropertyAsString("inverterName"));
				inverter.setInverterManufacturer(element.getPrimitivePropertyAsString("inverterManufacturer"));
				inverter.setInverterManufacturerCode(element.getPrimitivePropertyAsString("inverterManufacturerCode"));
				inverter.setInverterLifeYears(Integer.parseInt(element.getPrimitivePropertyAsString("inverterLifeYears")));
				inverter.setInverterRRP(Double.parseDouble(element.getPrimitivePropertyAsString("inverterRRP")));
				inverter.setInverterCost(Double.parseDouble(element.getPrimitivePropertyAsString("inverterCost")));
				inverter.setInverterEfficiency(Double.parseDouble(element.getPrimitivePropertyAsString("inverterEfficiency")));
				inverter.setInverterLossYear(Double.parseDouble(element.getPrimitivePropertyAsString("inverterLossYear")));
				inverter.setInverterWattage(Double.parseDouble(element.getPrimitivePropertyAsString("inverterWattage")));

				inverters.add(inverter);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return inverters;

	}
}
