package Controllers;

import Models.Client;
import Views.ClientVideo;
import Views.ClientView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class ClientController {
    private final ClientView view;
    private final Client model;

    /**
     * Constructor
     *
     * @param view  The view
     * @param model the model Client
     */
    public ClientController(ClientView view, Client model) {
        this.view = view;
        this.model = model;
        String command = "";
        do {
            try {
                setServerAddress();
                do {
                    command = view.catchString(
                            "To have a list of all commands, use help. \n" +
                                    "What do you want to do ?");
                    switch (command) {
                        case "list":
                            GetAllDocumentsFromServer();
                            break;
                        case "refresh":
                            listToServer(model.getFileList());
                            break;
                        case "getfile":
                            getFile();
                            break;
                        case "help":
                            helpCommand();
                            break;
                        case "exit":
                            model.exit();
                            break;
                        default:
                            view.showError("This is not a command.");
                    }
                } while (!command.equals("exit"));
            } catch (IOException e) {
               view.showInformation("The server was disconnected !");
            }
        } while (!command.equals("exit"));
    }

    /**
     * retrieve the ids of the user and file
     * show a message if the informations are incorrect
     * give choice between download and stream to the user
     * @throws IOException
     */
    private void getFile() throws IOException {
        int idUser = view.catchNumber("Which user do you want the file : ");
        int idFile = view.catchNumber("Which file : ");

        String message = model.askFile(idUser, idFile);

        if (message.charAt(0) == '!') {
            switch (message) {
                case "!badUser":
                    view.showInformation("The user id you give is not valid !");
                    break;
                case "!badFile":
                    view.showInformation("The file id you give is not valid !");
                    break;
            }
            return;
        }

        boolean result = view.getBoolean(message);
        model.acceptFile(result);
        if (!result)
            return;
        char action = view.choiceActionForFile(model.askFileToClient(model.getClientInetAddress(), model.getClientPort(), idFile - 1));
        model.sendActionFile(action);

        switch (action) {
            case 'd':
                model.downloadFile();
                break;
            case 's':
                String url = model.getUrlStream();
                ClientVideo viewVideo = new ClientVideo(url);
                break;
        }


    }

    /**
     * The client will ask to the server to give him a list with all available documents
     * @throws IOException
     */
    private void GetAllDocumentsFromServer() throws IOException {
        view.showInformation(model.getDocumentsFromServer());
    }

    /**
     * Send a list of commands with explanation
     */
    public void helpCommand() {
        String message = "There is a list of the command you can do :\n" +
                "list : Display a list of all the files in the server and ask to the client which file he wants,\n" +
                "refresh : Refresh the list of all your file\n"+
                "getfile : download or stream a file\n"+
                "exit : quit the app\n";
        view.showInformation(message);
    }

    /**
     * set the socket with the server's IP
     * and the server's port
     * retrieve the user's name
     */
    public void setServerAddress() {
        boolean isConnectedToServer = false;
        boolean isIdentifiedToServer;
        boolean isPortClientAvalaibles;
        do {
            try {
                model.setServerIP(view.catchString("enter the IP address of the server : "));
                int port = view.catchNumber("enter the port of the server : ");
                model.setClientSocket(new Socket(model.getServerIP(), port));
                isConnectedToServer = true;
                int portForUser;
                do {
                    portForUser = view.catchNumber("Enter the port you want to use to connect with other users : ");
                    isPortClientAvalaibles = model.startServerSocket(portForUser);
                    if(!isPortClientAvalaibles)
                        view.showInformation("This port is already used.");
                } while (!isPortClientAvalaibles);
                model.sendPortForUserConnection(portForUser);
                do {
                    String name = view.catchString("What is your username ?");
                    isIdentifiedToServer = model.identifyToServer(name);
                    if (!isIdentifiedToServer)
                        view.showInformation("A user is already connected with this username !");
                } while (!isIdentifiedToServer);
            } catch (UnknownHostException e) {
                view.showInformation("The address is not in the good format. Try an IP address(192.168.1.2) or domain name(www.example.com).");
            } catch (IOException e) {
                view.showInformation("The client could not connect to the server. Verify the given address and port.");
            }
        } while (!isConnectedToServer);
        view.showInformation("You are connected !");
    }

    /**
     * send the list of the files to the server
     *
     * @param list List of files name
     */
    public void listToServer(List<String> list) throws IOException {
        DataOutputStream out = new DataOutputStream(model.getClientSocket().getOutputStream());
        out.writeUTF("refresh");
        out.flush();
        ObjectOutputStream oop = new ObjectOutputStream(model.getClientSocket().getOutputStream());
        oop.writeObject(list);
        oop.flush();
    }

}
