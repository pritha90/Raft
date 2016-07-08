package model;

public class LeaderLog{
	short nodeId;
	short status;// up/down
	int lastIndex;
	boolean voteGranted;
	int matchIndex;
	String nodeIP;
	LeaderLog(){}
	
	LeaderLog(short nodeId, String ip, short status, int lastIndex, boolean voteGranted, int matchIndex){
		this.nodeId = nodeId;
		this.status = status;
		this.lastIndex = lastIndex;
		this.voteGranted = voteGranted;
		this.matchIndex = matchIndex;
		this.nodeIP = ip;
	}
	
	public short getStatus(){
		return this.status;
	}
	
	public int getMatchIndex()
	{
		return this.matchIndex;
	}
	
	public void setMatchIndex(int matchIndex)
	{
		this.matchIndex = matchIndex;
	}
		
	public void setLastIndex(int lastIndex)
	{
		this.lastIndex = lastIndex;
	}
	
	public int getLastIndex(){
		return this.lastIndex;
	}
}