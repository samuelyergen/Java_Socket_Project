package Models;

import Controllers.ServerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AsyncConnexion implements Runnable{

    static Logger logger = LogManager.getLogger(ServerController.class.getName());

    Server myServer;
    ServerSocket mySocket;

    public AsyncConnexion(Server myServer) {
        this.myServer=myServer;
        this.mySocket=myServer.getMyServerSocket();
    }
    @Override
    public void run() {
        boolean isClose=false;
        do{
            try {
                //Wait a client
                Socket clientSocket = this.mySocket.accept();
                BufferedReader bf = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                boolean clientIsConnected = false;
                Client client = null;
                while(!clientIsConnected) {
                    int port = bf.read();
                    String name = bf.readLine();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                     client = this.myServer.connectClient(name, clientSocket,port);
                     if(client!=null)
                        clientIsConnected=true;
                     objectOutputStream.writeBoolean(clientIsConnected);
                     objectOutputStream.flush();
                }
                logger.info(client.getName() + " is connected.");
                new Thread(new WaitCommand(this.myServer, client)).start();
            }catch (IOException e) {
                if(this.myServer.getMyServerSocket().isClosed())
                    isClose=true;
                else
                    logger.warn("The client had a problem and never finished the connexion.");
            }

        }while(!isClose);
    }
}
