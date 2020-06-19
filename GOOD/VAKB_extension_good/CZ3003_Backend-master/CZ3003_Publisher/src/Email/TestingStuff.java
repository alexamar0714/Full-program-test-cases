package Email;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestingStuff {
	public static HashMap<Integer, ArrayList<MessageToSend>> messagesToSend = new HashMap<Integer, ArrayList<MessageToSend>>();
	public static JSONObject jsonObjectOverall;
	public static JSONObject jsonObject;
	public static JSONArray jsonArrayOfMails;
	
	public static void formTrialJsonObject() throws IOException{
		for (int i=1; i<=3; i++){
			messagesToSend.put(i, new ArrayList<MessageToSend>());
		}
		
		jsonArrayOfMails = new JSONArray();
		
		MessageToSend m = new MessageToSend("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6e0d145d5e5e5d1a07030b0d1c071d071d2e09030f0702400d0103">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="0e6f657c677a677867643f364e69636f6762206d6163">[email protected]</a>", "Subject", "Body text", 3);
		messagesToSend.get(3).add(m);
		m = new MessageToSend("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="56352c65666665223f3b3335243f253f2516313b373f3a7835393b">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bddcd6cfd4c9d4cbd4d78c85fddad0dcd4d193ded2d0">[email protected]</a>", "Subject", "Body text", 1);
		messagesToSend.get(1).add(m);
		m = new MessageToSend("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="50332a6360606324393d3533223923392310373d31393c7e333f3d">[email protected]</a>", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="432228312a372a352a29727b03242e222a2f6d202c2e">[email protected]</a>", "Subject", "Body text", 1);
		messagesToSend.get(1).add(m);
		
		jsonObject = new JSONObject();
		jsonObject.put("from",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="17746d24272724637e7a7274657e647e6457707a767e7b3974787a">[email protected]</a>");
		jsonObject.put("recipients",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4b2a2039223f223d22217a730b2c262a222765282426">[email protected]</a>");
		jsonObject.put("subject",  "Subject ");
		jsonObject.put("messageText",  "body here");
		jsonObject.put("priority",  3);
		
		jsonArrayOfMails.add(jsonObject);
	
		
		jsonObject = new JSONObject();
		jsonObject.put("from",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="82e1f8b1b2b2b1f6ebefe7e1f0ebf1ebf1c2e5efe3ebeeace1edef">[email protected]</a>");
		jsonObject.put("recipients",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4c2d273e2538253a25267d740c2b212d2520622f2321">[email protected]</a>");
		jsonObject.put("subject",  "Subject ");
		jsonObject.put("messageText",  "body here");
		jsonObject.put("priority",  3);
		
		jsonArrayOfMails.add(jsonObject);
		
		jsonObject = new JSONObject();
		jsonObject.put("from",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9ffce5acafafacebf6f2fafcedf6ecf6ecdff8f2fef6f3b1fcf0f2">[email protected]</a>");
		jsonObject.put("recipients",  "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a8c9c3dac1dcc1dec1c29990e8cfc5c9c1c486cbc7c5">[email protected]</a>");
		jsonObject.put("subject",  "Subject ");
		jsonObject.put("messageText",  "body here");
		jsonObject.put("priority",  1);
		
		jsonArrayOfMails.add(jsonObject);
		
		jsonObject= new JSONObject();
		jsonObject.put("messagesToBeSent", jsonArrayOfMails);
		
		System.out.println(jsonArrayOfMails.toJSONString());
		System.out.println(jsonObject.toJSONString());
		
		FileWriter file = new FileWriter("trialJson.txt");
        try {
            file.write(jsonObject.toJSONString());
            System.out.println("Successfully Copied JSON Object to File...");
            
 
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            file.flush();
            file.close();
        }
		
	}
}
