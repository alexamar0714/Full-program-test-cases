package com.motour.recommender;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class XmlParserForGov {

String InputContentID;
static Scanner sc = new Scanner(System.in);
String serviceKey = "dZ0pzhl5bsDfgAXjmHS0%2BQVpl0Vlcl%2BUKtEeQV%2FRGV1Oj50vgfSscxifcrf7x2OhJP4gQGO8zOt3z4U52OEbzg%3D%3D";
int index;


	public String returnCat2(String ContentID) throws Exception {
		String cat2 = null;
		//여행지 이름이 필요할경우 사용
//		String title = null;
		URL url = new URL(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=" + serviceKey +"&contentId=" + ContentID + "&defaultYN=Y&MobileOS=ETC&MobileApp=AppTesting&catcodeYN=Y&cat2=");
				
		URLConnection connection = url.openConnection();
		
		Document doc = parseXML(connection.getInputStream());
		NodeList descNodes = doc.getElementsByTagName("item");
		
		for (int i = 0; i < descNodes.getLength(); i++) {
			for (Node node = descNodes.item(i).getFirstChild(); node != null; node = node.getNextSibling()) {
				if(node.getNodeName().equals("contentid")){
					ContentID = node.getTextContent();
				}
				if(node.getNodeName().equals("cat2")){
					cat2 = node.getTextContent();
				}
				//여행지 이름이 필요할 경우 사용
//				if(node.getNodeName().equals("title")){
//					title = node.getTextContent();
//				
//					}				
				}
			}
		return cat2;
	}


	public Document parseXML(InputStream stream) throws Exception {
		DocumentBuilderFactory objDocumentBuilderFactory = null;
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try {
			objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
			doc = objDocumentBuilder.parse(stream);
		} catch (Exception ex) {
			throw ex;
		}
		return doc;
	}

	public String XmlReturn() throws IOException {
		StringBuilder urlBuilder = new StringBuilder(
				"http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon?ServiceKey=dZ0pzhl5bsDfgAXjmHS0%2BQVpl0Vlcl%2BUKtEeQV%2FRGV1Oj50vgfSscxifcrf7x2OhJP4gQGO8zOt3z4U52OEbzg%3D%3D&contentTypeId=12&contentId=127480&MobileOS=ETC&MobileApp=TourAPI3.0_Guide&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&transGuideYN=Y"); /* URL */
		urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8")
				+ "=dZ0pzhl5bsDfgAXjmHS0%2BQVpl0Vlcl%2BUKtEeQV%2FRGV1Oj50vgfSscxifcrf7x2OhJP4gQGO8zOt3z4U52OEbzg%3D%3D"); 
		urlBuilder.append("&" + URLEncoder.encode("TourOfAll", "UTF-8") + "="
				+ URLEncoder.encode("HSU capstone-degisn", "UTF-8")); 
		System.out.println(urlBuilder);
		URL url = new URL(urlBuilder.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();
		conn.disconnect();
		System.out.println(sb.toString());

		String ParsingXML = sb.toString();
		return ParsingXML;
	}
}