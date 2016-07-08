package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;

import server.RaftServer;
import utility.Constants;


public class LogRPC{
	public static JSONObject prepareLogForRPC(short nodeId){
		LeaderLog followerInfo = MongoDBHandler.getFollowerNodeInfo(nodeId);
		int followerLastIndex = followerInfo.getLastIndex();
		long leaderLastIndex= MongoDBHandler.getCountOfLogEntries();
		JSONObject resultObj = new JSONObject();
		if(followerInfo.getStatus() == Constants.UP &&  leaderLastIndex >= followerLastIndex){
			JSONArray log = MongoDBHandler.getFromLog(followerLastIndex);
			try {
				if(log.length() == 0)
					resultObj.put("forServerOnly", 0);
				else{
					DBObject logSentLastLog = (DBObject)(log.get(log.length() -1));
					int logSentLastIndex = (Integer)logSentLastLog.get(Constants.LOG_INDEX);
					resultObj.put("forServerOnly", logSentLastIndex);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				resultObj.put(Constants.SENT_LOG, log);
				int prevTerm = MongoDBHandler.getTermOfIndex(followerLastIndex -1);
				resultObj.put(Constants.PREV_TERM, prevTerm);				
				if(followerLastIndex != 0)
					resultObj.put(Constants.PREV_INDEX, followerLastIndex - 1);
				else
					resultObj.put(Constants.PREV_INDEX, followerLastIndex);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else
			try {
				resultObj.put("forServerOnly", 0);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		//leader id and term are always send - for heart beat and rpc
		try {
			resultObj.put(Constants.LEADER_ID, RaftServer.self.getNodeId());
			resultObj.put(Constants.LEADER_CURRENT_TERM, RaftServer.self.getCurrentTerm());
			resultObj.put(Constants.LEADER_COMMIT_INDEX, RaftServer.self.getCommitIndex());		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return resultObj;
	}

	public static AppendRPCResponse processAppendRPC(int term, short leaderId, int prevIndex, int prevLogTerm, JSONArray log, int commitIndex){
		AppendRPCResponse response = new AppendRPCResponse();
		
		//if term proposed is lesser than your currentTerm abort
		if (term < RaftServer.self.getCurrentTerm())
		{
			response.setTerm(RaftServer.self.getCurrentTerm());
			response.setSuccess(false);
			System.out.println("Aborting due to lower currentTerm of leader");
			return response;
		}
		
		//set the currentTerm to term, update the db as well
		//TODO: do we update the currentTerm here? I think one changes the currentTerm only during elections
		
		//RaftServer.self.setCurrentTerm(term);
		
		//reset_timer with a random delay
		RaftServer.self.setNextTimeOut();
	
		if(RaftServer.self.getClusterLeaderId()!= leaderId) //We have a new leader
		{
			RaftServer.self.setClusterLeaderId(leaderId);
			System.out.println("We have a new leader"+RaftServer.self.getClusterLeaderId());
		}
		else
		{
			System.out.println("The old leader is up"+RaftServer.self.getClusterLeaderId());
		}
		if(log == null)
		{
			//Its a hearbeat
			response.setTerm(RaftServer.self.currentTerm);
			response.setSuccess(true);
			System.out.println("Its a heartbeat");
			return response;			
		}
		//There is something to add to the log
		else
		{
			if(MongoDBHandler.getTermOfIndex(prevIndex)!=prevLogTerm)
			{
				response.setTerm(RaftServer.self.getCurrentTerm());
				response.setSuccess(false);
				System.out.println("Aborting as prevIndexTerms dont match");
				return response;
			}
			else
			{
				MongoDBHandler.consistentlyAppend(log,prevIndex);
				RaftServer.self.setCommitIndex(commitIndex);
				response.setTerm(RaftServer.self.getCurrentTerm());
				response.setSuccess(true);
				System.out.println("This was a success!");
				return response;
			}
		}
	}
}
