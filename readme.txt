file: readme.txt
author: garret patten
date: 12/5/17

--------------------------------------------------------------------------------------------

assignment: Peer 2 Peer File Transfer

Introduction:

Now that you have mastered peer2peer gaming, you will tackle peer2peer file transfers. This project will be based on the operation of a Simplified version of Napster we discussed in class. Peers will register with a broker files that they wish to share with members of the community, the broker will provide requesting peers the IP:Port# of the such peers. NO FILES will actually be located with the broker, only IP:port#:filename tuples.

Broker:

The broker is responsible for registering peers (their IP:PORT#:Filename tuple), and providing this information to a requesting peer. If a peer requests a file that is not in the data structure holding the tuples, then the broker must inform the requesting peer that the file does not exist in the community.

Peers:

Peers register a filename(s) and its IP and port number with the broker. Then, when  peer searches for a file, it contacts the broker for the host peer's IP and Port number. After receiving this pair, the requesting peer directly (not using multicasting) the host peer to transfer the file.

--------------------------------------------------------------------------------------------

Classes:

Server --> to accept client calls
ServerThread --> to handle the client functions (register, unregister, seed, download)
clientRegister --> to handle a client registering a file with the server
clientUnregister --> to handle a client unregistering a file with the server
clientSeed --> to handle when a client wants to allow peers to download his files
clientDownload --> to handle when a client wants to download a file 
Utilities --> utilities methods used when dealing with clients
clientUtilities --> extra utilities for client-side programming
Node --> provides the basic data structure to hold client registration info
NodeList --> a linked list built of nodes that provide the Server with its directory of users and files

--------------------------------------------------------------------------------------------

How to Use (from the Terminal):

To Launch the Server Broker:
1. download Project5_Server directory
2. navigate to the Project5/src directory
3. type "javac *.java" to compile the source code
4. type "java Server <Server PORT>" with the <Server PORT> being the argument of what port you would like to use...I generally use 5555.

To Register a File with the Server: 
1. download Project5_Reg directory
2. navigate to the Project5/src directory
3. type "javac *.java" to compile the source code
4. type "java clientRegister <Server IP> <Server PORT>
5. when prompted for the file name, simply type in the name...so if you want to register "mobydick.txt" you will type "mobydick.txt" (be sure to include the '.txt' portion of the file)
6. the server will ask if you want to register another file...if so, repeat step 5...if not, simply type "exit" and the program will end

To Unregister a File with the Server: 
1. download Project5_Reg directory
2. navigate to the Project5/src directory
3. type "javac *.java" to compile the source code
4. type "java clientUnregister <Server IP> <Server PORT>
5. when prompted for the file name, simply type in the name...so if you want to unregister "mobydick.txt" you will type "mobydick.txt" (be sure to include the '.txt' portion of the file)
6. the server will ask if you want to unregister another file...if so, repeat step 5...if not, simply type "exit" and the program will end

To Seed your Files:
1. download Project5_Reg directory
2. navigate to the Project5/src directory
3. type "javac *.java" to compile the source code
4. type "java clientSeed <Server IP> <Server PORT>
5. the server will notify you that you are currently seeding all your files...your computer will now act as a server in a way...to exit hit 'Ctrl + C' as the server is on a loop

To Download a File from the Server:
1. download Project5_Download directory
2. navigate to the Project5/src directory
3. type "javac *.java" to compile the source code
4. type "java clientDownload <Server IP> <Server PORT>
5. when prompted for the file, simply type in the name...so if you want to register "mobydick.txt" you will type "mobydick.txt" (be sure to include the '.txt' portion of the file)
6. the server will automatically connect you with a peer who has the file if there is one seeding that exact file, and the file will automatically download to your Project5 directory...if not, it will apologize and ask you if you want to search for another file

--------------------------------------------------------------------------------------------

Rating System:

Essentially, the rating system works like this:

When the download process completes, the clientDownload reconnects with the ServerBroker and gives it the IP Address from the clientSeed and a rating of 1 if the download was successful (0 if not).

The ServerBroker then iterates through the NodeList and increments the ratings for that file of that IP Address.

The rating system is utilized when the ServerBroker fetches an IP and Port to send to clientDownload after a file is found...the user with the highest rating is chosen above all others.

Unsuccessful downloads decrement the rating by 2 and successful downloads increment the rating by 1...so seeders are penalized at twice the rate for unsuccessful downloads