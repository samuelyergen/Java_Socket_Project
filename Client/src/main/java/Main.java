import Controllers.ClientController;
import Models.Client;
import Views.ClientView;

public class Main {
    public static void main(String args[])throws Exception{
        ClientController clientController = new ClientController(
                new ClientView(),
                new Client());


    }
}
