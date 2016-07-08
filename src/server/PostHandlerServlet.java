package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import model.MongoDBHandler;
import utility.Constants;

import java.io.IOException;

public class PostHandlerServlet extends HttpServlet
{
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String content = request.getParameter(Constants.LOG_MSG);
		JSONObject obj = new JSONObject();
		try {
			obj.put(Constants.LOG_MSG, content);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		boolean status = MongoDBHandler.pushToLog(obj);
		//TODO: update commitIndex, etc.
		response.setStatus(HttpServletResponse.SC_OK);
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String htmlContent = "<html><body style=\"background-color:rgb(51, 153, 255);\"><form action='http://"+RaftServer.MY_IP+":8081/syncinc/api/v2/post' method='post'>"
				+ "<div>"
				+ "<label style = color:white; for='message'>Message:</label><textarea rows=2; cols=70; width=10; id='message' name='message'>"
				+ "</textarea></div><br>"
				+ "<div>"
				
				+ "<div class='button'><button color=white; type='submit'>Submit</button></div></form></body></html>";
		response.getWriter().write(htmlContent);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}