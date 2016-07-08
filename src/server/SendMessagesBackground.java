package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import model.LeaderLog;
import model.LogRPC;
import model.LogRPCResponse;
import utility.Constants;

public class SendMessagesBackground implements Runnable{
	
	public SendMessagesBackground(){
    }

    @Override
    public void run() {
		while(true){
			try {
				Thread.sleep(Constants.HEARTBEAT_TIME_OUT);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(RaftServer.self.isLeader()){
				Iterator it = RaftServer.clusterNodesArray.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        short nodeId = (short) pair.getKey();
			        String ip = (String) pair.getValue();
			        System.out.println(ip);
			        if(nodeId != RaftServer.self.getNodeId()){
				        JSONObject obj = LogRPC.prepareLogForRPC(nodeId);
				        try {
							sendPost(obj, ip, nodeId);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			    }
			}
		}
    }
    
    private boolean sendPost(JSONObject sendObj, String ip, short nodeId) throws Exception {
    	System.out.println(sendObj.toString());
		String url = "http://" + ip + ":8081/syncinc/api/v2/sendingRPC";
		System.out.println(url);
		URL obj = new URL(url);
		
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		con.setConnectTimeout(Constants.HTTP_TIMEOUT); //set timeout to 5 seconds
		// Send post request
		con.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
		wr.write(sendObj.toString());
		wr.flush();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) {
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		//print result
		System.out.println(response.toString());
		//TODO: update lastIndx && do more up
		JSONObject respObj = new JSONObject(response.toString());
		int lastReplicatedIndex = sendObj.getInt("forServerOnly");
		if(respObj.getBoolean(Constants.RESPONSE_STATUS)){
			System.out.println(sendObj.toString());
			if(lastReplicatedIndex != 0) // not HB
				LogRPCResponse.handleSuccessResponse(nodeId, lastReplicatedIndex);
		}
		else{
			if(lastReplicatedIndex != 0 && respObj.getInt(Constants.LOG_TERM) <= RaftServer.self.getCurrentTerm()) // not HB
			{
				//Find the object from leaderLog corresponding to nodeId
				LeaderLog record = RaftServer.self.getLeaderLog().get(nodeId);
				record.setLastIndex(lastReplicatedIndex -1);
				RaftServer.self.getLeaderLog().remove(nodeId);
				RaftServer.self.getLeaderLog().put(nodeId, record);
			}
		}
		return true;
		}
		return false;
	}

}
