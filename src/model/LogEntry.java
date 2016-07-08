package model;

public class LogEntry{
	String message;
	int term;
	int index;
	
	LogEntry(){}
	
	LogEntry(String message, int term, int index){
		this.message = message;
		this.term = term;
		this.index = index;
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public int getTerm(){
		return this.term;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setTerm(int term){
		this.term = term;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
}