/*
 * file: clientUnregister.java
 * author: garret patten
 * date: 12/2/17
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class clientUnregister {
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
    	oos.writeObject("unregister");
    	oos.flush();
		clientUtilities clientUtil = new clientUtilities(socket);
    	
    	//receive status from server
    	System.out.println("receiving status from server");
    	ois = new ObjectInputStream(socket.getInputStream());
    	String status = (String)ois.readObject();
    	System.out.println("Server status: " + status);
    	
    	//get filename from client to register
    	System.out.println("Welcome back to our file-sharing app! To unregister a file, you will have to enter the filename.");
    	String input = clientUtil.readFromKeyboard();
    	System.out.println("Client input is: " + input);
    	oos.flush();
    	oos.writeObject(input);
    	oos.flush();
    	System.out.println("filename sent to server");
    	
    	while (true){
	    	String message = (String)ois.readObject();
	    	System.out.println(message);
	    	System.out.println("If you are done unregistering, simply write 'exit' instead of a filename.");
	    	input = clientUtil.readFromKeyboard();
	    	if (input.equals("exit")){break;}
	    	else{
	    		oos.flush();
	    		oos.writeObject(input);
	    		oos.flush();
	    		System.out.println("filename sent to server");
	    	}
    	}
	}
}
