package mongotest;



import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import org.apache.commons.codec.binary.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.bson.Document;
import com.mongodb.DBObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import com.mongodb.client.model.*;
import com.mongodb.util.JSON;


public class mong {
	
	
	public static void connection() throws FileNotFoundException, IOException {
		MongoClient client = new MongoClient("localhost", 27017);
		String connectPoint = client.getConnectPoint();
		System.out.println(connectPoint);
		MongoDatabase db = client.getDatabase("test");
		MongoCollection<Document> collection = db.getCollection("collection1");
		System.out.println(db);
		
		String File = "file3.json";
		BufferedReader reader = new BufferedReader(new FileReader(File));
		try {
			String json;
			while ((json = reader.readLine()) != null) {
		        collection.insertOne(Document.parse(json));
		    } 
		}finally {
			reader.close();
	}
		
		/*int count =0;
		int batch =100;
	
		List<InsertOneModel<Document>> docs = new ArrayList<>();
		try(BufferedReader br = new BufferedReader (new FileReader("file1.json"))){
			String line ;
			while((line = br.readLine())!=null) {
				docs.add(new InsertOneModel<>(Document.parse(line)));
				count++;
				if(count==batch) {
					collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
					docs.clear();
					count=0;
				}
				
			}
					
		
	}
		if(count>0) {
			collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
		}*/
	}
	
	/*public void parser(JsonObject o) throws JSONException {
		JsonElement UUID = o.get("UUID");
		
		System.out.println(UUID);
	}*/
	
	
	
	public void SystemInfo(JSONObject o) {
		
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
	
	public void writer(JsonObject o) throws JSONException, IOException {
		try (FileWriter file = new FileWriter("file1.json")) {
			file.write(o.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + o);
		}
		
		
	}	
	
	public static void main(String[] args) throws JSONException, IOException {
		 
		 mong mon = new mong();
		 JsonObject o = mon.authen();
		 mon.writer(o);
		 // mon.parser(o);
		 connection();
		//System.out.println(o);
		
		
		
	}
}
