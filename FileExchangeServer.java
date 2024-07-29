import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileExchangeServer {
	private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final Set<String> handles = ConcurrentHashMap.newKeySet();
	//private static final Map<Socket, String> client = new ConcurrentHashMap<>();

    public static void main(String[] args) {
		int nPort = Integer.parseInt(args[0]); // 4000
		System.out.println("Server: Listening on port " + args[0] + "...");
		

		try(ServerSocket serverSocket = new ServerSocket(nPort)) 
		{
			while(true){
			Socket serverEndpoint = serverSocket.accept();
			System.out.println("Server: New client connected: " + serverEndpoint.getRemoteSocketAddress());
			pool.submit(new ClientHandler(serverEndpoint));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Server: Connection is terminated.");
		}
	
	
	} //main

	public static boolean addHandle(String username){
		return handles.add(username);
	}
	
	// public static void mapSocketToUser(Socket socket, String username){
	// 	client.put(socket, username);
	// }

	// public static void unmapSocketFromUser(Socket socket){
	// 	client.remove(socket);
	// }
}//class


class ClientHandler implements Runnable{
	private Socket serverEndpoint;
	private String username = "Unknown";
	public ClientHandler(Socket serverEndpoing){
		this.serverEndpoint = serverEndpoint;
	}

	public void run(){
		try{
			DataInputStream in = new DataInputStream(serverEndpoint.getInputStream());
			DataOutputStream out = new DataOutputStream(serverEndpoint.getOutputStream());
			
			String command;
			while(true){
				command = in.readUTF();
				handleCommand(command, out);
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}

	private void handleCommand(String command, DataOutputStream out) throws IOException {
		String[] parts = command.split(" ");
		switch(parts[0]){
			case "/register":
				handleRegister(parts, out);
				break;
			case "/leave":
				handleLeave(out);
				break;
			case "/store":
				handleStore(parts, out);
				break;
			case "/dir":
				handleDir(out);
				break;
			case "/get":
				handleGet(parts, out);
				break;
			case "/?":
				handleHelp(out);
				break;
			default:
				out.writeUTF("Error: Command not found.");
		}
	}

	private void handleRegister(String[] parts, DataOutputStream out) throws IOException{
		if(parts.length == 2){
			String newUsername = parts[1];
			System.out.println("Received registration for " + newUsername);
			if(FileExchangeServer.addHandle(username)){
				// FileExchangeServer.mapSocketToUser(serverEndpoint, newUsername);
				username = newUsername;
				out.writeUTF("Welcome " + username + "!");
			}
			else{
				out.writeUTF("Error: Registration failed. Handle or alias already exists");
			}
		}
		else{
			out.writeUTF("Error: Command parameters do not match or is not allowed");
		}
	}

	private void handleLeave(DataOutputStream out) throws IOException{
		// FileExchangeServer.unmapSocketFromUser(serverEndpoint);
		out.writeUTF("Connection closed. Thank you!");
	}

	private void handleStore(String[] parts, DataOutputStream out) throws IOException{
		if(parts.length == 2){
			String filename = parts[1];
			File file = new File(filename);
			if(file.exists()){
				try{
					FileInputStream fis = new FileInputStream(file);
					byte[] buffer = new byte[(int) file.length()];
					fis.read(buffer);
					out.writeInt(buffer.length);
					out.write(buffer);
					String timestamp = getCurrentTime();
					out.writeUTF(username + "<" + timestamp + ">: Uploaded" + filename);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			else{
				out.writeUTF("Error: File not found");
			}
		}
		else{
			out.writeUTF("Error: Command parameters do not match or is not allowed");
		}
	}

	private void handleDir(DataOutputStream out) throws IOException{
		File dir = new File(".");
		File[] files = dir.listFiles();
		if(files != null){
			StringBuilder res = new StringBuilder("Server Directory\n");
			for(File file : files){
				if(file.isFile()){
					res.append(file.getName()).append("\n");
				}
			}
			out.writeUTF(res.toString());
		}

	}

	private void handleGet(String[] parts, DataOutputStream out) throws IOException{
		if(parts.length == 2){
			String filename = parts[1];
			File file = new File(filename);
			if(file.exists()){
				out.writeUTF("File received from Server: "+filename);
			}
			else{
				out.writeUTF("Error: File not found in the server.");
			}
		}
		else{
			out.writeUTF("Error: Command parameters do not match or is not allowed");
		}
	}

	private void handleHelp(DataOutputStream out) throws IOException{
		out.writeUTF("---- Here are the commands ----\n To join : /join <server_ip_ad> <port>\n" +
					"To leave : /leave\n To register : /register <handle>\n" +
					"To store a file : /store <filename>\n To get file list : /dir\n"+
					"To fetch a file : /get <filename>\n To request command list : /?" 
		);
	}

	private String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
} // class client handler