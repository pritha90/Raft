package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.AppendRPCResponse;
import model.LogRPC;
import model.MongoDBHandler;
import utility.Constants;
import utility.HelperFunctions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RecieverRPCHandler extends HttpServlet
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("RecieverRPCHandler");
		StringBuffer jb = new StringBuffer();
		AppendRPCResponse responseData = null;
		  String line = null;
		  try {
		    BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		      jb.append(line);
		  } catch (Exception e) { /*report an error*/ }
	    System.out.println(jb.toString());
	    try {
			JSONObject obj = new JSONObject(jb.toString());
			//processAppendRPC(int term, short leaderId, int prevIndex, int prevLogTerm, JSONArray log, int commitIndex)
			JSONArray log;int term; short leaderId; int prevIndex; int prevTerm; int commitIndex;

			try{log = obj.getJSONArray(Constants.SENT_LOG);}
			catch (JSONException e){log=null;}
			
			//Are going to be sent
			try{term = obj.getInt(Constants.LEADER_CURRENT_TERM);}
			catch (JSONException e){term = 0;}
			
			try{leaderId = (short) obj.getInt(Constants.LEADER_ID);}
			catch (JSONException e){leaderId = 0;}
			
			try{prevIndex = obj.getInt(Constants.PREV_INDEX);}
			catch (JSONException e){prevIndex = 0;}
			
			try{prevTerm = obj.getInt(Constants.PREV_TERM);}
			catch (JSONException e){prevTerm = -1;}
			
			try{commitIndex = obj.getInt(Constants.LEADER_COMMIT_INDEX);}
			catch (JSONException e){commitIndex = 0;}
			
			responseData = LogRPC.processAppendRPC(term,
					leaderId,
					prevIndex,
					prevTerm,
					log,
					commitIndex
					);
			System.out.println("Response sent is");
			System.out.println(responseData.getSuccess());
			System.out.println(responseData.getTerm());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if(responseData != null){
	    	JSONObject obj = new JSONObject();
	    	try {
				obj.put(Constants.RESPONSE_STATUS, responseData.getSuccess());
				obj.put(Constants.LOG_TERM, responseData.getTerm());
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	
			PrintWriter out = response.getWriter();
			out.print(obj);
		}
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
    }
	
}