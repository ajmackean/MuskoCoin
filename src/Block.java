import java.util.Date;

public class Block {
	
	// digital identification for use on chain
	public String hash;
	// implements a linked list structure through node reference
	public String previousHash;
	private String data;
	private long timeStamp;
	
	// constructor. Implementation of a linked list data structure 
	public Block (String data, String previousHash) {
		
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
		
	}
	
	// helper method for SHA256 hash encryption
	public String calculateHash() {
		
		// allocation of variable params using static method call
		String returnedHash = StringUtility.applySha256(
				previousHash +
				Long.toString(timeStamp) +
				data
				);
				
		return returnedHash;
	}

}
