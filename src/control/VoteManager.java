package control;

import java.util.List;

import sockets.Server;
import data.MyData;
import data.VoteData;

public class VoteManager {
	
	public static void initializeVoteData(){
		List<Server> neighbors = MyData.getMyData().getNeighbors();
		VoteData.getVoteData().setVN(1);
		VoteData.getVoteData().setRU(neighbors.size()+1);
		updateDistinguishedSite(neighbors);
		displayVoteData();
	}

	public static void displayVoteData() {
		System.out.println("VN = "+VoteData.getVoteData().getVN());
		System.out.println("RU = "+VoteData.getVoteData().getRU());
		System.out.println("DS = "+VoteData.getVoteData().getDS());
	}

	public static void updateXIfAllVotesReceived() {
		int num_of_votes_rcvd = VoteData.getVoteData().getVotesReceived().size();
		if(num_of_votes_rcvd != (MyData.getMyData().getNeighbors().size()+1))
			return;
		int ru = VoteData.getVoteData().getRU();
		if((num_of_votes_rcvd > ru/2)||
			(ru%2 == 0 && num_of_votes_rcvd == ru/2 && VoteData.getVoteData().getVotesReceived().contains(VoteData.getVoteData().getDS()))){
			System.out.println("Received majority votes. Need to update X!");
			VoteData.getVoteData().incrementVersionNumber();
			VoteData.getVoteData().setRU(num_of_votes_rcvd);
			updateDistinguishedSite(MyData.getMyData().getNeighbors());
			for (Server neighbor : MyData.getMyData().getNeighbors()) {
				neighbor.sendObject("VN\t"+VoteData.getVoteData().getVN());
				neighbor.sendObject("RU\t"+VoteData.getVoteData().getRU());
				if(VoteData.getVoteData().getDS()!= null)
					neighbor.sendObject("DS\t"+VoteData.getVoteData().getDS());
				else
					neighbor.sendObject("DS\tnull");
			}
			System.out.println("Write success");
		}
		else{
			System.out.println("Write failed.");
			return;
		}
	}

	public static void updateDistinguishedSite(List<Server> neighbors) {
		if(neighbors.size()%2!=0){//odd number of neighbors => even number of members in network.
			char largestNodeLabel = MyData.getMyData().getMyNodeLabel();
			for (Server server : neighbors) {
				if(server.getNodeConnectedTo()>largestNodeLabel)
					largestNodeLabel = server.getNodeConnectedTo();
			}
			VoteData.getVoteData().setDS(largestNodeLabel);
		}
		else {
			VoteData.getVoteData().setDS(null);
			
		}
	}
}
