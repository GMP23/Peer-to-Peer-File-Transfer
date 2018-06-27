/*
 * file: Utilities.java
 * author: garret patten
 * date: 12/2/17  
 */

import java.net.*;

public class Utilities {
	public Socket clientSocket;
	public String client_IP;
	public static NodeList directory;
	
	public Utilities(Socket clientSocket, NodeList directory){
		this.clientSocket = clientSocket;
		String clientAddress = clientSocket.getRemoteSocketAddress().toString();
		String substring = clientAddress.substring(1, clientAddress.indexOf(":"));
		this.client_IP = substring;
		this.directory = directory;
	}
	
	public synchronized void insertNode(Node node){
		Node x = directory.post.previous;
		Node y = directory.post;
		
		x.next = node;
		node.previous = x;
		
		y.previous = node;
		node.next = y;
		
		directory.nodeCount += 1;
	}
	
	public synchronized void removeNode(String fileToRemove){
		Node x = directory.pre;
		for (int i = 0; i < directory.nodeCount; i++){
			x = x.next;
			//System.out.println("client_IP: " + client_IP);
			//System.out.println("x.IP: " + x.IP);
			if (x.IP.equals(client_IP) && x.file.equals(fileToRemove)){
				Node nodeOne = x.previous;
				Node nodeTwo = x.next;
				nodeOne.next = nodeTwo;
				nodeTwo.previous = nodeOne;
				directory.nodeCount -= 1;
			}
		}
	}
	
	public synchronized void setAvailable(String Seed_IP, int PORT){
		Node x = directory.pre;
		for (int i = 0; i < directory.nodeCount; i++){
			x = x.next;
			if (x.IP.equals(Seed_IP)){
				x.PORT = PORT;
				x.available = true;
			}
		}
	}
	
	public synchronized void setUnavailable(String Seed_IP){
		Node x = directory.pre;
		for (int i = 0; i < directory.nodeCount; i++){
			x = x.next;
			if (x.IP.equals(Seed_IP)){x.available = false;}
		}
	}
	
	public synchronized Node getFile(String filename){
		Node x = this.directory.pre;
		//System.out.println("Looking for file...");
		//System.out.println("directory: " + this.directory);
		//System.out.println("nodeCount: " + this.directory.nodeCount);
		Node returnNode = new Node();
		Integer highestRating = -1000000;
		boolean firstTime = true;
		for (int i = 0; i < this.directory.nodeCount; i++){
			x = x.next;
			if(x.file.equals(filename)){
				if (firstTime){
					returnNode = x;
					highestRating = x.rating;
					firstTime = false;
				}
				if (x.rating > highestRating){returnNode = x;}
			}
			else{
				System.out.println("tried node from IP: " + x.IP);
				System.out.println("No success.");
				System.out.println("x.file: " + x.file);
				System.out.println("Looking for: " + filename);
			}
		}
		if (highestRating > -1000000){return returnNode;} //file was found
		else{return this.directory.pre;}
	}
	
	public synchronized void updateRating(String download_IP, String rating){
		Integer r = Integer.parseInt(rating);
		Node x = this.directory.pre;
		
		if(r.equals(0)){ //download unsuccessful...drop ratings by 2
			for(int i=0; i < this.directory.nodeCount; i++){
				x = x.next;
				if(x.IP.equals(download_IP)){
					x.rating -= 2;
				}
			}
		}
		else{ //download was successful...increment ratings by 1
			for (int i = 0; i < this.directory.nodeCount; i++){
				x = x.next;
				if (x.IP.equals(download_IP)){
					x.rating += 1;
				}
			}
		}
	}
} //end of class