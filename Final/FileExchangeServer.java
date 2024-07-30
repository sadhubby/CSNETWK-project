import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class FileExchangeServer {
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final Set<String> handles = ConcurrentHashMap.newKeySet();
    private static final String FILE_STORAGE_DIR = "server_storage"; // Directory for storing files

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java FileExchangeServer <server_address> <port>");
            System.exit(1);
        }
        
        String serverAddress = args[0];
        int nPort = Integer.parseInt(args[1]);
        System.out.println("Server: Listening on " + serverAddress + " port " + nPort + "...");
        
        // Create directory if it does not exist
        File storageDir = new File(FILE_STORAGE_DIR);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        
        try (ServerSocket serverSocket = new ServerSocket(nPort, 50, InetAddress.getByName(serverAddress))) {
            while (true) {
                Socket serverEndpoint = serverSocket.accept();
                System.out.println("Server: New client connected: " + serverEndpoint.getRemoteSocketAddress());
                pool.submit(new ClientHandler(serverEndpoint));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
            System.out.println("Server: Connection is terminated.");
        }
    }

    public static boolean addHandle(String username) {
        return handles.add(username);
    }
}

class ClientHandler implements Runnable {
    private Socket serverEndpoint;
    private String username = "Unknown";
    
    public ClientHandler(Socket serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }
    
    public void run() {
        try (DataInputStream in = new DataInputStream(serverEndpoint.getInputStream());
            DataOutputStream out = new DataOutputStream(serverEndpoint.getOutputStream())) {
            
            String command;
            while ((command = in.readUTF()) != null) {
                System.out.println("Received command: " + command);
                String[] parts = command.split(" ", 2);
                
                if (parts.length > 1) {
                    switch (parts[0]) {
                        case "/register":
                            if(parts[1] == null){
                                out.writeUTF("Error: Command not found");
                            }
                            else{
                                handleRegister(parts[1], out);
                            }
                            break;
                        case "/store":
                            handleStore(parts[1], in, out);
                            break;
                        case "/get":
                            handleGet(parts[1], out);
                            break;
                        default:
                            out.writeUTF("Error: Command not recognized");
                            break;
                    }
                } else {
                    switch (parts[0]) {
                        case "/dir":
                            handleDir(out);
                            break;
                        case "/leave":
                            handleLeave(out);
                            return; 
                        case "/?":
                            handleHelp(out);
                            break;
                        default:
                            out.writeUTF("Error: Command parameters do not match or are not allowed");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverEndpoint.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRegister(String newUsername, DataOutputStream out) throws IOException {
        System.out.println("Received registration for " + newUsername);
        
        if (FileExchangeServer.addHandle(newUsername)) {
            username = newUsername;
            out.writeUTF("Welcome " + username + "!");
        } else {
            out.writeUTF("Error: Registration failed. Handle or alias already exists");
        }
        
    }

    private void handleStore(String filename, DataInputStream in, DataOutputStream out) throws IOException {
        File file = new File("server_storage", filename);
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.readFully(buffer);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(buffer);
            out.writeUTF("File stored successfully as " + filename);
        } catch (IOException e) {
            out.writeUTF("Error: File not found.");
        }
    }

    private void handleGet(String filename, DataOutputStream out) throws IOException {
        File file = new File("server_storage", filename);
        if (file.exists() && !file.isDirectory()) {
            long fileSize = file.length();
            out.writeLong(fileSize);
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[(int) fileSize];
                fis.read(buffer);
                out.write(buffer);
            }
        } else {
            out.writeLong(-1);
            out.writeUTF("Error: File not found in the server");
        }
    }

    private void handleDir(DataOutputStream out) throws IOException {
        File dir = new File("server_storage");
        String[] files = dir.list();
        if (files != null) {
            out.writeUTF(String.join(", ", files));
        } else {
            out.writeUTF("Error: Unable to list directory");
        }
    }

    private void handleLeave(DataOutputStream out) throws IOException {
        out.writeUTF("Connection closed. Thank you!");
    }

    private void handleHelp(DataOutputStream out) throws IOException {
        out.writeUTF("---- Here are the commands ----\n"
                + "To join: /join <server_ip_ad> <port>\n"
                + "To leave: /leave\n"
                + "To register: /register <handle>\n"
                + "To store a file: /store <filename>\n"
                + "To get file list: /dir\n"
                + "To fetch a file: /get <filename>\n"
                + "To request command list: /?");
    }
}
