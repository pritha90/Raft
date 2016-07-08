package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import server.RaftServer;
import utility.Constants;

public class MongoDBHandler {

	public static void initialiseNode() {
		// TODO Auto-generated method stub
		
	}

	public static LeaderLog getFollowerNodeInfo(short nodeId) {
		return RaftServer.self.getLeaderLog(nodeId);
	}

	public static JSONArray getFromLog(int followerLastIndex) {
		DBCollection collection = RaftServer.db.getCollection(Constants.RAFT_LOG_NAME);
		BasicDBObject gtQuery = new BasicDBObject();
		gtQuery.put("index", new BasicDBObject("$gt", followerLastIndex -1)); // >= followerLastIndex 
		DBCursor cursorDoc = collection.find(gtQuery).sort(new BasicDBObject("index",1));
	 	JSONArray jsonArray = new JSONArray();
	 	while (cursorDoc.hasNext()) {
	 		jsonArray.put(cursorDoc.next());
		}
		return jsonArray;
	}

	public static int getTermOfIndex(int index) {
		DBCollection collection = RaftServer.db.getCollection(Constants.RAFT_LOG_NAME);
		BasicDBObject eqQuery = new BasicDBObject();
		eqQuery.put("index", new BasicDBObject("$eq", index));  
		DBObject dbObj = collection.findOne(eqQuery);
		if(dbObj != null)
			return (int) dbObj.get("term");
		return -1;
	}
	
	public static boolean pushToLog(JSONObject jsonObj){
		try{
			DBCollection collection = RaftServer.db.getCollection(Constants.RAFT_LOG_NAME);
			jsonObj.put(Constants.LOG_INDEX, getNextLogSequence(Constants.INCREMENT_LOG_INDEX));
			jsonObj.put(Constants.LOG_TERM, RaftServer.self.getCurrentTerm());
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());
			collection.insert(dbObject);
			System.out.println("Successfully added!");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static int  getNextLogSequence(String name) {
		DBCollection collection = RaftServer.db.getCollection(Constants.COUNTERS_TABLE_NAME);
		BasicDBObject find = new BasicDBObject();
	    find.put("_id", name);
	    BasicDBObject update = new BasicDBObject();
	    update.put("$inc", new BasicDBObject("seq", 1));
	    DBObject obj =  collection.findAndModify(find, update);
	    return (Integer) obj.get("seq");
	}
	
	public static void initIndex(){
		try{
			DBCollection collection = RaftServer.db.getCollection(Constants.COUNTERS_TABLE_NAME);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("_id", Constants.INCREMENT_LOG_INDEX);
			jsonObj.put("seq", 1);
			
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(jsonObj.toString());

			collection.insert(dbObject);
			System.out.println("Successfully added!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static long getCountOfLogEntries(){
		DBCollection collection = RaftServer.db.getCollection(Constants.RAFT_LOG_NAME);
		return collection.count();
	}

	public static void consistentlyAppend(JSONArray log, int prevIndex)
	{
		//starting from prevIndex + 1 make the entire log consistent!
		for(int j = 0; j < log.length(); j++)
		{
			//retrieve the local log contents with index prevIndex + j
			//update it with log[j-1]
			
			DBCollection collection = RaftServer.db.getCollection(Constants.RAFT_LOG_NAME);
			BasicDBObject eqQuery = new BasicDBObject();

			System.out.println("Putting into the db");
			System.out.println(prevIndex+j+1);
			eqQuery.put("index", new BasicDBObject("$eq", prevIndex+j+1)); //get the local log contents at prevIndex + j 
			try {
				collection.update(eqQuery, (DBObject) JSON.parse(log.get(j).toString()), true, false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //upsert is true
	
		}
	}

	public static void initialiseLeaderLog() {
		Iterator it = RaftServer.clusterNodesArray.entrySet().iterator();
		HashMap<Short,LeaderLog> leaderLog = new HashMap();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        short nodeId = (short) pair.getKey();
	        String ip = (String) pair.getValue();
	        //LeaderLog(short nodeId, String ip, short status, int lastIndex, boolean voteGranted, int matchIndex)
	        LeaderLog log = new LeaderLog(nodeId, ip, Constants.UP, 0, false, 0);
	        leaderLog.put(nodeId, log);
	    }
	    RaftServer.self.setLeaderLog(leaderLog);
	}
	
}