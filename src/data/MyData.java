package data;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;

import sockets.Server;

public class MyData {
	private static final MyData myData = new MyData();
	private boolean shutDown;
	private char myNodeLabel;
	private SynchronousQueue<Message> messageQueue = new SynchronousQueue<Message>();
	private CopyOnWriteArrayList<Server> connectionList = new CopyOnWriteArrayList<>();
	private String networkNodesFile;
	private int portNumber;
	
	private MyData(){}
	
	public static MyData getMyData() {
		return myData;
	}

	public boolean isShutDown() {
		return shutDown;
	}

	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	public SynchronousQueue<Message> getMessageQueue() {
		return messageQueue;
	}

	public char getMyNodeLabel() {
		return myNodeLabel;
	}

	public void setMyNodeLabel(char nodeLabel) {
		this.myNodeLabel = nodeLabel;
	}

	public List<Server> getNeighbors() {
		return Collections.synchronizedList(connectionList);
	}

	public Server getNeighborNodeNum(char nodeLabel) {
		for (Server server : Collections.synchronizedList(connectionList)) {
			if(server.getNodeConnectedTo() == nodeLabel)
				return server;
		}
		return null;
	}

	public void removeNeighbor(char senderID) {
		if(VoteData.getVoteData().getVoteGivenTo() != null && VoteData.getVoteData().getVoteGivenTo() == senderID){
			VoteData.getVoteData().setVoteGivenTo(null);
		}
		synchronized (getNeighbors()) {
			int mark = -1;
			for (int i = getNeighbors().size()-1; i >= 0; i--) {
				if(getNeighbors().get(i).getNodeConnectedTo() == senderID){
					mark = i;
					break;
				}
			}
			if(mark!=-1){
				getNeighbors().remove(mark);
			}
		}
	}

	public String getNetworkNodesFile() {
		return networkNodesFile;
	}

	public void setNetworkNodesFile(String networkNodesFile) {
		this.networkNodesFile = networkNodesFile;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}
