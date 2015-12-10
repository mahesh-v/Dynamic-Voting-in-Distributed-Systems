package control;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import sockets.AcceptingServer;
import sockets.InitiatingServer;
import sockets.Server;
import data.MyData;

//test scontrol
public class Controller {
	
	private static final int PORT_NUMBER = 5030;
	private static final String NETWORK_NODES_FILE = "NetworkNodes.txt";

	public static void main(String[] args) throws UnknownHostException {
		char nodeLabel = args.length>0?args[0].toUpperCase().charAt(0):'A';
		String localAddress = InetAddress.getLocalHost().getHostName();
		MyData.getMyData().setShutDown(false);
		MyData.getMyData().setMyNodeLabel(nodeLabel);
		MyData.getMyData().setNetworkNodesFile(NETWORK_NODES_FILE);
		MyData.getMyData().setPortNumber(PORT_NUMBER);
		MessageProcessor mp = new MessageProcessor();
		mp.start();
		AcceptingServer as = new AcceptingServer(PORT_NUMBER);
		as.start();
		connectToAllNodes();
		addEntryToFile(nodeLabel, localAddress);
		CLIController cli = new CLIController();
		cli.startControl();
		
		closeNeighborConnections();
		as.close();
		removeEntryFromNetworkFile(nodeLabel);
		MyData.getMyData().setShutDown(true);
		displayReport();
	}

	private static void removeEntryFromNetworkFile(char nodeLabel) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(NETWORK_NODES_FILE));
			List<String> new_lines = new ArrayList<String>();
			for (String line : lines) {
				if(!line.contains(nodeLabel+"\t"))
					new_lines.add(line);
			}
			Files.write(Paths.get(NETWORK_NODES_FILE), new_lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addEntryToFile(char nodeName, String localAddress) {
		String content = nodeName+"\t"+localAddress;
		List<String> lines = new ArrayList<String>();
		lines.add(content);
		try {
			Files.write(Paths.get(NETWORK_NODES_FILE), lines, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void connectToAllNodes() {
		if(Files.notExists(Paths.get(NETWORK_NODES_FILE)))
			return;
		List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(NETWORK_NODES_FILE));
			if(lines.size() == 0)
				return;
			for (String line : lines) {
				String[] split = line.split("\t");
				InitiatingServer is = new InitiatingServer(split[1], split[0].charAt(0), PORT_NUMBER);
				is.connectToServer();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void closeNeighborConnections() {
		List<Server> list = MyData.getMyData().getNeighbors();
		Iterator<Server> i = list.iterator();
		List<Server> connectionsToClose = new ArrayList<Server>();
		while(i.hasNext()){
			connectionsToClose.add(i.next());
		}
		for (Server server : connectionsToClose) {
			server.close();
		}
	}
	
	private static void displayReport()
	{
		
	}
}
