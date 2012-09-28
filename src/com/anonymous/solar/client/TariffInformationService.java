package com.anonymous.solar.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.anonymous.solar.shared.TariffRate;

/**
 * SOAP Handler class for getting Location Information from the AppEngine Web
 * Service.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class TariffInformationService {

	private final static String SOAP_URI = "http://anonymous-solarenergy.appspot.com/TRateInformationService";
	private final static String SOAP_NAMESPACE = "http://server.solar.anonymous.com/";

	private final static String SOAP_METHOD = "getTariffRates";
	private final static String SOAP_ACTION = "http://server.solar.anonymous.com/getTariffRates";

	/**
	 * Retrieve a list of all locations stored in the web service.
	 * 
	 * @return List of LocationData objects.
	 */
	public List<TariffRate> getTariffRates() {
		ArrayList<TariffRate> tariffs = new ArrayList<TariffRate>();

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
				TariffRate tariff = new TariffRate();
				// get the next item.
				SoapObject element = (SoapObject) response.get(i);
				// Unmarshall the element.
//				  <xs:element name="tariff11Cost" type="xs:double" minOccurs="0"/>
//			      <xs:element name="tariff11Fee" type="xs:double" minOccurs="0"/>
//			      <xs:element name="tariff33Cost" type="xs:double" minOccurs="0"/>
//			      <xs:element name="tariff33Fee" type="xs:double" minOccurs="0"/>
//			      <xs:element name="tariffFeedIn" type="xs:double" minOccurs="0"/>
//			      <xs:element name="tariffProvider" type="xs:string" minOccurs="0"/>
//			      <xs:element name="tariffState" type="xs:string" minOccurs="0"/>
				
				tariff.setTariffProvider(element.getPrimitivePropertyAsString("tariffProvider"));
				tariff.setTariffState(element.getPrimitivePropertyAsString("tariffState"));
				tariff.setTariff11Cost(Double.parseDouble(element.getPrimitivePropertyAsString("tariff11Cost")));
				tariff.setTariff11Fee(Double.parseDouble(element.getPrimitivePropertyAsString("tariff11Fee")));
				tariff.setTariff33Cost(Double.parseDouble(element.getPrimitivePropertyAsString("tariff33Cost")));
				tariff.setTariff33Fee(Double.parseDouble(element.getPrimitivePropertyAsString("tariff33Fee")));
				tariff.setTariffFeedInFee(Double.parseDouble(element.getPrimitivePropertyAsString("tariffFeedIn")));

				tariffs.add(tariff);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return tariffs;

	}
}
