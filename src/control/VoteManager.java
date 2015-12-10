package control;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
		File X = new File(MyData.getMyData().getMyNodeLabel()+File.separator+"X.txt");
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
			System.out.println("Received majority votes. Updating X...");
			if(VoteData.getVoteData().getNodeWithHighestVersion()!= null){
				updateXFromHighestNeighbor(VoteData.getVoteData().getNodeWithHighestVersion());
				while(VoteData.getVoteData().getNodeWithHighestVersion() != null){}
			}
			
			VoteData.getVoteData().incrementVersionNumber();
			
			//ru should be set to total neighbours + 1 because we will be updating all neighbours + , not just valid neighbours.
			int new_ru = MyData.getMyData().getNeighbors().size() + 1;
			VoteData.getVoteData().setRU(new_ru);
			
//			VoteData.getVoteData().setRU(num_of_votes_rcvd);
			
			updateDistinguishedSite(MyData.getMyData().getNeighbors());
			writeToX();
			for (Server neighbor : MyData.getMyData().getNeighbors()) {
				neighbor.sendObject("VN\t"+VoteData.getVoteData().getVN());
				neighbor.sendObject("RU\t"+VoteData.getVoteData().getRU());
				if(VoteData.getVoteData().getDS()!= null)
					neighbor.sendObject("DS\t"+VoteData.getVoteData().getDS());
				else
					neighbor.sendObject("DS\tnull");
				List<String> lines = readX();
				neighbor.sendObject(lines);
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

	public static void writeToX() {
		if(!Files.exists(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator))){
			try {
				Files.createDirectories(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator));
			} catch (IOException e) {
				System.err.println("Error creating directories: "+e.getMessage());
			}
		}
		File X = new File(MyData.getMyData().getMyNodeLabel()+File.separator+"X.txt");
		if(!X.exists()){
			try {
				X.createNewFile();
			} catch (IOException e) {
				System.err.println("IOException when creating file X: "+e.getMessage());
			}
		}
		try(BufferedWriter bw = new BufferedWriter(new FileWriter(X, true))){
			bw.append(VoteData.getVoteData().getContentToWrite());
		} catch (IOException e) {
			System.err.println("IOException when writing to file X: "+e.getMessage());
		}
	}
	
	private static List<String> readX() {
		try {
			if(!Files.exists(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator))){
				Files.createDirectories(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator));
			}
			List<String> lines = Files.readAllLines(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator+"X.txt"));
			if(lines!=null)
				return lines;
		} catch (IOException e) {
			System.err.println("Error while reading from file X: "+e.getMessage());
		}
		return new ArrayList<String>();
	}

	public static void updateXFromHighestNeighbor(Character nodeWithHighestVersion) {
		if(nodeWithHighestVersion == null)
			return;
		MyData.getMyData().getNeighborNodeNum(nodeWithHighestVersion).sendObject("requesting_X");
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
