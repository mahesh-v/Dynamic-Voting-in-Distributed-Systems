package control;

import java.util.List;

import sockets.Server;
import data.MyData;
import data.VoteData;

public class VoteManager {
	
	public static void initializeVoteData(){
		List<Server> neighbors = MyData.getMyData().getNeighbors();
		VoteData.getVoteData().setVN(1);
		VoteData.getVoteData().setRU(neighbors.size());
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
		displayVoteData();
	}

	public static void displayVoteData() {
		System.out.println("VN = "+VoteData.getVoteData().getVN());
		System.out.println("RU = "+VoteData.getVoteData().getRU());
		System.out.println("DS = "+VoteData.getVoteData().getDS());
	}

	public static void updateXIfAllVotesReceived() {
		if(!VoteData.getVoteData().isWaitingForMajority())
			return;
		int ru = VoteData.getVoteData().getRU();
		int numberOfVotes = VoteData.getVoteData().getVotesReceived().size();
		if((numberOfVotes > ru/2)||
			(ru%2 == 0 && numberOfVotes == ru/2 && VoteData.getVoteData().getVotesReceived().contains(VoteData.getVoteData().getDS()))){
			System.out.println("Received majority votes. Need to update X!");
		}
		else{
			return;
		}
		for (Server neighbor : MyData.getMyData().getNeighbors()) {
			neighbor.sendObject("releasing_vote");
		}
		VoteData.getVoteData().setWaitingForMajority(false);
	}
}
