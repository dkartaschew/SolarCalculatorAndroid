package com.anonymous.solar.client;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.anonymous.solar.shared.LocationData;

/**
 * SOAP Handler class for getting Location Information from the AppEngine Web
 * Service.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class LocationInformationService {

	private final static String SOAP_URI = "http://anonymous-solarenergy.appspot.com/LocationInformationService";
	private final static String SOAP_NAMESPACE = "http://server.solar.anonymous.com/";

	private final static String SOAP_METHOD = "StoreLocationGetAll";
	private final static String SOAP_ACTION = "http://server.solar.anonymous.com/StoreLocationGetAll";

	/**
	 * Retrieve a list of all locations stored in the web service.
	 * 
	 * @return List of LocationData objects.
	 */
	public List<LocationData> StoreLocationGetAll() {
		ArrayList<LocationData> locations = new ArrayList<LocationData>();

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
				LocationData location = new LocationData();
				// get the next item.
				SoapObject element = (SoapObject) response.get(i);
				// Unmarshall the element.
				location.setLocationName(element.getPrimitivePropertyAsString("locationName"));
				location.setLatitude(Double.parseDouble(element.getPrimitivePropertyAsString("latitude")));
				location.setLongitude(Double.parseDouble(element.getPrimitivePropertyAsString("longitude")));

				// Unmarshall the weather information;
				ArrayList<Double> weatherInfo = new ArrayList<Double>();

				for (int j = 0; j < 12; j++) {
					SoapPrimitive weatherInformation = (SoapPrimitive) element.getProperty(j + 3);
					Double item = Double.parseDouble(weatherInformation.toString());
					weatherInfo.add(item);
				}
				location.setLocationWeatherData(weatherInfo);

				// Unmarshall the weather information;
				ArrayList<Double> weatherEff = new ArrayList<Double>();
				for (int j = 0; j < 12; j++) {
					SoapPrimitive weatherInformation = (SoapPrimitive) element.getProperty(j + 15);
					Double item = Double.parseDouble(weatherInformation.toString());
					weatherEff.add(item);
				}
				location.setLocationWeatherData(weatherEff);

				locations.add(location);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return locations;

	}
}
