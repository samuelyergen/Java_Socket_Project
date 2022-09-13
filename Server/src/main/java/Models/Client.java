package Models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private String name;
    private List<String> filenames;
    @JsonIgnore
    private boolean isConnected=false;
    @JsonIgnore
    private Socket socket;
    @JsonIgnore
    private int portForUserConnection;

    public Client(){
        this("Unknown", null);
    }

    public Client(String name, Socket socket){
        this(name, socket,new ArrayList<>());
    }

    public Client(String name, Socket socket, List<String> filenames){
        this.name=name;
        this.socket=socket;
        this.filenames=filenames;
    }

    /**
     * Getter of the server for the other users
     * @return the port
     */
    public int getPortForUserConnection() {
        return portForUserConnection;
    }
    /**
     * Setter of the server for the other users
     * @param portForUserConnection the port
     */
    public void setPortForUserConnection(int portForUserConnection) {
        this.portForUserConnection = portForUserConnection;
    }

    /**
     * Getter of the name of the client
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of the name of the client
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of the list of filenames
     * @return list of filenames
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Setter of the list of filenames
     * @param filenames list of fileNames
     */
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    /**
     * Getter of the socket to the server
     * @return socket client
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Setter of the socket to the server
     * @param socket socket client
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Getter if the client is connected
     * @return true if the client is connected
     */
    public boolean getIsConnected() {
        return this.isConnected;
    }

    /**
     * Setter of the status of the client
     * @param status status of the client
     */
    public void setIsConnected(boolean status){
        this.isConnected=status;
    }

}
