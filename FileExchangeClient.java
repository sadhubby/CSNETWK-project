
import java.net.*;
import java.io.*;
import java.util.*;

public class FileExchangeClient {
    private static String serverAddress;
    private static int port;
    private static Socket clientEndpoint;
    public static void main(String[] args) {

        int join = 0;
        int main = 0;
        Scanner sc = new Scanner(System.in);
        try{
            do{
                System.out.println("\nPlease join a server:");
                String inputConnect = sc.nextLine();
                if (inputConnect.startsWith("/join")){
                    String[] parts = inputConnect.substring(6).split(" ");
                    if(parts.length == 2){
                        serverAddress = parts[0];
                        port = Integer.parseInt(parts[1]);
                        //clientEndpoint = new Socket(serverAddress, port);
                        System.out.println("Connection to the File Exchange\r" + 
                                                        "Server is successful!");     
                        join = 1;                                                   
                    }  
                    
                }
                if (inputConnect.startsWith("/?")){
                    System.out.println("----- Here are the commands -----");
                    System.out.println("To join : /join <server_ip_add> <port> ");
                }
            }while(join == 0);

            do{
                System.out.println("\nPlease enter command:");
                String inputCommand = sc.nextLine(); 
                if (inputCommand.startsWith("/leave")){
                    System.out.println("Thank you for using this File Exchange program");
                    main = 1;
                    //clientEndpoint.close();
                }
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


