import Controllers.ServerController;
import Models.Server;
import Views.ServerView;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
       ServerController srvController= new ServerController(
                new ServerView(),
                new Server( ));

    }
}
