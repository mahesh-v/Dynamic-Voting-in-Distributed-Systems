package control;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import data.Message;
import data.MyData;
import data.VoteData;

public class MessageProcessor extends Thread {
	
	@SuppressWarnings("unchecked")
	public void run()
	{
		while(!MyData.getMyData().isShutDown())
		{
			Message m = MyData.getMyData().getMessageQueue().poll();
			if(m!=null)
			{
				Object inObject = m.getMessage();
				if(inObject instanceof String)
				{
					processStringInput((String) inObject, m.getSenderNodeID());
				}
				else if(inObject instanceof List<?>)
				{
					processsListInput((List<String>) inObject);
				}
				else 
				{
					System.out.println("Received message of unknown type: "+inObject.getClass());
				}
			}
		}
	}
	
	private void processsListInput(List<String> lines) {
		try {
			if(!Files.exists(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator))){
				Files.createDirectories(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator));
			}
			Files.write(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator+"X.txt"), lines, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.err.println("Error while writing to file X: "+e.getMessage());
		}
	}

	private void processStringInput(String data, char senderID) {
//		if(data.contains("CONNECT_TO"))
//		{
//			String[] fields = data.split("\t");
//			if(fields.length < 2)
//				System.err.println("Abnormal connect message received: "+data);
//			for (int i = 1; i < fields.length; i++) {
//				if(!fields[i].isEmpty())
//				{
//					String[] split = fields[i].split(",");
//					if(split.length != 2)
//						continue;
//					InitiatingServer is = new InitiatingServer(split[1], split[0].charAt(0), 5050);
//					is.connectToServer();
//				}
//			}
//		}
		if(data.equalsIgnoreCase("initialize")){
			VoteManager.initializeVoteData();
			System.out.print("> ");
		}
		else if(data.equalsIgnoreCase("display_vote_data")){
			VoteManager.displayVoteData();
			System.out.print("> ");
		}
		else if(data.startsWith("requesting_votes")){
			System.out.println("Sending vote to "+senderID);
			MyData.getMyData().getNeighborNodeNum(senderID).sendObject("vote_granted\t"+VoteData.getVoteData().getVN());
			System.out.print("> ");
		}
		else if(data.startsWith("vote_granted")){
			String[] split = data.split("\t");
			int rcvd_VN = Integer.parseInt(split[1]);
			if(rcvd_VN>VoteData.getVoteData().getHighestVersion()){
				VoteData.getVoteData().setHighestVersion(rcvd_VN);
				VoteData.getVoteData().setNodeWithHighestVersion(senderID);
			}
			VoteData.getVoteData().addToTotalVotesReceived(senderID);
			if(rcvd_VN==VoteData.getVoteData().getVN()){
				System.out.println("Received valid vote from "+senderID);
				VoteData.getVoteData().getValidVotesReceived().add(senderID);
			}
			VoteManager.updateXIfAllVotesReceived();
		}
		else if(data.startsWith("VN")){
			String[] split = data.split("\t");
			int vn = Integer.parseInt(split[1]);
			VoteData.getVoteData().setVN(vn);
		}
		else if(data.startsWith("RU")){
			String[] split = data.split("\t");
			int ru = Integer.parseInt(split[1]);
			VoteData.getVoteData().setRU(ru);
		}
		else if(data.startsWith("DS")){
			String[] split = data.split("\t");
			if(split[1].equalsIgnoreCase("null"))
				VoteData.getVoteData().setDS(null);
			else{
				char ds = split[1].charAt(0);
				VoteData.getVoteData().setDS(ds);
			}
		}
		else if(data.startsWith("requesting_X")){
			try {
				if(!Files.exists(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator))){
					Files.createDirectories(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator));
				}
				List<String> lines = Files.readAllLines(Paths.get(MyData.getMyData().getMyNodeLabel()+File.separator+"X.txt"));
				System.out.println("Sending updated X to "+senderID);
				if(lines!=null)
					MyData.getMyData().getNeighborNodeNum(senderID).sendObject(lines);
				else
					System.out.println("No lines read from file.");
			} catch (IOException e) {
				System.err.println("Error while reading from file X: "+e.getMessage());
			}
		}
		
	}

}
