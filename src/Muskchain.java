
public class Muskchain {

	public static void main(String[] args) {
		
		Block genesisBlock = new Block("initial block", "0");
		System.out.println("Hash for block 1: " + genesisBlock.hash);
		
		Block secondBlock = new Block("second block", genesisBlock.hash);
		System.out.println("Hash for block 2: " + secondBlock.hash);
		
		Block thirdBlock = new Block("third block", secondBlock.hash);
		System.out.println("Hash for block 3: " + thirdBlock.hash);

	}

}