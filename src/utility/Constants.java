package utility;

public class Constants{
	public static final String RAFT_LOG_NAME = "raftServerLog";
	
	public static final short LEADER = 0;
	public static final short FOLLOWER = 1;
	public static final short CANDIDATE = 2;
	
	public static final short UP = 0;
	public static final short DOWN = 1;
	public static final short UNKNOWN = -1;
	
	public static final short ELECTION_TIME_OUT_MIN = 500;
	public static final short ELECTION_TIME_OUT_MAX = 800;
	public static final short HEARTBEAT_TIME_OUT = 5000;
	
	//prepareRPC
	public static final String SENT_LOG = "log";
	public static final String PREV_INDEX = "prevIndex";
	public static final String PREV_TERM = "prevTerm";
	public static final String LEADER_ID = "leaderId";
	public static final String LEADER_CURRENT_TERM = "leaderCurrentTerm";
	public static final String LEADER_COMMIT_INDEX = "leaderCommitIndex";
	
	//log structure
	public static final String LOG_MSG = "message";
	public static final String LOG_TERM = "term";
	public static final String LOG_INDEX = "index";
	
	public static final String RESPONSE_STATUS = "status";

	public static final String INCREMENT_LOG_INDEX = "incIndex";

	public static final String COUNTERS_TABLE_NAME = "indexInc";

	public static final int HTTP_TIMEOUT = 5000;
}