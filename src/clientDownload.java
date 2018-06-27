/*
 * file: clientDownload.java
 * author: garret patten
 * date: 12/2/17
 */

import java.io.*;
import java.net.*;

public class clientDownload {
	public static void main(String args[]) throws Exception{
		String serverAddress = args[0];
		int servPort = Integer.parseInt(args[1]);
		Socket socket = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
				
		//must acquire multicast IP and port from server
		//creation of socket that is connected to server on specified port
		System.out.println("creating socket and streams");
    	socket = new Socket(serverAddress, servPort);
    	oos = new ObjectOutputStream(socket.getOutputStream());
    	System.out.println("sending clientType to server");
    	oos.writeObject("download");
    	oos.flush();
		clientUtilities clientUtil = new clientUtilities(socket);
    	
    	//receive status from server
    	System.out.println("receiving status from server");
    	ois = new ObjectInputStream(socket.getInputStream());
    	String status = (String)ois.readObject();
    	System.out.println("Server status: " + status);
    	    	
    	//get filename from client to register
    	System.out.println("Welcome to our file-sharing app! To download a file, first enter its name here.");
    	String input = clientUtil.readFromKeyboard();
    	System.out.println("Client input is: " + input);
    	oos.flush();
    	oos.writeObject(input);
    	oos.flush();
    	System.out.println("filename sent to server");
    	
    	while (true){
    		System.out.println("Waiting to receive message from broker.");
	    	String message = (String)ois.readObject();
	    	System.out.println(message);
	    	if (input.equals("exit")){break;}
	    	else if (message.substring(0, 5).equals("Sorry")){ //file was not found
	    		//System.out.println(message);
	    		System.out.println("If you would like to search for another file, simply enter its name below.");
	    		System.out.println("If you are done with this session, simply write 'exit' instead of a filename.");
	    		input = clientUtil.readFromKeyboard();
	    		oos.flush();
	    		oos.writeObject(input);
	    		oos.flush();
	    		System.out.println("filename sent to server");
	    	}
	    	else{ //file was found
	    		System.out.println("Your file was found, preparing to download");
	    		String downloadIP = message;
	    		System.out.println("IP received from broker: " + downloadIP);
	    		
	    		oos.flush();
	    		String response = "OK";
	    		oos.writeObject(response);
	    		oos.flush();
	    		
	    		System.out.println("response sent to server");
	    		System.out.println("Waiting for port from broker...");
	    		message = (String)ois.readObject();
	    		String downloadPORT = message;
	    		System.out.println("PORT received from broker: " + downloadPORT);
	    		Integer dPort = Integer.parseInt(downloadPORT);
	    		
	    		System.out.println("Sending exit to broker");
	    		oos.flush();
	    		response = "exit";
	    		oos.writeObject(response);
	    		oos.flush();
	    		
	    		//can connect with downloadIP and downloadPORT to search for input (filename)
	    		socket.close();
	    		System.out.println("broker socket closed");
	    		
	    		Socket downloadSocket = new Socket(downloadIP, dPort);
	    		clientUtilities downloadUtil = new clientUtilities(downloadSocket);
	    		ObjectOutputStream download_oos = new ObjectOutputStream(downloadSocket.getOutputStream());
	    		System.out.println("sending filename to clientServer");
	    		System.out.println("filename to be sent: " + input);
	    		download_oos.writeObject(input);
	    		download_oos.flush();
	    		System.out.println("filename sent to clientServer");
	    		
	    		byte[] file_contents = new byte[10000];
	    		
	    		//must initialize an output stream to save the file
	    		FileOutputStream fos = new FileOutputStream(input);
	    		BufferedOutputStream bos = new BufferedOutputStream(fos);
	    		InputStream is = downloadSocket.getInputStream();
	    		
	    		int bytesRead = 0;
	    		
	    		while((bytesRead=is.read(file_contents))!=-1){
	    			bos.write(file_contents, 0, bytesRead);
	    		}
	    		
	    		bos.flush();
	    		downloadSocket.close();
	    		
	    		System.out.println("File download complete.");
	    		
	    		//now must send rating to server
	    		Socket ratingSocket = new Socket(serverAddress, servPort);
	        	
	    		ObjectOutputStream rating_oos = new ObjectOutputStream(ratingSocket.getOutputStream());
	        	String clientType = "rating";
	        	System.out.println("Sending rating clientType to server...");
	        	rating_oos.writeObject(clientType);
	        	
	        	ObjectInputStream rating_ois = new ObjectInputStream(ratingSocket.getInputStream());
	        	String ratingResponse = (String)rating_ois.readObject();
	        	System.out.println("response from server after client type: " + ratingResponse);
	        	
	        	//sending IP to rate
	        	rating_oos.flush();
	        	rating_oos.writeObject(downloadIP);
	        	rating_oos.flush();
	        	System.out.println("IP sent to server for rating...");
	        	
	        	ratingResponse = (String)rating_ois.readObject();
	        	System.out.println("Server response: " + ratingResponse);
	        	
	        	String rating = "1";
	        	System.out.println("Sending rating to server...");
	        	rating_oos.flush();
	        	rating_oos.writeObject(rating);
	        	rating_oos.flush();
	        	
	        	System.out.println("Waiting for response from server...");
	    		ratingResponse = (String)rating_ois.readObject();
	    		System.out.println("response from server: " + ratingResponse);
	    		
	    		System.out.println("Closing socket used for rating");
	    		ratingSocket.close();
	        	break;
	    	}
    	}
	}
}
