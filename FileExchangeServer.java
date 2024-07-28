import java.net.*;
import java.io.*;
import java.util.*;

public class FileExchangeServer {

    
    public static void main(String[] args) {
		int nPort = Integer.parseInt(args[0]); // 4000
		System.out.println("Server: Listening on port " + args[0] + "...");
		ServerSocket serverSocket;
		Socket serverEndpoint;

		try 
		{
			serverSocket = new ServerSocket(nPort);
			serverEndpoint = serverSocket.accept();
			
			System.out.println("\nServer: New client connected: " + serverEndpoint.getRemoteSocketAddress());
			
			DataInputStream disReader = new DataInputStream(serverEndpoint.getInputStream());
		
			DataOutputStream dosWriter = new DataOutputStream(serverEndpoint.getOutputStream());	

			FileWriter fileWriter = new FileWriter("Download.txt");
			fileWriter.write("Hello World!");
			fileWriter.close();

            File file = new File("Download.txt"); 
            FileInputStream fis = new FileInputStream(file); 
            
            byte[] buffer = new byte[(int) file.length()]; 
			fis.read(buffer); 

            dosWriter.writeInt(buffer.length); 
            dosWriter.write(buffer, 0, buffer.length);
			System.out.println("\nServer: Sending File \"Download.txt\" (" + buffer.length +" bytes)\n");
            fis.close();












            
			serverEndpoint.close();
            serverSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			System.out.println("Server: Connection is terminated.");
		}
	}
}
