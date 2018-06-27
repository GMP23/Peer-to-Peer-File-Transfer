/*
 * file: Node.java
 * author: garret patten
 * date: 12/2/17
 */

public class Node {
	Node previous;
	Node next;
	String IP;
	int PORT;
	String file;
	boolean available;
	Integer rating;

	
	public Node(){
		this.rating = 0; //must initiate to zero to avoid NullPointerException in Utilities.getFile()
	}
	
	public Node(String IP, int PORT, String file){
		this.IP = IP;
		this.PORT = PORT;
		this.file = file;
		this.rating = 0;
	}
}


