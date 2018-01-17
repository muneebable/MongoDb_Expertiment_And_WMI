package mongotest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import org.apache.commons.codec.binary.*;
import org.json.JSONException;
import org.bson.Document;
import static com.mongodb.client.model.Projections.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import static com.mongodb.client.model.Filters.*;

public class mong {
	
	/*
	 * This method made a connection to MongoDB and get the collection which have the data stored in it.
	 */
	public static MongoCollection<Document> connection() throws IOException {
		@SuppressWarnings("resource")
		MongoClient client = new MongoClient("localhost", 27017);
		String connectPoint = client.getConnectPoint();
		System.out.println(connectPoint);
		MongoDatabase db = client.getDatabase("test");
		MongoCollection<Document> collection = db.getCollection("collection1");
		System.out.println(db);
		
		/* This chunk of code will help to read & parse the JSON file
		 * 
		String File = "file3.json";
		BufferedReader reader = new BufferedReader(new FileReader(File));
		try {
			String json;
			while ((json = reader.readLine()) != null) {
		        collection.insertOne(Document.parse(json));
		    } 
		}finally {
			reader.close();}*/
		
		return collection;
	}
	
	 /* Credentials of the Server
	 */
	private String GetMyCredentials () {
	    String rawUser = "admin";
	    String rawPass = "admin";
	    String rawCred = rawUser+":"+rawPass;
	    String myCred = Base64.encodeBase64String(rawCred.getBytes());
	    return "Basic "+myCred;
	  }
	
	/*
	 * Get the JSONObject from the server through REDFISH. 
	 */
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
	
	/*
	 * Saving the Data in a file
	 */
	public void writer(JsonObject o) throws JSONException, IOException {
		try (FileWriter file = new FileWriter("file1.json")) {
			file.write(o.toString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + o);
		}
		
	}
	/*
	 * Export() function will save all the documents in the collection to JSON File
	 */

	
	public static void Export(MongoCollection<Document> coll) throws IOException {
		
		Runtime.getRuntime().exec("C:\\\\Program Files\\\\MongoDB\\\\Server\\\\3.6\\\\bin\\\\mongoexport.exe --host localhost --port 27017 --db test --collection collection1 --out file1.json");
	}
	
	/*
	 *This function retreive all data from the Collection
	 */
	public static void RetreiveAllData(MongoCollection<Document> coll) {
		MongoCursor<Document> cursor = coll.find().iterator();
		try {
		    while (cursor.hasNext()) {
		        System.out.println(cursor.next().toJson());
		    }
		} finally {
		    cursor.close();
		}
	}
	
	/*
	 This projection bring the specific data from the table.Here this projection is equal to
	 	select Chassis from collection where ID = 10.172.8.37
	 	provide the ID, you will get the value.
	 */
	@SuppressWarnings("unchecked")
	public static void FindByIP(MongoCollection<Document> coll) {
		
		
		@SuppressWarnings("rawtypes")
		FindIterable it = coll.find(eq("Id", "10.172.8.37")).projection(fields(include("Chassis"), excludeId()));
		 @SuppressWarnings("rawtypes")
		ArrayList<Document> docs = new ArrayList();
		 it.into(docs);
		 for (Document doc : docs) {
	            System.out.println(doc);
	        } 
	}
	
	/*
	 This function Modifies one of the field 
	 */
	
	public static void Update(MongoCollection<Document> coll) {
		//coll.deleteOne(eq());
		coll.deleteOne(eq("RedfishCopyright","Copyright 2014-2017 Distributed Management Task Force, Inc (DMTF) For the full DMTF copyright policy, see http://wwwdmtforg/about/policies/copyright"));
        coll.updateOne(new Document("RedfishCopyright", "Copyright 2014-2017 Distributed Management Task Force, Inc (DMTF) For the full DMTF copyright policy, see http://wwwdmtforg/about/policies/copyright"),  
                new Document("$set", new Document("RedfishCopyright", "Copyright 2014-2017")));
        
        System.out.println("update Successfully");
	}
	
	
	public static void main(String[] args) throws JSONException, IOException {
		 
		 mong mon = new mong();
		 JsonObject o = mon.authen();
		 mon.writer(o);
		 connection();
		 RetreiveAllData(connection());
		 Update(connection()); 
		 Export(connection());
		 FindByIP(connection());
		
	}
}
