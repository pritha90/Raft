package model;

import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

import server.RaftServer;
import utility.Constants;

public class Node{
	short role;
	int currentTerm;
	short votedFor;
	int commitIndex;
	short nodeId;
	short clusterLeaderId;
	long nextTimeOut;
	HashMap<Short,LeaderLog> leaderLog = null;
	public Node(short nodeId, short role, short clusterLeaderId, int currentTerm, short votedFor, int commitIndex){
		this.nodeId = nodeId;
		this.role = role;
		this.currentTerm = currentTerm;
		this.votedFor = votedFor;
		this.commitIndex = commitIndex;
		this.clusterLeaderId = clusterLeaderId;
	}	
	public void setRole(short role){
		this.role = role;
	}
	public void setCurrentTerm(int currentTerm){
		//TODO: update the db as well and use locks as well
		this.currentTerm = currentTerm;
	}
	public void setVotedFor(short votedFor){
		this.votedFor = votedFor;
	}
	public void setCommitIndex(int commitIndex){
		this.commitIndex = commitIndex;
	}
	
	public long getNextTimeOut(){
		return this.nextTimeOut;
	}
	//Increment the nextTimeOut randomly
	public void setNextTimeOut(){
		Random r = new Random();
		short max = Constants.ELECTION_TIME_OUT_MAX;
		short min = Constants.ELECTION_TIME_OUT_MIN;
		short n = (short) (max - min + 1);
		short threshold =  (short) (min + (short) r.nextInt() % n);
		//TODO: use locks here
		this.nextTimeOut = this.nextTimeOut + (long) threshold;
		
	}
	
	public void setLeaderLog(HashMap<Short,LeaderLog> leaderLog){
		this.leaderLog = leaderLog;
	}
	public void setNodeId(short id){
		this.nodeId = id;
	}
	public void setClusterLeaderId(short id){
		this.clusterLeaderId = id;
	}
	public short getRole(){
		return this.role;
	}
	public int getCurrentTerm(){
		return this.currentTerm;
	}
	public short getVotedFor(short votedFor){
		return this.votedFor;
	}
	public int getCommitIndex(){
		return this.commitIndex;
	}
	public HashMap<Short,LeaderLog> getLeaderLog(){
		return this.leaderLog;
	}
	public LeaderLog getLeaderLog(short id){
		return this.leaderLog.get(id);
	}
	public short getNodeId(){
		return this.nodeId;
	}
	public short getClusterLeaderId(){
		return this.clusterLeaderId;
	}
	public boolean isLeader(){
		return getRole() == Constants.LEADER;
	}
}