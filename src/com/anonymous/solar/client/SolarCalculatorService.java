package com.anonymous.solar.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.kxml2.io.KXmlParser;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;

import com.anonymous.solar.android.MainActivity;
import com.anonymous.solar.shared.SolarResult;
import com.anonymous.solar.shared.SolarResultException;
import com.anonymous.solar.shared.SolarSetup;

/**
 * SOAP Handler class for getting Location Information from the AppEngine Web
 * Service.
 * 
 * @author 07627505 Darran Kartaschew
 * @version 1.0
 */
public class SolarCalculatorService {

	private final static String SOAP_URI = "http://anonymous-solarenergy.appspot.com/SolarCalculatorService";
	private final static String SOAP_NAMESPACE = "http://server.solar.anonymous.com/";

	private final static String SOAP_METHOD = "calculateAllResults";
	private final static String SOAP_ACTION = "http://server.solar.anonymous.com/calculateAllResults";

	/**
	 * Retrieve a list of all locations stored in the web service.
	 * 
	 * @return List of LocationData objects.
	 */
	public SolarResult calculateAllResults(Activity parent, SolarSetup setup) {

		SolarResult result = new SolarResult();
		try {
			result.setSolarSetup(setup);
		} catch (SolarResultException e1) {
			e1.printStackTrace();
			return null;
		}

		// Start SOAP request.
		SoapObject Request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.setOutputSoapObject(Request);
		envelope.encodingStyle = SoapSerializationEnvelope.ENC;

		// Add our solarsetup to the request.
		Serializer serializer = new Persister();
		File xmlFile = new File(parent.getFilesDir().getPath() + "/Request.xml");

		// Serialize the Person

		try {
			serializer.write(setup, xmlFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// read our file to a string...
		String text = null;
		try {
			text = readFile(parent.getFilesDir().getPath() + "/Request.xml");
			text = text + "<arg1>" + ((MainActivity) parent).getReportYears().toString() + "</arg1>\n"; // Add
																										// our
																										// second
																										// argument.
			// System.out.println("--------------------- Simple ------------------------");
			// System.out.println(text);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		/*
		 * As KSOAP2 has a very limited Object parsing engine, we need to rely
		 * on the Simple XML library to build out SOAP body but KSOAP2 mangles
		 * the XML we have, which GAE can't handle. What we need to do is
		 * manually send the SOAP message, and then use KSOAP to process the
		 * return message. (This is very messy, apologies for the code, but
		 * until KSOAP2 marshaling engine gets better handling for complex
		 * objects with Lists, or provides a raw method to set a property, we
		 * need to do as is below.
		 */
		String soapBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		soapBody += "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://server.solar.anonymous.com/\">";
		soapBody += "<soapenv:Header/>\n<soapenv:Body>\n<ser:calculateAllResults>";
		soapBody += text;
		soapBody += "</ser:calculateAllResults>\n</soapenv:Body>\n</soapenv:Envelope>";

		// Attempt to execute the method call.
		try {

			// Create a HTTP Web request.
			URL connectURL = new URL(SOAP_URI);
			HttpURLConnection connection = (HttpURLConnection) connectURL.openConnection();

			// Set the HTTP Header information.
			connection.setRequestProperty("SOAPAction", SOAP_ACTION);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Content-Length", "" + soapBody.getBytes().length);
			connection.setRequestProperty("User-Agent", "kSOAP/2.0");
			connection.setRequestMethod("POST");

			// Send the Web service request.
			OutputStream os = connection.getOutputStream();
			os.write(soapBody.getBytes(), 0, soapBody.getBytes().length);
			os.close();

			soapBody = null;

			InputStream is = connection.getInputStream();

			XmlPullParser xp = new KXmlParser();
			xp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			xp.setInput(is, null);

			// Back to using KSOAP for handling our request.
			envelope.parse(xp);
			// Get our returned objects and count.
			SoapObject response = (SoapObject) envelope.getResponse();
			int responseCount = response.getPropertyCount();
			System.out.println(envelope.bodyIn.toString());
			// Unmarshall the element.
			// <xs:element name="dailySavings" type="xs:double"/>
			// <xs:element name="monthlySavings" type="xs:double"/>
			// <xs:element name="savingsOverYears" type="xs:double"
			// nillable="true" minOccurs="0" maxOccurs="unbounded"/>
			// <xs:element name="solarSetup" type="tns:solarSetup"
			// minOccurs="0"/>
			// <xs:element name="yearlySavings" type="xs:double"/>
			result.setDailySavings(Double.parseDouble(response.getPrimitivePropertyAsString("dailySavings")));
			result.setMonthlySavings(Double.parseDouble(response.getPrimitivePropertyAsString("monthlySavings")));
			result.setYearlySavings(Double.parseDouble(response.getPrimitivePropertyAsString("yearlySavings")));

			// read in the savingOverYears.
			ArrayList<Double> savings = new ArrayList<Double>();
			int years = ((MainActivity) parent).getReportYears();
			for (int i = 0; i < years; i++) {
				SoapPrimitive weatherInformation = (SoapPrimitive) response.getProperty(i + 2);
				Double item = Double.parseDouble(weatherInformation.toString());
				savings.add(item);
			}
			result.setSavingsOverYears(savings);

		} catch (Exception e) {
			System.out.println(envelope.bodyIn.toString());
			e.printStackTrace();
			return null;
		}
		return result;
	}

	/**
	 * Read an entire text file into a string.
	 * 
	 * @param pathname
	 *            The file to opena dn read.
	 * @return A string with the contents
	 * @throws IOException
	 */
	private String readFile(String pathname) throws IOException {

		File file = new File(pathname);
		StringBuilder fileContents = new StringBuilder((int) file.length());
		Scanner scanner = new Scanner(file);
		String lineSeparator = System.getProperty("line.separator");

		try {
			while (scanner.hasNextLine()) {
				fileContents.append(scanner.nextLine() + lineSeparator);
			}
			return fileContents.toString();
		} finally {
			scanner.close();
		}
	}

}
