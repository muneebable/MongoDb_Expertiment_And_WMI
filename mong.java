package mongotest;



import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
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
import static com.mongodb.client.model.Projections.*;
//import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import com.mongodb.client.model.*;
import com.mongodb.util.JSON;
import static com.mongodb.client.model.Filters.*;

public class mong {
	
	/*
	 * This method made a connection to MongoDB and get the collection which have the data stored in it.
	 */
	public static MongoCollection<Document> connection()/* throws FileNotFoundException, IOException */{
		MongoClient client = new MongoClient("localhost", 27017);
		String connectPoint = client.getConnectPoint();
		System.out.println(connectPoint);
		MongoDatabase db = client.getDatabase("test");
		MongoCollection<Document> collection = db.getCollection("collection1");
		System.out.println(db);
		
		
		
		/*String File = "file3.json";
		BufferedReader reader = new BufferedReader(new FileReader(File));
		try {
			String json;
			while ((json = reader.readLine()) != null) {
		        collection.insertOne(Document.parse(json));
		    } 
		}finally {
			reader.close();
	}*/
		return collection;
		
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
	
	/*
	 * Credentials of the Server
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
	 * Getting all the data from the collection
	 */
	
	public static void qopera(MongoCollection<Document> coll) {
		BasicDBObject query = new BasicDBObject("Id", 
                new BasicDBObject("$eq", "RootService"));
		
		coll.find(query).forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        });    
	}
	
	public static void query(MongoCollection<Document> coll) {
		
		try(MongoCursor<Document> cur = coll.find().iterator()){
			while(cur.hasNext()) {
				Document doc = cur.next();
				List list = new ArrayList(doc.values());
                System.out.print(list.get(1));
                System.out.print(": ");
                System.out.println(list.get(2));
			}
		}
		System.out.println("get iit");
	}
	
	/*
	 This projection bring the specific data from the table.Here this projection is equal to
	 	select id,odataetag from collection.
	 	provide the ID, you will get the value.
	 */
	public static void projection(MongoCollection<Document> coll) {
		
		FindIterable it = coll.find().projection(include("odataetag","Id"));
		 ArrayList<Document> docs = new ArrayList();
		 it.into(docs);
		 for (Document doc : docs) {
	            System.out.println(doc);
	        }   
	}
	
	/*
	 This function Modifies one of the field 
	 */
	
	public static void modify(MongoCollection<Document> coll) {
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
		  //mon.parser(o);
		 connection();
		 //modify(connection());
		 qopera(connection());
		 //projection(connection());
		 
		//System.out.println(o);
		
		
		
	}
}
