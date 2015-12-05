package control;
import sockets.Server;
import data.Message;
import data.MyData;
import data.VoteData;

public class MessageProcessor extends Thread {
	
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
				else 
				{
					System.out.println("Received message of unknown type: "+inObject.getClass());
				}
			}
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
		if(data.equalsIgnoreCase("disconnecting")){
			Server server = MyData.getMyData().getNeighborNodeNum(senderID);
			if (server!=null){
				server.close();
			}
		}
		else if(data.equalsIgnoreCase("initialize")){
			VoteManager.initializeVoteData();
			System.out.print("> ");
		}
		else if(data.equalsIgnoreCase("display_vote_data")){
			VoteManager.displayVoteData();
			System.out.print("> ");
		}
		else if(data.equalsIgnoreCase("releasing_vote")){
			VoteData.getVoteData().setVoteGivenTo(null);
		}
		else if(data.startsWith("requesting_votes")){
			if(VoteData.getVoteData().getVoteGivenTo() == null){
				System.out.println("Sending vote to "+senderID);
				MyData.getMyData().getNeighborNodeNum(senderID).sendObject("vote_granted");
				String[] split = data.split("\t");
				VoteData.getVoteData().setVoteGivenTo(split[1].charAt(0));
			}
			else{
				System.out.println("Received request but not sending vote to "+senderID+" since vote already sent to "+VoteData.getVoteData().getVoteGivenTo());
			}
			System.out.print("> ");
		}
		else if(data.equalsIgnoreCase("vote_granted")){
			System.out.println("Received vote from "+senderID);
			VoteData.getVoteData().getVotesReceived().add(senderID);
			VoteManager.updateXIfAllVotesReceived();
		}
		
		
	}

}
