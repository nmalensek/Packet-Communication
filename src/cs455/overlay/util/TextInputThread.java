package cs455.overlay.util;

import cs455.overlay.node.Node;

import java.util.Scanner;

public class TextInputThread extends Thread {
    private Node node;

    public TextInputThread(Node node) {
        this.node = node;
    }

    public void run() {
        Scanner userInput = new Scanner(System.in);
        while (userInput.hasNext()) {
            String command = userInput.next();
            System.out.println(command);
        }
    }

    public static String userInput() {
        Scanner input = new Scanner(System.in);
        String command;

        command = input.next();
        return command;
    }
}
