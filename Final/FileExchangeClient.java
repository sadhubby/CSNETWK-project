import java.net.*;
import java.io.*;
import java.util.*;

public class FileExchangeClient {
    private static String serverAddress;
    private static int port;
    private static Socket clientEndpoint;
    private static String username;
    private static final String CLIENT_STORAGE_DIR = "client_storage"; // Directory for storing received files

    public static void main(String[] args) {
        int join = 0;
        int main = 0;
        Scanner sc = new Scanner(System.in);

        // Create directory if it does not exist
        File storageDir = new File(CLIENT_STORAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        try {
            do { // loop for joining a server first
                //maybe try catch here so itll continue to loop? need to check if its in netwk mp kit.
                try{
                System.out.println("\nPlease join a server:");
                String inputConnect = sc.nextLine();
                if (inputConnect.startsWith("/join")) {
                    String[] parts = inputConnect.substring(6).trim().split(" ");
                    if (parts.length == 2) {
                        serverAddress = parts[0];
                        port = Integer.parseInt(parts[1]);
                        clientEndpoint = new Socket(serverAddress, port);
                        System.out.println("Connection is successful!");
                        join = 1;
                    } else {
                        System.out.println("Error: Command parameters do not match or are not allowed.");
                    }
                    
                } else if (inputConnect.startsWith("/?")) {
                    System.out.println("----- Here are the commands -----");
                    System.out.println("To join : /join <server_ip_add> <port>");
                } 
                else if(inputConnect.startsWith("/leave")){
                    System.out.println("Error: Disconnection failed. Please connect to the server first");
                }else {
                    System.out.println("Error: Command not found");
                }
                }catch(IOException e){
                    System.out.println("Error: Connection to the Server has failed! Please check IP Address and Port Number");
                }


            } while (join == 0);

            try (DataInputStream in = new DataInputStream(clientEndpoint.getInputStream());
                DataOutputStream out = new DataOutputStream(clientEndpoint.getOutputStream())) {
                do { // loop after joining a server
                    System.out.println("\nPlease enter command:");
                    String inputCommand = sc.nextLine();
                    // leave function
                    if (inputCommand.startsWith("/leave")) {
                        out.writeUTF(inputCommand);
                        System.out.println("Connection closed. Thank you!");
                        clientEndpoint.close();
                        main = 1;
                    }
                    // register function
                    else if (inputCommand.startsWith("/register")) {
                        //String[] nullcommand = inputCommand.split("", 2);
                        if(inputCommand.equals("/register")){
                            System.out.println("Error: Command not found");
                            continue;
                        }
                        out.writeUTF(inputCommand);
                        String response = in.readUTF();
                        System.out.println(response);
                        username = inputCommand.substring(10).trim();
                        
                    }
                    // store a file into server function
                    else if (inputCommand.startsWith("/store")) {
                        String filename = inputCommand.substring(7).trim();
                        File file = new File(filename);
                        if (file.exists() && !file.isDirectory()) {
                            try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] buffer = new byte[(int) file.length()];
                                fis.read(buffer);
                                out.writeUTF(inputCommand);
                                out.writeInt(buffer.length);
                                out.write(buffer);
                                System.out.println(in.readUTF());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Error: File not found.");
                        }
                    }
                    // directory function
                    else if (inputCommand.startsWith("/dir")) {
                        out.writeUTF(inputCommand);
                        System.out.println("Files in server storage: " + in.readUTF());
                    }
                    // get file from server function
                    else if (inputCommand.startsWith("/get")) {
                        String filename = inputCommand.substring(5).trim();
                        out.writeUTF(inputCommand);
                        long fileSize = in.readLong();
                        if (fileSize >= 0) {
                            byte[] buffer = new byte[(int) fileSize];
                            in.readFully(buffer);
                            File file = new File(CLIENT_STORAGE_DIR, filename);
                            try (FileOutputStream fos = new FileOutputStream(file)) {
                                fos.write(buffer);
                                System.out.println("File received and saved as " + file.getPath());
                            }
                        } else {
                            System.out.println(in.readUTF());
                        }
                    }
                    // help function
                    else if (inputCommand.startsWith("/?")) {
                        out.writeUTF(inputCommand);
                        System.out.println(in.readUTF());
                    }
                    // for invalid inputs
                    else {
                        out.writeUTF(inputCommand);
                        System.out.println(in.readUTF());
                    }
                } while (main == 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
