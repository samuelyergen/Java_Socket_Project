package Views;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ServerView  {

    public ServerView(){ }

    /**
     * Show an information message using JOptionPane
     * @param text The information to display
     */
    public void showInformation(String text){
        System.out.println(text);
    }

    /**
     * Send a message to the user to ask for which port to use
     * @return the port given by the user
     */
    public int catchPort() throws InputMismatchException {
        System.out.println("Which port do you want to use ?");
        Scanner scanner=new Scanner(System.in);
        return scanner.nextInt();

    }

    /**
     * Catch the command from the user
     * @return the command of the user in lower case
     */
    public String catchCommand(){
        System.out.println("What do you want to do ?");
        Scanner scanner=new Scanner(System.in);
        return scanner.next().toLowerCase();
    }
}
