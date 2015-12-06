package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import sockets.Server;
import data.MyData;
import data.VoteData;

public class VoteManager {
	
	public static void initializeVoteData(){
		List<Server> neighbors = MyData.getMyData().getNeighbors();
		VoteData.getVoteData().setVN(0);
		VoteData.getVoteData().setRU(neighbors.size()+1);
		updateDistinguishedSite(neighbors);
		displayVoteData();
		File X = new File("X.txt");
		if(X.exists()){
			X.delete();
		}
	}

	public static void displayVoteData() {
		System.out.println("VN = "+VoteData.getVoteData().getVN());
		System.out.println("RU = "+VoteData.getVoteData().getRU());
		System.out.println("DS = "+VoteData.getVoteData().getDS());
	}

	public static void updateXIfAllVotesReceived() {
		if(VoteData.getVoteData().getTotalVotesReceived().size() != (MyData.getMyData().getNeighbors().size()+1))
			return;
		int num_of_votes_rcvd = VoteData.getVoteData().getValidVotesReceived().size();
		int ru = VoteData.getVoteData().getRU();
		if((num_of_votes_rcvd > ru/2)||
			(ru%2 == 0 && num_of_votes_rcvd == ru/2 && VoteData.getVoteData().getValidVotesReceived().contains(VoteData.getVoteData().getDS()))){
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
			File X = new File("X.txt");
			if(!X.exists()){
				try {
					X.createNewFile();
				} catch (IOException e) {
					System.err.println("IOException when creating file X: "+e.getMessage());
				}
			}
			try(BufferedWriter bw = new BufferedWriter(new FileWriter(X, true))){
				bw.append("Version no:"+VoteData.getVoteData().getVN()+": Writing from "+MyData.getMyData().getMyNodeLabel()+"\r\n");
			} catch (IOException e) {
				System.err.println("IOException when writing to file X: "+e.getMessage());
			}
			System.out.println("Write success");
		}
		else{
			System.out.println("Write failed.");
		}
		for (Server neighbor : MyData.getMyData().getNeighbors()) {
			neighbor.sendObject("display_vote_data");
		}
		displayVoteData();
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
