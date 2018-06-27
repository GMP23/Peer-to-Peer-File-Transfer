/*
 * file: Server.java
 * author: garret patten
 * date: 12/2/17
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class Server {	
	//code to create linked list that will contain registrants
	public static NodeList directory = new NodeList();
	
	public static void main(String[] args) throws IOException{

		//test for correct number of parameters
		if (args.length != 1){
			throw new IllegalArgumentException("Parameter(s): <Port>");
		}
		
		int servPort = Integer.parseInt(args[0]);
			
		//creation of TCP Server Socket
		ServerSocket serverSocket = new ServerSocket(servPort);
		boolean listening = true;
		
		//initialize variables for having client address and port
		SocketAddress clientAddress;
		int port;
		
		ServerThread ST;

		//continuous loop to accept client connections and determine their purpose before passing them to the ServerThread
		while(listening){
			System.out.println("Server is on and waiting for a connection...");
			String clientType = null;
			Socket clientSocket = serverSocket.accept();
			ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
			
			//accepting client connection and acquiring client IP Address
			clientAddress = clientSocket.getRemoteSocketAddress();
			port = clientSocket.getPort();
			System.out.println("Handling client at " + clientAddress + ", port number: " + port);
			
			//receiving client type --> register, unregister, download, seed
			try{
				clientType = (String)ois.readObject();
				System.out.println("clientType is: " + clientType);
				
				/*
				System.out.println("clientSocket: " + clientSocket);
				System.out.println("directory: " + directory);
				System.out.println("ois: " + ois);
				System.out.println("oos: " + oos);
				System.out.println("clientType: " + clientType);
				*/
			
				ST = new ServerThread(clientSocket, directory, ois, oos, clientType);
				Thread T = new Thread(ST);
				T.start();
			}catch (IOException e){}
			catch (ClassNotFoundException e){}
			

			//debug --> gives nodeCount and iterates through node files
			//System.out.println("nodeCount: " + directory.nodeCount);
			//Node x = directory.pre;
			//for(int i = 0; i < directory.nodeCount; i++){
				//x = x.next;
				//System.out.println(x.file);
			//}
		}
	}
}
