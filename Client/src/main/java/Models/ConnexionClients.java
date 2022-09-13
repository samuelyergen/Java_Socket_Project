package Models;

import java.io.IOException;
import java.net.Socket;

public class ConnexionClients implements Runnable{

    private final Client client;

    public ConnexionClients(Client c){
        this.client = c ;
    }

    @Override
    public void run() {
        boolean isClosed=false;
        while (!isClosed){
            try {
                Socket s = client.getServerSocket().accept();
                client.clientsConnection.add(s);
                Thread thread = new Thread(new ClientToCLient(client, s));
                thread.start();
                client.threadsList.add(thread);
            } catch (IOException e) {
                isClosed = this.client.getServerSocket().isClosed();
                if(!isClosed)
                    e.printStackTrace();
            }
        }

    }
}
