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
        while (userInput.hasNextLine()) {
            String command = userInput.nextLine();
            node.processText(command);
        }
    }
}
