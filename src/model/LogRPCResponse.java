package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import server.RaftServer;
import utility.Constants;

public class LogRPCResponse {

	// Processing of all responses
	public static void handleSuccessResponse(short nodeId, int lastReplicatedIndex)
	{
		//Find the object from leaderLog corresponding to nodeId
		LeaderLog record = RaftServer.self.leaderLog.get(nodeId);
		//update matchIndex and lastIndex
		record.setMatchIndex(lastReplicatedIndex);
		record.setLastIndex(lastReplicatedIndex+1);
		RaftServer.self.leaderLog.remove(nodeId);
		RaftServer.self.leaderLog.put(nodeId, record);
		checkCommitIndex();
	}
	
	@SuppressWarnings("null")
	public static void checkCommitIndex()
	{
		ArrayList<Integer> matchIndexes = new ArrayList();
		Iterator it = RaftServer.self.getLeaderLog().entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        short nodeId = (short) pair.getKey();
	        LeaderLog leaderLog = (LeaderLog) pair.getValue();
	        matchIndexes.add(leaderLog.getMatchIndex());
	    }
		
		Collections.sort(matchIndexes);
		
		//get the index at n+1/2 of the sorted version
		int candidateCommitIndex = matchIndexes.get((RaftServer.clusterNodesArray.size()+1)/2);
		int candidateCommitIndexTerm = MongoDBHandler.getTermOfIndex(candidateCommitIndex);
		
		//check if the term of the candidateCommitIndex is equal to the currentTerm then update commitIndex
		if(candidateCommitIndexTerm == RaftServer.self.getCurrentTerm())
		{
			RaftServer.self.setCommitIndex(candidateCommitIndex);
		}
		
	}	
}
