package Models;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.Socket;

public class ClientToCLient implements Runnable {
    private Client serverClient;
    private Socket connectedSocket;

    public ClientToCLient(Client serverClient, Socket connectedSocket) {
        this.serverClient = serverClient;
        this.connectedSocket = connectedSocket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataReader = new DataInputStream(this.connectedSocket.getInputStream());
            int idFile = dataReader.readInt();

            File file = serverClient.getFile(idFile);
            if (file == null) {
                connectedSocket.close();
                return;
            }
            String fileType;
            String extension = FilenameUtils.getExtension(file.getCanonicalPath());
            switch (extension.toLowerCase()) {
                case "mp4":
                case "mov":
                case "avi":
                case "mkv":
                    fileType = "video";
                    break;
                case "wav":
                case "mp3":
                case "wma":
                    fileType = "audio";
                    break;
                default:
                    fileType = "other";

            }
            DataOutputStream writer = new DataOutputStream(this.connectedSocket.getOutputStream());
            writer.writeUTF(fileType);
            writer.flush();
            char action = dataReader.readChar();
            switch (action) {
                case 'd':
                    downloadFile(file);
                    break;
                case 's':
                    serverClient.streamVideo(file, connectedSocket);
            }

        } catch (IOException e) {

        }
    }

    /**
     * send the file to another client
     * @param file is the file who will be sent
     * @throws IOException
     */
    private void downloadFile(File file) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(this.connectedSocket.getOutputStream());
        dataOutputStream.writeLong(file.length());
        dataOutputStream.flush();
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.flush();
        byte[] mybyteArray = new byte [(int)file.length()];
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybyteArray,0,mybyteArray.length);

        OutputStream os = this.connectedSocket.getOutputStream();
        os.write(mybyteArray, 0, mybyteArray.length);
        os.flush();
    }
}

