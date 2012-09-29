package com.anonymous.solar.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.anonymous.solar.shared.SolarPanel;

/**
 * SOAP Handler class for getting Inverter Information from the AppEngine Web
 * Service.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class PanelInformationService {

	private final static String SOAP_URI = "http://anonymous-solarenergy.appspot.com/SPanelService";
	private final static String SOAP_NAMESPACE = "http://server.solar.anonymous.com/";

	private final static String SOAP_METHOD = "getPanels";
	private final static String SOAP_ACTION = "http://server.solar.anonymous.com/getPanels";

	/**
	 * Retrieve a list of all locations stored in the web service.
	 * 
	 * @return List of LocationData objects.
	 */
	public List<SolarPanel> getPanels() {
		ArrayList<SolarPanel> panels = new ArrayList<SolarPanel>();

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
				SolarPanel panel = new SolarPanel();
				// get the next item.
				SoapObject element = (SoapObject) response.get(i);
				// Unmarshall the element.
//			      <xs:element name="key" type="xs:long" minOccurs="0"/>
//			      <xs:element name="panelCost" type="xs:double" minOccurs="0"/>
//			      <xs:element name="panelLifeYears" type="xs:int" minOccurs="0"/>
//			      <xs:element name="panelLossYear" type="xs:double" minOccurs="0"/>
//			      <xs:element name="panelManufacturer" type="xs:string" minOccurs="0"/>
//			      <xs:element name="panelManufacturerCode" type="xs:string" minOccurs="0"/>
//			      <xs:element name="panelName" type="xs:string" minOccurs="0"/>
//			      <xs:element name="panelRRP" type="xs:double" minOccurs="0"/>
//			      <xs:element name="panelWattage" type="xs:double" minOccurs="0"/>
				
				panel.setPanelName(element.getPrimitivePropertyAsString("panelName"));
				panel.setPanelManufacturer(element.getPrimitivePropertyAsString("panelManufacturer"));
				panel.setPanelManufacturerCode(element.getPrimitivePropertyAsString("panelManufacturerCode"));
				panel.setPanelLifeYears(Integer.parseInt(element.getPrimitivePropertyAsString("panelLifeYears")));
				panel.setPanelRRP(Double.parseDouble(element.getPrimitivePropertyAsString("panelRRP")));
				panel.setPanelCost(Double.parseDouble(element.getPrimitivePropertyAsString("panelCost")));
				panel.setPanelLossYear(Double.parseDouble(element.getPrimitivePropertyAsString("panelLossYear")));
				panel.setPanelWattage(Double.parseDouble(element.getPrimitivePropertyAsString("panelWattage")));

				panels.add(panel);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return panels;

	}
}
