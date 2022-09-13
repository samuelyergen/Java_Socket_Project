package Models;

import Controllers.ServerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * This thread will read all the commands of the client
 */
public class WaitCommand implements Runnable {
    static Logger logger = LogManager.getLogger(ServerController.class.getName());

    Server server;
    Client client;

    public WaitCommand(Server server, Client client) {
        this.server = server;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            String command;
            DataInputStream reader;
            while (true) {
                reader = new DataInputStream(this.client.getSocket().getInputStream());
                //Wait for a command from user
                command = reader.readUTF();
                if (command == null)
                    return;
                command = command.toLowerCase();
                logger.info(client.getName() + " runs the command : " + command);
                switch (command) {
                    case "list":
                        server.sendFileNamesList(client);
                        break;
                    case "refresh":
                        server.changeListInListFile(client.getName(), server.getFileListFromClient(client.getSocket()));
                        break;
                    case "getfile":
                        server.getFileForClient(this.client);
                        break;
                }
            }
        } catch (IOException e) {
            client.setIsConnected(false);
            logger.info(client.getName() + " is disconnected.");
        }
    }
}
