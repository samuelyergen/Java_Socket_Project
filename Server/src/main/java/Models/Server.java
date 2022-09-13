package Models;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Model of the server
 */
public class Server {

    static Logger logger = LogManager.getLogger("Server");

    private final String ROOT_FOLDER = "C:/VsFlix";
    private final String FILE_NAME = "filesList.json";
    private int port;
    private int numberConnection;
    private ServerSocket myServerSocket;
    private List<Client> clients;
    private Thread openConnections;

    /**
     * Constructor
     */
    public Server() {
        this.port = 45000;
        this.numberConnection = 10;
    }

    /**
     * This method will get the data. If the data file does not exist it will be created.
     *
     * @throws IOException An IOException that needs to be caught in the controller
     */
    public void getData() throws IOException {
        File file = new File(ROOT_FOLDER, FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        if (file.exists()) {
            clients = mapper.readValue(file, new TypeReference<List<Client>>() {
            });
        } else {
            File dir = new File(ROOT_FOLDER);
            if (!dir.exists())
                dir.mkdir();
            try {
                file.createNewFile();

                clients = new ArrayList<>();
                mapper.writeValue(file, clients);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Setter of the port of the server
     * @param port port of the server
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Getter of the server socker
     * @return server socket
     */
    public ServerSocket getMyServerSocket() {
        return myServerSocket;
    }


    /**
     * Start the server
     */
    public void start() throws IOException {
        this.myServerSocket = new ServerSocket(this.port, this.numberConnection);
        openConnections = new Thread(new AsyncConnexion(this));
        openConnections.start();
    }

    /**
     * We connect to an existing client or create a new one
     * @param nameClient name of the client
     * @param clientSocket Socket of the client
     * @return The connected client
     */
    public Client connectClient(String nameClient, Socket clientSocket, int port) {
        Client client=null;
        boolean bFirstConnection=true;
        for (Client client2 : clients) {
            if (client2.getName().equals(nameClient)) {
                if(client2.getIsConnected())
                    return null;
                client = client2;
                bFirstConnection=false;
            }
        }
        if(bFirstConnection) {
            client = new Client();
            client.setName(nameClient);
        }
        client.setPortForUserConnection(port);
        client.setIsConnected(true);
        client.setSocket(clientSocket);
        if(bFirstConnection)
            addClient(client);
        return client;
    }

    /**
     * Add a new client
     *
     * @param client New client to add to the list
     */
    synchronized public void addClient(Client client) {
        clients.add(client);
        File file = new File(ROOT_FOLDER, FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(file, clients);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create the message to display the list of files
     * @return the message
     * @throws IOException
     */
    public String getFilesNameListMessage() throws IOException {
        if (clients.size() == 0)
            return "There is no client !";
        String filesMessage = "No client is connected or has no file.\n";
        int idx = 1;
        boolean isFirstClient = true;
        for (Client client : clients) {
            if (client.getIsConnected() && client.getFilenames().size() > 0) {
                if (isFirstClient) {
                    filesMessage = "List of Files : \n";
                    isFirstClient = false;
                }
                filesMessage += "\t" + idx + " : " + client.getName() + "\n";

                for (int i = 0; i < client.getFilenames().size(); i++) {
                    filesMessage += "\t\t" + (i + 1) + " : " + client.getFilenames().get(i) + "\n";
                }
                idx++;
            }
        }
        filesMessage+="!stop";
        return filesMessage;
    }

    /**
     * get the list of files from the client
     *
     * @param client Client who we need to refresh the list of files
     * @return List of files
     */
    public List<String> getFileListFromClient(Socket client) throws  IOException{
        try {
            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
            return (List<String>) input.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update the files list of the server
     * @param nameClient client name who update his files list
     * @param fileList the files list
     */
    public void changeListInListFile(String nameClient, List<String> fileList) {
        ObjectMapper om = new ObjectMapper();
        File file = new File(ROOT_FOLDER, FILE_NAME);
        for (Client c : clients) {
            if (c.getName().equals(nameClient)) {
                c.setFilenames(fileList);
                try {
                    om.writeValue(file, clients);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Send the list of files available
     * @param client Client who wants the list of files
     */
    public void sendFileNamesList(Client client) {
        try {
            PrintWriter output = new PrintWriter(client.getSocket().getOutputStream());
            output.println(this.getFilesNameListMessage());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We will find the file that the user want
     * @param client The client that want the file
     * @throws IOException an error occurred to the socket it surely disconnected
     */
    public void getFileForClient(Client client) throws IOException {
        int[] ids;
        try {
            ObjectInputStream objectInput = new ObjectInputStream(client.getSocket().getInputStream());
            ids = (int[]) objectInput.readObject();
            int idClient = ids[0] - 1;  //The id of the client
            int idFile = ids[1] - 1;    //The id of the file
            int idx = 0;
            Client clientServer = null;
            do {
                if (clients.get(idx).getIsConnected() && clients.get(idx).getFilenames().size()>0) {
                    if(idx == idClient)
                        clientServer = clients.get(idx);
                }else
                    idClient++; //We increase the id of the wanted client because we did not calculate the disconnected client in this id
                idx++;
            } while (clientServer == null && idx < clients.size());
            DataOutputStream writer = new DataOutputStream(client.getSocket().getOutputStream());
            //Error when the user is not found
            if (clientServer == null) {
                logger.warn("The client with the id "+idClient+" is unavailable.");
                writer.writeUTF("!badUser");
                writer.flush();
                return;
            }
            //Error when the file is not found
            if(idFile >= clientServer.getFilenames().size()){
                logger.warn("The file with the id "+idFile+" of "+ clientServer.getName()+ " does not exist.");
                writer.writeUTF("!badFile");
                writer.flush();
                return;
            }

            String fileName = clientServer.getFilenames().get(idFile);
            writer.writeUTF("Do you want the file \"" + fileName + "\" from " + clientServer.getName() + " ?");
            writer.flush();

            // We need to create a new objectInput to avoid the problem of "invalid type code: AC"
            objectInput = new ObjectInputStream(client.getSocket().getInputStream());
            boolean response = objectInput.readBoolean();

            if (!response) {
                logger.info(client.getName()+" refused the file.");
                return;
            }
            logger.info(client.getName()+" accepted the file.");
            String s = clientServer.getSocket().getInetAddress().getHostAddress();
            writer.writeUTF(s);
            writer.flush();
            int p = clientServer.getPortForUserConnection();

            writer.writeInt(p);
            writer.flush();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Stop all threads of the server
     */
    public void exit() throws IOException {
        myServerSocket.close();
    }
}
