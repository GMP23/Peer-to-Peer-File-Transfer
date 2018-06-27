/*
 * file: clientUtilities.java
 * author: garret patten
 * date: 12/2/17
 */

import java.net.*;
import java.io.*;

public class clientUtilities {
	public Socket socket;
	
	public clientUtilities(Socket socket){
		this.socket = socket;
	}
	
	public String readFromKeyboard() throws Exception{
		BufferedReader br;
		String sendStr;
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter filename: ");
		sendStr = br.readLine();
		return sendStr;
	}
}
