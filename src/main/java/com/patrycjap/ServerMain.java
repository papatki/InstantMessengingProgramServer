package com.patrycjap;
import javax.swing.*;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server();
        server.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        server.startRunning();
    }
}
