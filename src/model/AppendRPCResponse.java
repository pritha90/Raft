package model;

public class AppendRPCResponse {

	int Term;
	boolean success;
	
	AppendRPCResponse(){}
	
	AppendRPCResponse(int Term, boolean success){
		this.Term = Term;
		this.success = success;
	}
	
	public int getTerm(){
		return this.Term;
	}
	
	public boolean getSuccess(){
		return this.success;
	}
	
	public void setTerm(int term){
		this.Term = term;
	}
	
	public void setSuccess(boolean success){
		 this.success = success;
	}
	
}
