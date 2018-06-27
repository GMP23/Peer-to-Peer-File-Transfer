/*
 * file: ServerThread.java
 * author: garret patten
 * date: 12/2/17
 */

import java.io.*;
import java.net.*;

public class ServerThread implements Runnable{
	public Socket clientSocket;
	public NodeList directory;
	public ObjectInputStream ois;
	public ObjectOutputStream oos;
	public String clientType;
	public Utilities util;
	
	public ServerThread(Socket clientSocket, NodeList directory, ObjectInputStream ois, ObjectOutputStream oos, String clientType){
		this.clientSocket = clientSocket;
		this.directory = directory;
		this.ois = ois;
		this.oos = oos;
		this.clientType = clientType;
	}
	
	public void run(){
		this.util = new Utilities(clientSocket, directory);
		String clientAddress = clientSocket.getRemoteSocketAddress().toString();
		System.out.println("clientAddress: " + clientAddress);
		int PORT = clientSocket.getPort();
		System.out.println("client port: " + PORT);
		String client_IP_Address = clientAddress.substring(1, clientAddress.indexOf(":"));
		
		try {
			oos.flush();
			oos.writeObject("OK");
			oos.flush();
		} catch (IOException e){}
		
		try{
			//will receive String (filename) to add to directory if client wants to register a file
			if (clientType.equals("register")){
				while (true){
					System.out.println("Receiving filename from client");
					String filename = (String)ois.readObject();
					System.out.println("filename from client: " + filename);
					
					//only add legitimate files to the directory...not an exit request from client
					if (!filename.equals("exit")){
						//then a new node will need to be added into the directory
						Node node = new Node(client_IP_Address, PORT, filename);
						node.available = false;
						util.insertNode(node);
						System.out.println(filename + " node inserted into directory");
						System.out.println("nodeCount: " + directory.nodeCount);
					
						String message = "If you would like to register another file, enter the file name now.";
						oos.flush();
						oos.writeObject(message);
						oos.flush();
					}
					//if client types 'exit'
					else{break;}
				}
			}
			
			//will receive Node to remove from directory if client wants to unregister a file
			else if(clientType.equals("unregister")){
				while (true){
					System.out.println("Receiving filename from client");
					String filename = (String)ois.readObject();
					System.out.println("filename from client: " + filename);
				
					if (!filename.equals("exit")){
						//must search for node in directory and remove it
						util.removeNode(filename);
						System.out.println(filename + " node removed from directory");
						System.out.println("nodeCount: " + directory.nodeCount);
						
						String message = "If you would like to unregister another file, enter the file name now.";
						oos.flush();
						oos.writeObject(message);
						oos.flush();
					}
					else{break;}
				}
			}
			
			//will receive a String (filename) if client wants to download a song
			else if(clientType.equals("download")){
				while (true){
					System.out.println("Receiving filename from client");
					String filename = (String)ois.readObject();
					System.out.println("filename received: " + filename);
					
					/* 
					 * must iterate through nodes to find filename...and if available == true, then send IP
					 * else, send that no one is available with that filename
					 * either way, you are sending a string to the client
					 */
					Node result = util.getFile(filename);
					System.out.println("result.IP: " + result.IP);
					System.out.println("result.PORT: " + result.PORT);
				
					if (result.PORT != 0){ //file was found
						System.out.println("Sending IP");
						oos.flush();
						oos.writeObject(result.IP);
						oos.flush();
						
						System.out.println("Receiving response from client");
						String response = (String)ois.readObject();
						System.out.println("Client status: " + response);
						
						System.out.println("Sending PORT");
						oos.flush();
						String portString = Integer.toString(result.PORT);
						oos.writeObject(portString);
						oos.flush();
						
						response = (String)ois.readObject();
						if(response.equals("exit")){
							System.out.println("Received exit order, broker work completed.");
							break;
						}
					}
					else{ //file was not found
						System.out.println("Sending sorry message.");
						String message = "Sorry, but that file is currently unavailable.";
						oos.flush();
						oos.writeObject(message);
						oos.flush();
					}
				}
			}
			
			//will receive a String (IP) if client wants to seed
			else if(clientType.equals("seed")){
				System.out.println("Setting up client seeding...");
				
				//must iterate through nodes to find IP Address of seed client and set available to true
				util.setAvailable(client_IP_Address, PORT);
				
				//code to send back status to client
				System.out.println("Receiving confirmation to client");
				String responseFromClient = (String)ois.readObject();
				System.out.println("Status from client: " + responseFromClient);
				
				//now must send necessary information to client
				oos.flush();
				oos.writeObject("" + PORT);
				oos.flush();
				
				//debug to make sure status set to available
				//Node x = directory.pre;
				//for (int i = 0; i<directory.nodeCount; i++){
					//x = x.next;
					//System.out.println("Availability: " + x.available);
				//}
				
				/*
				 * when seed file reaches end, will send another message
				 * must iterate through nodes to find IP_seed
				 * when found, available must be set to false
				 */
				
				System.out.println("Waiting to receive stop order from seed client.");
				try{
					String status = (String)ois.readObject();
				} catch (Exception e){System.out.println("Stop order received from client. Setting status to unavailable");}
				
				util.setUnavailable(client_IP_Address);
				System.out.println("Client has been set to unavailable");
				}
			
			else if(clientType.equals("rating")){
				String rating_IP = (String)ois.readObject();
				System.out.println("rating IP: " + rating_IP);
				
				String receivedIP = "Got the IP...";
				oos.writeObject(receivedIP);
				
				String rating = (String)ois.readObject();
				System.out.println("rating from client: " + rating);
				
				String closeSocket = "OK";
				oos.writeObject(closeSocket);
				
				util.updateRating(rating_IP, rating);
				System.out.println("Done updating rating...");
				Node x = this.directory.pre.next;
				System.out.println("Node 1 rating: " + x.rating.toString());
			}
			
			else{
				//then there was an error
				System.out.println("clientType error: " + clientType);
				clientSocket.close();
			}
		}catch (Exception e){System.out.println("The client has disconnected...");}
		/*
		try {
			System.out.println("Receiving status from client");
			status = (String)ois.readObject();
			System.out.println("Client status is: " + status);
		} 
		catch (IOException e){}
		catch (ClassNotFoundException e) {}
		*/
	}
}
