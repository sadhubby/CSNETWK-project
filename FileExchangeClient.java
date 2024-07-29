
import java.net.*;
import java.io.*;
import java.util.*;

public class FileExchangeClient {
    private static String serverAddress;
    private static int port;
    private static Socket clientEndpoint;
    private static String username;
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
                    else{
                        System.out.println("Error: Command parameters do not match or is not allowed.");
                    }  
                }
                else if (inputConnect.startsWith("/?")){
                    System.out.println("----- Here are the commands -----");
                    System.out.println("To join : /join <server_ip_add> <port> ");
                }
                else{
                    System.out.println("Error: Command not found");
                }
            }while(join == 0);
            try{
                DataInputStream in = new DataInputStream(clientEndpoint.getInputStream());
                DataOutputStream out = new DataOutputStream(clientEndpoint.getOutputStream());
                do{ // loop after joining a server.
                    System.out.println("\nPlease enter command:");
                    String inputCommand = sc.nextLine(); 
    
                    // leave function
                    if (inputCommand.startsWith("/leave")){
                        out.writeUTF(inputCommand);
                        System.out.println("Connection closed. Thank you!");
                        clientEndpoint.close();
                        main = 1;
                    }
                    // register function
                    else if (inputCommand.startsWith("/register")){
                        out.writeUTF(inputCommand);
                        System.out.println("Sent registration command: " + inputCommand);
                        String response = in.readUTF();
                        System.out.println(response);
                        username = inputCommand.substring(10).trim();
                    }
                    // store a file into server function
                    else if (inputCommand.startsWith("/store")){
                        String filename = inputCommand.substring(7).trim();
                        File file = new File(filename);
                        if(file.exists()){
                            try{
                                FileInputStream fis = new FileInputStream(file);
                                byte[] buffer = new byte[(int) file.length()];
                                fis.read(buffer);
                                out.writeInt(buffer.length);
                                out.write(buffer);
                                System.out.println(in.readUTF());
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println("Error: File not found.");
                        }
                    }
                    // directory function
                    else if (inputCommand.startsWith("/dir")){
                        out.writeUTF(inputCommand);
                        String dirList = in.readUTF();
                        System.out.println(dirList);
                    }
                    // get function
                    else if (inputCommand.startsWith("/get")){
                        out.writeUTF(inputCommand);
                        String filename = inputCommand.substring(5).trim();
                        int length = in.readInt();
                        if(length > 0){
                            byte[] buffer = new byte[length];
                            in.readFully(buffer);
                            try{
                                FileOutputStream fos = new FileOutputStream(filename);
                                fos.write(buffer);
                                System.out.println("File received from Server: " + filename);
                                fos.close();
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println(in.readUTF());
                        }
                    }
                    // list of commands function
                    else if (inputCommand.startsWith("/?")){
                        System.out.println("----- Here are the commands -----");
                        System.out.println("To leave : /leave");
                        System.out.println("To register user : /register <handle>");
                        System.out.println("To store a file : /store <filename>");
                        System.out.println("To get file list : /dir");
                        System.out.println("To fetch a file : /get <filename>");
                    }
                    else{
                        System.out.println("Error: Command not found");
                    }
                }while(main == 0);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Error: Command parameters do not match or is not allowed.");
        }
    }

    
}


