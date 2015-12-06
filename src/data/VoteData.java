package data;

import java.util.HashSet;

public class VoteData {
	
	private static final VoteData vData = new VoteData();
	private int RU;
	private int VN;
	private Character DS;
	private HashSet<Character> totalVotesReceived = new HashSet<Character>();
	private HashSet<Character> validVotesReceived = new HashSet<Character>();
	private int requestCount = 0;
	
	public static VoteData getVoteData(){
		return vData;
	}

	public int getRU() {
		return RU;
	}

	public void setRU(int rU) {
		RU = rU;
	}

	public int getVN() {
		return VN;
	}

	public void setVN(int vN) {
		VN = vN;
	}

	public Character getDS() {
		return DS;
	}

	public void setDS(Character dS) {
		DS = dS;
	}

	public HashSet<Character> getValidVotesReceived() {
		return validVotesReceived;
	}

	public void addToValidVotesReceived(Character vote) {
		this.validVotesReceived.add(vote);
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void incrementRequestCount() {
		this.requestCount++;
	}

	public void incrementVersionNumber() {
		this.VN++;
	}

	public HashSet<Character> getTotalVotesReceived() {
		return totalVotesReceived;
	}

	public void addToTotalVotesReceived(Character senderID) {
		this.totalVotesReceived.add(senderID);
	}
}
