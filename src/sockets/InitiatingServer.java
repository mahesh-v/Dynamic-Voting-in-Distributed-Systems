package sockets;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import data.MyData;


public class InitiatingServer {
	private String destinationAddress;
	private int standardPort;
	private char nodeConnectedTo; 
	
	public InitiatingServer(String destinationAddress, char nodeName, int standardPort) {
		this.nodeConnectedTo = nodeName;
		this.destinationAddress = destinationAddress;
		this.standardPort = standardPort;
	}
	
	public Server connectToServer()
	{
		Socket socket = null;
		try {
			socket = new Socket(destinationAddress, standardPort);
			System.out.print("\nInitiated connection successfully to "+destinationAddress+" at port "+standardPort+"\n> ");
			Server server = new Server(socket);
			server.setNodeConnectedTo(nodeConnectedTo);
			server.setName("ThreadTo"+nodeConnectedTo);
			server.start();
			Thread.sleep(500);//to establish conn
			MyData.getMyData().getNeighbors().add(server);
			server.sendObject("CONNECT_ME\t"+MyData.getMyData().getMyNodeLabel());
			return server;
		} catch (UnknownHostException e) {
			System.err.println("\nUnknownHostException when opening Socket to address, "+destinationAddress+" at port "+standardPort+": "+e.getMessage());
		} catch (IOException e) {
			System.err.println("\nIOException when opening Socket to address, "+destinationAddress+" at port "+standardPort+": "+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
