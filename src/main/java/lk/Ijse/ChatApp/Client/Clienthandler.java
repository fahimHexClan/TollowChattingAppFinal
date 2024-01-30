package lk.Ijse.ChatApp.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Clienthandler {
    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private String userName;
    private String sender;

    public static ArrayList<Clienthandler> clients = new ArrayList<>();

    public Clienthandler(Socket socket) {
        try {
            this.socket = socket;
            this.dataInputStream = new DataInputStream(socket.getInputStream());
            this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
            userName = dataInputStream.readUTF();
            clients.add(this);

            listenMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listenMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String type = dataInputStream.readUTF();
                    if (type.equals("Message")) {
                        String message = dataInputStream.readUTF();
                        String[] name = message.split(" : ");
                        sender = name[0];
                        broadcastMessage(message);
                    } else if (type.equals("image")) {
                        receiveImage();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    private void receiveImage() {
        try {
            String size = dataInputStream.readUTF();
            int imageSize = Integer.parseInt(size);

            byte[] blob = new byte[imageSize];
            dataInputStream.readFully(blob);

            broadcastImage(blob);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void broadcastImage(byte[] blob) {
        for (Clienthandler client : clients) {
            try {
                if (!client.userName.equals(sender)) {
                    client.dataOutputStream.writeUTF("image");
                    client.dataOutputStream.writeUTF(blob.length + "");
                    client.dataOutputStream.write(blob);
                    client.dataOutputStream.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void broadcastMessage(String message) {
        for (Clienthandler client : clients) {
            try {
                if (!client.userName.equals(sender)) {
                    client.dataOutputStream.writeUTF("Message");
                    client.dataOutputStream.writeUTF(message);
                    client.dataOutputStream.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
