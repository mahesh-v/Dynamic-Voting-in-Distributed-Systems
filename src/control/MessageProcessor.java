package control;
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
			if(rcvd_VN==VoteData.getVoteData().getVN()){
				System.out.println("Received valid vote from "+senderID);
				VoteData.getVoteData().getVotesReceived().add(senderID);
				VoteManager.updateXIfAllVotesReceived();
			}
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
		
		
	}

}
