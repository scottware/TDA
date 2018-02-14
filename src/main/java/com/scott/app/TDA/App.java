package com.scott.app.TDA;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class App {
	public static void main(String[] args) {
		App app = new App();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String user = null;
		String password = null;
		String sourceId = null;
		try {
			System.out.print("user: ");
			user = in.readLine();
			System.out.print("password: ");
			password = in.readLine();
			System.out.print("source id: ");
			sourceId = in.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			app.login(user, password, sourceId);
			app.getChain(sourceId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean DEBUG = false;

	public static void login(String AMTDUserName, String AMTDPassword, String AMTDsourceID)
			throws IOException, ParserConfigurationException, SAXException {

		LinkedHashMap<String, String> ohm = new LinkedHashMap<String, String>();
		ohm.put("userid", AMTDUserName);
		ohm.put("password", AMTDPassword);
		ohm.put("source", AMTDsourceID); // F3
		ohm.put("version", "1001");
		String url = "https://apis.tdameritrade.com/apps/100/LogIn";

		String res = URLUtil.sendURLPostRequest(url, ohm);
		// File res = new File("login_output.xml");

		System.out.println(res);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new InputSource(new StringReader(res)));
		doc.getDocumentElement().normalize();

		SessionControl.setSessionid(doc.getElementsByTagName("session-id").item(0).getTextContent());
		SessionControl.setCompany(doc.getElementsByTagName("company").item(0).getTextContent());
		SessionControl.setSegment(doc.getElementsByTagName("segment").item(0).getTextContent());

	}

	public static void getChain(String AMTDsourceID) throws IOException, ParserConfigurationException, SAXException {
		String chainUrl = "https://apis.tdameritrade.com/apps/200/OptionChain;jsessionid="
				+ SessionControl.getSessionid(DEBUG);
		chainUrl += "?source=" + AMTDsourceID + "&type=P&quotes=true&symbol=AMZN";

		// File chain = new File("option_chain_output.xml");
		String chain = URLUtil.getfromURL(chainUrl);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

		Document doc = dBuilder.parse(new InputSource(new StringReader(chain)));
		// Document doc = dBuilder.parse(chain);

		doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("description");
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			String desc = nNode.getTextContent();
			// System.out.println(desc);
		}
		System.out.println(chain);
		System.out.println("Count: " + nList.getLength());

	}
}
