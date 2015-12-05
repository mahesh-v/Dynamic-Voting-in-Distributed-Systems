package sockets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import data.Message;
import data.MyData;


public class Server extends Thread {
	private Socket socket;
	private ObjectOutputStream ooStream;
	private ObjectInputStream oiStream;
	private char nodeConnectedTo;
	
	public Server(Socket socket){
		this.socket = socket;
	}
	
	public void run(){
		readSocketData();
	}
	
	public void sendObject(Object object)
	{
		try {
			Message m = new Message();
			m.setTimeStamp(LocalDateTime.now());
			m.setSenderNodeID(MyData.getMyData().getMyNodeLabel());
			m.setMessage(object);
			if(ooStream != null){
				ooStream.writeObject(m);
				ooStream.flush();
			}
			else {
				System.out.println("Did not send: "+object+" to "+getNodeConnectedTo());
			}
		} catch (IOException e) {
			System.err.println("IOException while sending object("+object+") to "+getNodeConnectedTo()+": "+e.getMessage());
		}
	}

	protected void readSocketData() {
		try {
			if(ooStream == null)
				ooStream = new ObjectOutputStream(socket.getOutputStream());
			if(oiStream == null)
				oiStream = new ObjectInputStream(socket.getInputStream());
			while(!MyData.getMyData().isShutDown())
			{
				try {
					Object object = oiStream.readObject();
					if(object!= null && object instanceof Message)
					{
						Message m =(Message) object;
						Object inObject = m.getMessage();
						if(inObject instanceof String && ((String) inObject).contains("CONNECT_ME"))
							processConnectRequest((String)inObject);
						else
							MyData.getMyData().getMessageQueue().put(m);
					}
				} catch (ClassNotFoundException e) {
					System.err.println("ClassNotFoundException when reading object from stream: "+e.getMessage());
				} catch (InterruptedException e) {
					if(!MyData.getMyData().isShutDown())
						System.err.println("ServerThread connected to node"+nodeConnectedTo+" interrupted: "+e.getMessage());
				}
			}
		} catch (IOException e) {
			System.err.println("Connection closed with "+nodeConnectedTo);
		} 
		finally {
			close();
		}
	}

	public void close() {
		try {
			if(ooStream != null)
				ooStream.close();
			if(oiStream != null)
				oiStream.close();
			if(socket!= null)
				socket.close();
			MyData.getMyData().removeNeighbor(nodeConnectedTo);
		} catch (IOException e) {}
	}
	
	private void processConnectRequest(String data) {
		if(data.contains("CONNECT_ME"))
		{
			String[] fields = data.split("\t");
			if(fields.length != 2)
				System.err.println("Abnormal connect message received: "+data);
			char nodeLabel = fields[1].charAt(0);
			this.setNodeConnectedTo(nodeLabel);
			this.setName("ThreadTo"+nodeLabel);
			MyData.getMyData().getNeighbors().add(this);
			System.out.print("Connection established with node"+nodeLabel+"\n> ");
		}
	}

	public void setNodeConnectedTo(char nodeNum){
		this.nodeConnectedTo = nodeNum;
	}
	
	public char getNodeConnectedTo(){
		return this.nodeConnectedTo;
	}
	
	public String getNodeAddress(){
		return this.socket.getInetAddress().getHostName();
	}
}
