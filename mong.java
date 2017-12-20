package mongotest;



import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.awt.List;
import java.io.BufferedReader;
import org.apache.commons.codec.binary.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;

public class mong {
	
	
	public static void connection() {
		MongoClient client = new MongoClient("localhost", 27017);
		String connectPoint = client.getConnectPoint();
		System.out.println(connectPoint);
		MongoDatabase db = client.getDatabase("test");
		
		System.out.println(db);
		
	}
	
	private String GetMyCredentials () {
	    String rawUser = "admin";
	    String rawPass = "admin";
	    String rawCred = rawUser+":"+rawPass;
	    String myCred = Base64.encodeBase64String(rawCred.getBytes());
	    return "Basic "+myCred;
	  }
	
	public JsonObject authen() {
		JsonObject myRestData = new JsonObject();
		try{
		      URL myUrl = new URL("http://tao-i134.tao.qanet/redfish/v1");
		      URLConnection urlCon = myUrl.openConnection();
		      urlCon.setRequestProperty("Method", "GET");
		      urlCon.setRequestProperty("Accept", "application/json");
		      urlCon.setConnectTimeout(5000);
		      //set the basic auth of the hashed value of the user to connect
		      urlCon.addRequestProperty("Authorization", GetMyCredentials() );
		      InputStream is = urlCon.getInputStream();
		      InputStreamReader isR = new InputStreamReader(is);
		      BufferedReader reader = new BufferedReader(isR);
		      StringBuffer buffer = new StringBuffer();
		      String line = "";
		      while( (line = reader.readLine()) != null ){
		        buffer.append(line);
		      }
		      reader.close();
		      JsonParser parser = new JsonParser();
		      myRestData = (JsonObject) parser.parse(buffer.toString());
		       
		      return myRestData;
		       
		    }catch( MalformedURLException e ){
		      e.printStackTrace();
		      myRestData.addProperty("error", e.toString());
		      return myRestData;
		    }catch( IOException e ){
		      e.printStackTrace();
		      myRestData.addProperty("error", e.toString());
		      return myRestData;
		    }
	}
	
	public void parser(JsonObject o) throws JSONException {
		
	}
	
	public static void main(String[] args) throws JSONException {
		 connection();
		 mong mon = new mong();
		 JsonObject o = mon.authen();
		System.out.println(o);
		
		
	}
}
