package Models;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private final String ROOT_FOLDER = "C:/VsFlix";
    private final String SHARE_FOLDER = ROOT_FOLDER + "/Share";
    private final String DOWNLOAD_FOLDER = ROOT_FOLDER + "/Download";
    public Socket clientSocket;
    public Socket socketToClientServer;
    public String serverIP;
    public ServerSocket serverSocket;
    public List<String> fileList;
    public List<Socket> clientsConnection = new ArrayList<>();
    public int portForUserConnection;
    List<Thread> threadsList = new ArrayList<>();

    public Client() {
        File folderRoot = new File(ROOT_FOLDER);
        if (!folderRoot.exists())
            folderRoot.mkdir();
        File folderShare = new File(SHARE_FOLDER);
        if (!folderShare.exists())
            folderShare.mkdir();
        File folderDownload = new File(DOWNLOAD_FOLDER);
        if(!folderDownload.exists())
            folderDownload.mkdir();
        fileList=new ArrayList<>();
        fileList = getFileList();
    }

    /**
     * getter of ServerSocket
     * @return
     */
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    /**
     * getter of clientSocket
     * @return
     */
    public Socket getClientSocket() {
        return clientSocket;
    }

    /**
     * setter of clientSocket
     * @param clientSocket
     */
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * getter of serverIP
     * @return
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * setter of serverIP
     * @param serverIP
     */
    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    /**
     * The client will ask to the server to give him a list with all available documents
     *
     * @return List with documents name or null if an error occurred.
     */
    public String getDocumentsFromServer() throws IOException {
        DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
        out.writeUTF("List");
        out.flush();
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        String line;
        String message = "";
        while (!(line = inputStream.readLine()).equals("!stop")) {
            message += line + "\n";
        }

        return message;

    }

    /**
     * list the files who are in the share folder
     * @return arraylist of files from the user
     */
    public List<String> getFileList() {
        File f = new File(SHARE_FOLDER);
        String[] l = f.list();
        fileList = new ArrayList<>();
        for (int i = 0; i < l.length; i++) {
            fileList.add(l[i]);
        }
        return fileList;
    }

    /**
     * Give informations of user id and file id
     * @param idUser is the id of the user choosen by the actual user
     * @param idFile is the id of the file choosen by the actual user
     * @return an array of int
     * @throws IOException
     */
    public String askFile(int idUser, int idFile) throws IOException {
        DataOutputStream writer = new DataOutputStream(this.clientSocket.getOutputStream());
        writer.writeUTF("getfile");
        writer.flush();
        int[] ids = new int[]{idUser, idFile};
        ObjectOutputStream outputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        outputStream.writeObject(ids);
        DataInputStream reader = new DataInputStream(this.clientSocket.getInputStream());
        return reader.readUTF();
    }

    /**
     *  used to know if the client accept to stream or download the file
     * @param response is boolean
     * @throws IOException
     */
    public void acceptFile(boolean response) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.clientSocket.getOutputStream());
        objectOutputStream.writeBoolean(response);
        objectOutputStream.flush();
    }

    /**
     * identify if the name of the client is already existing
     * @param name
     * @return a boolean which is the answer of the server
     * @throws IOException
     */
    public boolean identifyToServer(String name) throws IOException {
        //Send name to the server
        PrintWriter writer = new PrintWriter(this.clientSocket.getOutputStream());
        writer.println(name);
        writer.flush();
        //Wait for the response of the server
        ObjectInputStream objectInputStream = new ObjectInputStream(this.clientSocket.getInputStream());
        return objectInputStream.readBoolean();
    }

    /**
     * retrieve the inetAddress of a choosen client
     * @return the InetAddress
     */
    public String getClientInetAddress() {
        try {
            DataInputStream reader = new DataInputStream(this.clientSocket.getInputStream());
            return reader.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * start the serverSocket used by the client
     * to listen other clients
     * @param port is the number of the port choosen by the client
     * @return
     */
    public boolean startServerSocket(int port) {
        try {
            this.portForUserConnection = port;
            this.serverSocket = new ServerSocket(port);
            Thread thread = new Thread(new ConnexionClients(this));
            thread.start();
            threadsList.add(thread);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * send the port choosen by the client to the server
     * @param port
     */
    public void sendPortForUserConnection(int port) {
        try {
            PrintWriter pout = new PrintWriter(clientSocket.getOutputStream());
            pout.write(port);
            pout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * retrieve the number of the port of another client
     * used to connect to the another client
     * @return the number of the port
     */
    public int getClientPort() {
        try {
            DataInputStream dataInputStream = new DataInputStream(this.clientSocket.getInputStream());
            return dataInputStream.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Get an object file of the given id
     *
     * @param idFile Id of the file
     * @return An File object or null
     */
    public File getFile(int idFile) {
        if (idFile >= fileList.size())
            return null;

        File file = new File(SHARE_FOLDER, fileList.get(idFile));
        if (!file.exists())
            return null;
        return file;
    }

    /**
     *  used to ask the choosen file to the choosen client
     * @param address of the stream client
     * @param port of the stream client
     * @param idFile id of the file choosen by the actual client
     * @return an arraylist with choices "download" and "streaming" if video or audio file
     * and only download if other format
     */
    public List<Character> askFileToClient(String address, int port, int idFile) {
        List<Character> availableChoice = new ArrayList<>();
        try {
            this.socketToClientServer = new Socket(address, port);
            DataOutputStream dataOutputStream = new DataOutputStream(this.socketToClientServer.getOutputStream());
            dataOutputStream.writeInt(idFile);
            dataOutputStream.flush();
            DataInputStream reader = new DataInputStream(this.socketToClientServer.getInputStream());
            String typeFile = reader.readUTF();
            availableChoice.add('d');
            if (typeFile.equals("video") || typeFile.equals("audio"))
                availableChoice.add('s');
        } catch (IOException e) {
            e.printStackTrace();
        }
        return availableChoice;
    }

    /**
     * used to send the action choosen by the client
     * @param response is a char (d or s)
     * @throws IOException
     */
    public void sendActionFile(char response) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(this.socketToClientServer.getOutputStream());
        outputStream.writeChar(response);
        outputStream.flush();
    }

    /**
     * used to receive a file
     * @throws IOException
     */
    public void downloadFile() throws IOException {
        DataInputStream dataInputStream = new DataInputStream(this.socketToClientServer.getInputStream());

        long filesize = dataInputStream.readLong();
        String name = dataInputStream.readUTF();

        byte[] mybytearray = new byte[(int)filesize];
        InputStream is = this.socketToClientServer.getInputStream();
            // save in a file
            int current = 0;
            int bytesRead = 0;
            FileOutputStream fos = new FileOutputStream(DOWNLOAD_FOLDER+"\\"+name);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            do {
                bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (current<filesize);

            bos.write(mybytearray, 0, mybytearray.length);
            bos.flush();
    }

    /**
     * use to stream
     * @param file
     * @param connectedSocket
     * @throws IOException
     */
    public void streamVideo(File file, Socket connectedSocket) throws IOException {
        int port;
        for (port = 45000; port <= 46000; port++) {
            try {
                ServerSocket socket = new ServerSocket(port);
                socket.close();
                break;

            } catch (IOException ex) {

            }
        }
        PrintWriter printWriter = new PrintWriter(connectedSocket.getOutputStream());
        StringBuilder sb = new StringBuilder(60);
        sb.append(":sout=#duplicate{dst=std{access=http,mux=ts,");
        sb.append("dst=");
        sb.append(connectedSocket.getInetAddress().getHostAddress());
        sb.append(':');
        sb.append(port);
        sb.append("}}");
        String options = sb.toString();
        printWriter.println("http://" + connectedSocket.getInetAddress().getHostAddress() + ":" + port);
        printWriter.flush();

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        EmbeddedMediaPlayer mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.media().play(file.getAbsolutePath(), options);
    }

    /**
     * retrieve the string needed to stream
     * @return a string
     * @throws IOException
     */
    public String getUrlStream() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socketToClientServer.getInputStream()));
        return bufferedReader.readLine();
    }

    /**
     * used to stop the client program
     */
    public void exit()  {
        try {
            this.clientSocket.close();
            if(this.socketToClientServer != null && !this.socketToClientServer.isClosed())
                this.socketToClientServer.close();
            this.serverSocket.close();
        } catch (IOException e) {
            //Disconnect all socket
        }

    }
}
