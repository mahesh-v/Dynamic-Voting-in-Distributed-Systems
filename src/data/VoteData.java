package data;

import java.util.HashSet;

public class VoteData {
	
	private static final VoteData vData = new VoteData();
	private int RU;
	private int VN;
	private Character DS;
	private HashSet<Character> votesReceived = new HashSet<Character>();
	private int requestCount = 0;
	private Character voteGivenTo = null;
	private boolean waitingForMajority = false;
	
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

	public HashSet<Character> getVotesReceived() {
		return votesReceived;
	}

	public void addToVotesReceived(Character vote) {
		this.votesReceived.add(vote);
	}

	public int getRequestCount() {
		return requestCount;
	}

	public void incrementRequestCount() {
		this.requestCount++;
	}

	public Character getVoteGivenTo() {
		return voteGivenTo;
	}

	public void setVoteGivenTo(Character voteGivenTo) {
		this.voteGivenTo = voteGivenTo;
	}

	public boolean isWaitingForMajority() {
		return waitingForMajority;
	}

	public void setWaitingForMajority(boolean waitingForMajority) {
		this.waitingForMajority = waitingForMajority;
	}
}
