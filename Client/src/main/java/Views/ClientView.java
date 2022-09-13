package Views;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ClientView {

    /**
     * Show an error message using JOptionPane
     * @param text the error to display
     */
    public void showError(String text){
        System.err.println(text);
    }

    /**
     * Show an information message using JOptionPane
     * @param text The information to display
     */
    public void showInformation(String text){
        System.out.println(text);
    }

    /**
     * Get a number from client
     * @return the number
     */
    public int catchNumber(String message) {
        do {
            try {
                System.out.println(message);
                Scanner scan = new Scanner(System.in);
                return scan.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("This is not a number.");
            }
        }while(true);
    }

    /**
     * Get a boolean from the client.
     * @param message Message that will display to the client. The message will have "(Y or N)" added to the end.
     * @return Boolean with the answers
     */
    public boolean getBoolean(String message) {
        message += "(Y or N)";
        System.out.println(message);
        char responseClient;
        boolean response = false;
        boolean goodAnswer;
        Scanner scan = new Scanner(System.in);
        do {
            goodAnswer=true;
            responseClient = scan.nextLine().toLowerCase().charAt(0);
            switch (responseClient) {
                case 'y':
                    response=true;
                    break;
                case 'n':
                    response=false;
                    break;
                default:
                    goodAnswer=false;
            }
        }while(!goodAnswer);
        return response;
    }

    public String catchString(String s) {
        System.out.println(s);
        Scanner scan = new Scanner(System.in);
        return scan.nextLine();
    }

    public char choiceActionForFile(List<Character> listActions) {
        String message="What do you want to do : \n";
        for (char action:listActions) {
            switch (action){
                case 'd':
                    message+="Download (d)\n";
                    break;
                case 's':
                    message+="Stream (s)\n";
                    break;
            }
        }
        message+="Your choice : ";
        Scanner scan = new Scanner(System.in);
        char response;
        boolean isGoodResponse;
        do {
            System.out.print(message);
            response = scan.nextLine().charAt(0);
            if(!(isGoodResponse=listActions.contains(response)))
                System.out.println("This action is not available.");
        }while(!isGoodResponse);
        return response;
    }
}
