package Controllers;

import Models.Server;
import Views.ServerView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.InputMismatchException;


public class ServerController {
    static Logger logger = LogManager.getLogger("Server");

    private ServerView view;
    private Server model;

    /**
     * Constructor
     * @param view The view of the program
     * @param model The model of the server
     */
    public ServerController(ServerView view, Server model ){
        this.view=view;
        this.model=model;
        this.view.showInformation("Initialisation of the server");
        try {
            model.getData();
        } catch (IOException e) {
            view.showInformation("Error to load the data !");
        }
        boolean bGetPort=false;
        do{
            try {
                int port = this.view.catchPort();
                if(!isPortAvailable(port))
                    logger.warn("This port is already used.");
                else {
                    bGetPort = true;
                    this.model.setPort(port);
                }
            }
            catch (InputMismatchException e){
                this.view.showInformation("You did not enter a good number of port.");
            }
        }while (!bGetPort);

        try {
            model.start();
        } catch (SocketException e) {
            logger.error("Problem to start the server !");
            return;
        } catch (IOException e) {
            logger.error("The server has been disconnected !");
            return;
        }

        String message;
        do{
            message=this.view.catchCommand();
        }while(!message.equals("exit"));
        try {
            this.model.exit();
        } catch (IOException e) {
            logger.error("A problem occured when the server was closing.");
        }
    }

    /**
     * Test if a port is available
     * @param port The port to be tested
     * @return available or not
     */
    private boolean isPortAvailable(int port) {
        // Assume no connection is possible.
        try {
            (new ServerSocket(port)).close();
            return true;
        }
        catch(Exception e) {
            // Could not connect.
        }
        return false;
    }

}
