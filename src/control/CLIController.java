package control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import sockets.InitiatingServer;
import sockets.Server;
import data.MyData;
import data.VoteData;

public class CLIController {

	public void startControl() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(!MyData.getMyData().isShutDown()){
			System.out.print("> ");
			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				System.err.println("Error while reading line from console. Leaving network...");
				return;
			}
			if(line.toLowerCase().equalsIgnoreCase("quit")||line.toLowerCase().equalsIgnoreCase("exit"))
				MyData.getMyData().setShutDown(true);
			else if(line.toLowerCase().equalsIgnoreCase("neighbors")){
				System.out.println("My neighbors are: ");
				for (Server neighbor : MyData.getMyData().getNeighbors()) {
					System.out.println(neighbor.getNodeConnectedTo()+" at "+neighbor.getNodeAddress());
				}
			}
			else if(line.toLowerCase().startsWith("disconnect_from")){
				System.out.println("Disconnecting...");
				String[] split = line.split(" ");
				ArrayList<Character> nodesToRemove = new ArrayList<>();
				List<Server> neighbors = MyData.getMyData().getNeighbors();
				synchronized (neighbors) {
					for (Server neighbor : neighbors) {
						if(split[1].indexOf(neighbor.getNodeConnectedTo()) != -1){
							System.out.println("Disonnecting from "+neighbor.getNodeConnectedTo());
							nodesToRemove.add(neighbor.getNodeConnectedTo());
						}
					}
				}
				for (Character character : nodesToRemove) {
					Server node = MyData.getMyData().getNeighborNodeNum(character);
					if(node!= null)
						node.close();
				}
			}
			else if(line.toLowerCase().startsWith("connect_to")){
				String[] split = line.split(" ");
				List<String> lines = null;
				try {
					lines = Files.readAllLines(Paths.get(MyData.getMyData().getNetworkNodesFile()));
				} catch (IOException e) {
					System.err.println("Error while reading addresses from the network nodes file: "+e.getMessage());
					continue;
				}
				for (String addressLine : lines) {
					if(split[1].indexOf(addressLine.charAt(0)) != -1){
						String[] values = addressLine.split("\t");
						InitiatingServer is = new InitiatingServer(values[1], values[0].charAt(0), MyData.getMyData().getPortNumber());
						is.connectToServer();
					}
				}
			}
			else if(line.toLowerCase().startsWith("initialize")){
				List<Server> neighbors = MyData.getMyData().getNeighbors();
				for (Server neighbor : neighbors) {
					neighbor.sendObject("initialize");
				}
				VoteManager.initializeVoteData();
			}
			else if(line.toLowerCase().startsWith("display_vote_data")){
				for (Server neighbor : MyData.getMyData().getNeighbors()) {
					neighbor.sendObject("display_vote_data");
				}
				VoteManager.displayVoteData();
			}
			else if(line.toLowerCase().startsWith("request_votes")){
				System.out.println("Sending out requests for votes");
				VoteData.getVoteData().incrementRequestCount();
				VoteData.getVoteData().getVotesReceived().clear();
				VoteData.getVoteData().addToVotesReceived(MyData.getMyData().getMyNodeLabel());
				for (Server neighbor : MyData.getMyData().getNeighbors()) {
					neighbor.sendObject("requesting_votes\t"+MyData.getMyData().getMyNodeLabel()+"_"+VoteData.getVoteData().getRequestCount());
				}
			}
			else if(line.toLowerCase().startsWith("release_votes")){
				for (Server neighbor : MyData.getMyData().getNeighbors()) {
					neighbor.sendObject("releasing_vote");
				}
				VoteData.getVoteData().getVotesReceived().clear();
			}
		}
	}

}
