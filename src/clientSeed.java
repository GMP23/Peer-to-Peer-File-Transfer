/*
 * file: clientSeed.java
 * author: garret patten
 * date: 12/2/17
 */

import java.io.*;
import java.net.*;

public class clientSeed {
	public static void main(String args[]) throws Exception{
		String serverAddress = args[0];
		int servPort = Integer.parseInt(args[1]);
		Socket socket = null;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
				
		//creation of socket that is connected to server on specified port
		System.out.println("creating socket and streams");
    	socket = new Socket(serverAddress, servPort);
    	oos = new ObjectOutputStream(socket.getOutputStream());
    	System.out.println("sending clientType to server");
    	oos.writeObject("seed");
    	oos.flush();
		clientUtilities clientUtil = new clientUtilities(socket);
    	
    	//receive status from server
    	System.out.println("receiving status from server");
    	ois = new ObjectInputStream(socket.getInputStream());
    	String status = (String)ois.readObject();
    	System.out.println("Status from server: " + status);
    	
    	//sending status back to server
    	oos.flush();
    	String responseToServer = "OK";
    	oos.writeObject(responseToServer);
    	oos.flush();
    	
    	//now receive necessary information from the server
    	String currentPORT = (String)ois.readObject();
    	System.out.println("Welcome to back our file-sharing app!");
    	System.out.println("Your availability has been confirmed. You are currently seeding your files.");
    	System.out.println("To stop seeding your files, hit the keys 'Ctrl' + 'C' at the same time...");
    	Integer myPORT = Integer.parseInt(currentPORT);
    	ServerSocket servSock = new ServerSocket(myPORT);
    	boolean listening = true;
    	
    	//spawn a thread to wait for stop order from client keyboard
    	//client 'exit' will change listening to false
    	//program will exit out at next possible opportunity
    	
    	//beginning of server-like code to wait for client to request download
    	while (listening){
    		System.out.println("Waiting for a client to connect...");
	    	Socket downloaderSocket = servSock.accept();
	    	System.out.println("Client connected...");
	    	System.out.println("Client downloading beginning...you will not be able to exit until the download has finished.");
	    	
	    	//InetAddress InetA = InetAddress.getByName("localhost");
	    	//System.out.println("InetA: " + InetA);
	    	
	    	System.out.println("Creating object input stream from downloader to seeder");
	    	ObjectInputStream download_ois = new ObjectInputStream(downloaderSocket.getInputStream());
	    	System.out.println("ObjectInputStream successfully created...");
	    	String filename = (String)download_ois.readObject();
	    	System.out.println("Filename received from client: " + filename);
	    	
	    	File file = new File("../" + filename);
	    	FileInputStream fis = new FileInputStream(file);
	    	BufferedInputStream bis = new BufferedInputStream(fis);
	    	
	    	ObjectOutputStream download_oos = new ObjectOutputStream(downloaderSocket.getOutputStream());
	    	
	    	byte[] file_contents;
	    	long fileLength = file.length();
	    	long current = 0;
	    	
	    	//long start = System.nanoTime();
	    	while(current!=fileLength){
	    		int size = 10000;
	    		if(fileLength - current >= size){
	    			current += size;
	    		}
	    		else{
	    			size = (int)(fileLength - current);
	    			current = fileLength;
	    		}
	    		file_contents = new byte[size];
	    		bis.read(file_contents, 0, size);
	    		download_oos.write(file_contents);
	    		System.out.println("Sending file..." + (current*100)/fileLength+"% complete!");
	    	}
	    	
	    	download_oos.flush();
	    	//now that the p2p file transfer has completed, socket must be closed
	    	downloaderSocket.close();
	    	
	    	if (!listening){break;}
    	}
    	
    	//be ready for client to stop seeding files
    	System.out.println("When you would like to stop seeding, simply hit 'Ctrl' + 'C' at the same time");
    	String input = clientUtil.readFromKeyboard();
    	oos.flush();
    	oos.writeObject(input);
    	oos.flush();
    	System.out.println("stop order sent to server");
    	
    	servSock.close();
	}
}
