package data;
import java.io.Serializable;
import java.time.LocalDateTime;


public class Message implements Serializable {

	/**
	 * Auto-generated serial version UID
	 */
	private static final long serialVersionUID = -2723363051271966964L;
	
	private LocalDateTime timeStamp;
	private char senderNodeID;
	private Object message;
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}
	public char getSenderNodeID() {
		return senderNodeID;
	}
	public void setSenderNodeID(char senderNodeID) {
		this.senderNodeID = senderNodeID;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
}
