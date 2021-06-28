import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId;
	public PublicKey sender;
	public PublicKey recipient;
	// bad boy variable
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	// transaction generation running tally
	private static int sequence = 0;
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
		
	}
	
	private String calculateHash() {
		
		// avoids having identical hash values
		sequence++;
		return StringUtility.applySha256(
				
				StringUtility.getStringFromKey(sender) + 
				StringUtility.getStringFromKey(recipient) +
				Float.toString(value) + sequence
				);
				
	}
	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(recipient) + Float.toString(value);
		signature = StringUtility.applyECDSASig(privateKey, data);
	}
	
	public boolean verifySignature() {
		String data = StringUtility.getStringFromKey(sender) + StringUtility.getStringFromKey(recipient) + Float.toString(value);
		return StringUtility.verifyECDSASig(sender, data, signature);
	}
	
public boolean processTransaction() {
		
		if(verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
				
		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i : inputs) {
			i.UTXO = Muskchain.UTXOs.get(i.transactionOutputId);
		}

		//check if transaction is valid:
		if(getInputsValue() < Muskchain.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		//generate transaction outputs:
		float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
		transactionId = calculateHash();
		outputs.add(new TransactionOutput( this.recipient, value,transactionId)); //send value to recipient
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
				
		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			Muskchain.UTXOs.put(o.id , o);
		}
		
		//remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			Muskchain.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	//returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //if Transaction can't be found skip it 
			total += i.UTXO.value;
		}
		return total;
	}

	//returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
	
	
	

}
