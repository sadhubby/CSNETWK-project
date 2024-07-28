
import java.net.*;
import java.io.*;
import java.util.*;

public class FileExchangeClient {
    private static String serverAddress;
    private static int port;
    private static Socket clientEndpoint;
    private static String username;
    private static String filename;
    public static void main(String[] args) {

        int join = 0;
        int main = 0;
        Scanner sc = new Scanner(System.in);
        try{
            do{ //loop for joining a server first
                System.out.println("\nPlease join a server:");
                String inputConnect = sc.nextLine();
                if (inputConnect.startsWith("/join")){
                    String[] parts = inputConnect.substring(6).split(" ");
                    if(parts.length == 2){
                        serverAddress = parts[0];
                        port = Integer.parseInt(parts[1]);
                        clientEndpoint = new Socket(serverAddress, port);
                        System.out.println("Connection is successful!");  
                        join = 1;                                                   
                    }  
                }
                if (inputConnect.startsWith("/?")){
                    System.out.println("----- Here are the commands -----");
                    System.out.println("To join : /join <server_ip_add> <port> ");
                }
            }while(join == 0);

            do{ // loop after joining a server.

                System.out.println("\nPlease enter command:");
                String inputCommand = sc.nextLine(); 

                // leave function
                if (inputCommand.startsWith("/leave")){
                    System.out.println("Connection closed. Thank you!");
                    main = 1;
                    clientEndpoint.close();
                }

                // register function
                if (inputCommand.startsWith("/register")){
                    
                    String[] parts = inputCommand.substring(10).split(" ");
                    if(parts.length == 1){
                        username = parts[0];
                    }
                    System.out.println("Welcome " + username + "!");
                }

                // store a file into server function
                if (inputCommand.startsWith("/store")){
                    String[] parts = inputCommand.substring(7).split(" ");
                    if(parts.length == 1){
                        filename = parts[0];
                    }
                    System.out.println(username + " uploaded " + filename);
                }

                // directory function
                if (inputCommand.startsWith("/dir")){
                    System.out.println("Server directory");
                    // print out array of files 
                }

                // get function
                if (inputCommand.startsWith("/get")){
                    String[] parts = inputCommand.substring(5).split(" ");
                    if(parts.length == 1){
                        filename = parts[0];
                        //check if the filename DOES exist
                    }
                    System.out.println("File received from Server: " + filename);
                }

                // list of commands function
                if (inputCommand.startsWith("/?")){
                    System.out.println("----- Here are the commands -----");
                    System.out.println("To leave : /leave");
                    System.out.println("To register user : /register <handle>");
                    System.out.println("To store a file : /store <filename>");
                    System.out.println("To get file list : /dir");
                    System.out.println("To fetch a file : /get <filename>");
                }
            }while(main == 0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    
}


