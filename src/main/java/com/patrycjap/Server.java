package com.patrycjap;

import javax.swing.*;
import java.awt.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends JFrame {

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ServerSocket serverSocket;
    private Socket connection;

    public Server() {
        super("Talk2Me - Server");
        chatWindow = new JTextArea();
        chatWindow.setBackground(Color.lightGray);
        add(new JScrollPane(chatWindow));
        userText = new JTextField();
        userText.setBackground(Color.white);
        userText.setEditable(false);
        userText.addActionListener(e -> {
            sendMessage(e.getActionCommand());
            userText.setText("");
        });
        add(userText, BorderLayout.SOUTH);
        setSize(300, 300);
        setVisible(true);
    }

    public void startRunning() {
        try {
            serverSocket = new ServerSocket(6789, 100);
            while (true) {
                try {
                    waitForConnection();
                    setupStreams();
                    whileChatting();

                } catch (EOFException e) {
                    showMessage("\n Server ended the connection.");
                } finally {
                    closeChat();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeChat() {
        showMessage("\nClosing connection... \n");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        showMessage("\nWaiting for someone to connect... \n");
        connection = serverSocket.accept();
        showMessage("Now connected to: " + connection.getInetAddress().getHostName()+ ".");
    }

    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
    }

    private void whileChatting() throws IOException {
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\nError!");
            }
        } while (!message.equals("CLIENT:  END"));
    }

    private void ableToType(final boolean trueOrFalse) {
        SwingUtilities.invokeLater(() -> userText.setEditable(trueOrFalse));
    }

    private void showMessage(final String text) {
        SwingUtilities.invokeLater(() -> chatWindow.append(text));
    }

    private void sendMessage(String message) {
        try {
            outputStream.writeObject("SERVER:  " + message);
            outputStream.flush();
            showMessage("\nSERVER:  " + message);
        } catch (IOException e) {
            chatWindow.append("\nError: I can't send that message.");
        }
    }
}
