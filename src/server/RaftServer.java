package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.MongoDBHandler;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import utility.Constants;
import utility.HelperFunctions;
import model.Node;

import com.mongodb.DB;
import com.mongodb.Mongo;


public class RaftServer {
	public static int counter = 0;
	public static Mongo mongo = null;
	public static DB db = null;
	public static String MY_IP = null;
	public static HashMap<Short, String> clusterNodesArray = new HashMap();
	//public static int currentTerm = 1;
	public static Node self = null;
	
	public static void main(String[] args) throws Exception {

		mongo = new Mongo( "localhost" , 27017 );
		db = mongo.getDB("test");
		
		
		clusterNodesArray.put((short)1,"128.111.84.246");
		clusterNodesArray.put((short)2,"128.111.84.168");
		clusterNodesArray.put((short)3,"128.111.84.176");
		//clusterNodesArray.put((short)4,"4.1.1.1");
		
		String config[] = HelperFunctions.readConfigFile("config.txt");
		MY_IP = config[1];
		if(args.length == 0)
		//Node(short nodeId, short role, short clusterLeaderId, int currentTerm, short votedFor, int commitIndex)
			self = new Node(Short.parseShort(config[0]), Constants.FOLLOWER, (short) 1,  1, (short) -1, 0);
		else
			self = new Node(Short.parseShort(config[0]), Constants.LEADER, (short) 1,  1, (short) -1, 0);
		if(self.isLeader()){
			MongoDBHandler.initIndex();
			MongoDBHandler.initialiseLeaderLog();
		}
		Server server = new Server();
		// register the connector
		registerHttpConnector(server);
		//Create servlets to handle GET/POST
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");
		context.addServlet(new ServletHolder(new PostHandlerServlet()),"/syncinc/api/v2/post");
		context.addServlet(new ServletHolder(new RecieverRPCHandler()), "/syncinc/api/v2/sendingRPC");
		server.setHandler(context);
		
		System.out.println("Server Starting. Listening for user requests.");
		Thread postInBackground = new Thread(new SendMessagesBackground());
        postInBackground.start();
		server.start();
		server.join();

	}

	private static void registerHttpConnector(Server server){
		ServerConnector httpConnector = new ServerConnector(server);
		httpConnector.setPort(8081);
		server.addConnector(httpConnector);
	}
}

